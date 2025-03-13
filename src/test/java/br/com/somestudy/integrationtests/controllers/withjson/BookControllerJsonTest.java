package br.com.somestudy.integrationtests.controllers.withjson;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.somestudy.config.TestConfigs;
import br.com.somestudy.integrationtests.dto.AccountCredentialsDTO;
import br.com.somestudy.integrationtests.dto.BookDTO;
import br.com.somestudy.integrationtests.dto.TokenDTO;
import br.com.somestudy.integrationtests.testcontainers.AbstractIntegrationTest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookControllerJsonTest extends AbstractIntegrationTest{

	private static RequestSpecification specification;
	private static ObjectMapper objectMapper;
	
	private static BookDTO book;
	private static TokenDTO tokenDto;
	
	@BeforeAll
	static void setUp() {
		objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		
		book = new BookDTO();
		tokenDto = new TokenDTO();
	}
	
	
	@Test
	@Order(0)
	void signin() {
	
		AccountCredentialsDTO credentials =  new AccountCredentialsDTO("root", "admin123");
		
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
		specification = new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCAL)
				.addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenDto.getAccessToken())
				.setBasePath("/api/book/v1")
				.setPort(TestConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
		
		assertNotNull(tokenDto.getAccessToken());
		assertNotNull(tokenDto.getRefreshToken());
	}
	
	@Test
	@Order(1)
	void createTest() throws JsonProcessingException {
	    book = new BookDTO(); // Ensure test isolation
	    mockBook();

	    String responseBody = given()
	            .spec(specification)
	            .contentType(MediaType.APPLICATION_JSON_VALUE)
	            .body(book)
	            .when()
	            .post()
	            .then()
	            .statusCode(200)
	            .contentType(MediaType.APPLICATION_JSON_VALUE)
	            .extract()
	            .body()
	            .asString();

	    BookDTO createdBook = objectMapper.readValue(responseBody, BookDTO.class);
	    book = createdBook;

	    assertNotNull(createdBook.getId(), "Created book should have an ID");
	    assertNotNull(book.getId(), "Book should have an ID");
	    assertEquals("Docker Deep Dive", book.getTitle(), "Book title should match");
	    assertEquals("Nigel Poulton", book.getAuthor(), "Book author should match"); // Corrected author name
	    assertEquals(55.99, book.getPrice(), "Book price should match");
	
		
	}
	
	
	private void mockBook() {
		book.setTitle("Docker Deep Dive");
		book.setAuthor("Nigel Poulton");
		book.setPrice(Double.valueOf(55.99));
		book.setLaunchDate(new Date());
		
	}
	
}
