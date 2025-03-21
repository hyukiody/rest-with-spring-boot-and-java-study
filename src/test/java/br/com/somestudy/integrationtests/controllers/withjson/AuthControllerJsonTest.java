package br.com.somestudy.integrationtests.controllers.withjson;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static io.restassured.RestAssured.given;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import br.com.somestudy.config.TestConfigs;
import br.com.somestudy.integrationtests.dto.AccountCredentialsDTO;
import br.com.somestudy.integrationtests.dto.TokenDTO;
import br.com.somestudy.integrationtests.testcontainers.AbstractIntegrationTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerJsonTest extends AbstractIntegrationTest{
	private static TokenDTO tokenDto;
	
	@BeforeAll
	static void setUp() {
		tokenDto = new TokenDTO();
		
	}
	@Test
	@Order(1)
	void signin() {
		AccountCredentialsDTO credentials = new AccountCredentialsDTO("root", "admin123");
		
		tokenDto = given()
			.basePath("/auth/signin")
				.port(TestConfigs.SERVER_PORT)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(credentials)
				.when()
				.post()
				.then()
				.statusCode(200)
				.extract()
				.body()
				.as(TokenDTO.class);
		assertNotNull(tokenDto.getAccessToken());
		assertNotNull(tokenDto.getRefreshToken());
	}
	
	@Test
	@Order(2)
	void refreshToken() {
		
		tokenDto = given()
				.basePath("/auth/refresh")
				.port(TestConfigs.SERVER_PORT)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.pathParam("username", tokenDto.getUsername())
				.header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer" + tokenDto.getRefreshToken())
				.when()
				.put("{username}")
				.then()
				.statusCode(200)
				.extract()
				.body()
				.as(TokenDTO.class);
				
		
	}
	
	

}
