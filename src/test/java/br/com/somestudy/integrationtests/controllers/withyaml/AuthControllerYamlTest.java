package br.com.somestudy.integrationtests.controllers.withyaml;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;

import br.com.somestudy.config.TestConfigs;
import br.com.somestudy.integrationtests.controllers.withyaml.mapper.YAMLMapper;
import br.com.somestudy.integrationtests.dto.AccountCredentialsDTO;
import br.com.somestudy.integrationtests.dto.TokenDTO;
import br.com.somestudy.integrationtests.testcontainers.AbstractIntegrationTest;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerYamlTest extends AbstractIntegrationTest{
	
	/*
	 * This Class is an integration test designed to verify the functionality 
	 * of an authentication controller using YAML for request and response
	 * payloads.
	 * 
	 *  Tests the "auth/signin" and the "/auth/refresh" endpoints 
	 *  of the API, with YAML as the content type;
	 */
	
	
	//Basically a mockage but not a proper Mock object, the tokenDTO stores
	//the authentication token 
	private static TokenDTO tokenDto;
	
	//Instance of the YAML Mapper for serialization and deserialization
	//
	private static YAMLMapper objectMapper;
	
	@BeforeAll
	static void setup() {
		objectMapper = new YAMLMapper();
		
		tokenDto = new TokenDTO();
	}
	
	@Test
	@Order(1)
	void signin() throws JsonProcessingException{
		AccountCredentialsDTO credentials = new AccountCredentialsDTO("root", "admin123");
		
		tokenDto = given()
				.config(
					RestAssuredConfig.config()
					.encoderConfig(
					EncoderConfig.encoderConfig()
					.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT))
				)
				.basePath("/auth/signin/")
				.port(TestConfigs.SERVER_PORT)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.accept(MediaType.APPLICATION_YAML_VALUE)
				.body(credentials,objectMapper)
				.when()
				.put("{username}")
				.then()
				.statusCode(200)
				.extract()
				.body()
				.as(TokenDTO.class, objectMapper);
		
		assertNotNull(tokenDto.getAccessToken());
		assertNotNull(tokenDto.getRefreshToken());
				
	}

}
