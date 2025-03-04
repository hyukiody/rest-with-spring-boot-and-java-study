package br.com.somestudy.integrationtests.controllers.withxml;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static io.restassured.RestAssured.given;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import br.com.somestudy.config.TestConfigs;
import br.com.somestudy.integrationtests.dto.AccountCredentialsDTO;
import br.com.somestudy.integrationtests.dto.TokenDTO;
import br.com.somestudy.integrationtests.testcontainers.AbstractIntegrationTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerXmlTest extends AbstractIntegrationTest {

	private static TokenDTO tokenDto;
	private static XmlMapper objectMapper;
	
	@BeforeAll
	static void setUp() {
		objectMapper = new XmlMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}
	
	void signin() throws JsonProcessingException{
		AccountCredentialsDTO credentials = new AccountCredentialsDTO("root", "admin123");
		
		var content = given()
				.basePath("/auth/signin")
				.port(TestConfigs.SERVER_PORT)
				.contentType(MediaType.APPLICATION_XML_VALUE)
				.accept(MediaType.APPLICATION_XML_VALUE)
				.body(credentials)
				.when()
				.post()
				.then()
				.statusCode(200)
				.extract()
				.body()
				.asString();
		
		tokenDto = objectMapper.readValue(content, TokenDTO.class);
		
		assertNotNull(tokenDto.getAccessToken());
		assertNotNull(tokenDto.getRefreshToken());
	}
	
	void refreshToken() throws JsonProcessingException{
		
		var content = given()
				.basePath("/auth/refresh")
				.port(TestConfigs.SERVER_PORT)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON_VALUE)
				.pathParam("root", tokenDto.getUsername())
				.header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenDto.getRefreshToken())
				.when()
				.put("username")
				.then()
				.statusCode(200)
				.extract()
				.body()
				.asString();
				
		tokenDto = objectMapper.readValue(content, TokenDTO.class);
		
		assertNotNull(tokenDto.getAccessToken());
		assertNotNull(tokenDto.getRefreshToken());
	}
	
}
