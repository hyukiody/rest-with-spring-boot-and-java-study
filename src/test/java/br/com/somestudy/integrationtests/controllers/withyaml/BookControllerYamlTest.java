package br.com.somestudy.integrationtests.controllers.withyaml;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;

import br.com.somestudy.config.TestConfigs;
import br.com.somestudy.integrationtests.controllers.withyaml.mapper.YAMLMapper;
import br.com.somestudy.integrationtests.dto.AccountCredentialsDTO;
import br.com.somestudy.integrationtests.dto.BookDTO;
import br.com.somestudy.integrationtests.dto.TokenDTO;
import br.com.somestudy.integrationtests.dto.wrappers.xmlandyaml.PagedModelBook;
import br.com.somestudy.integrationtests.testcontainers.AbstractIntegrationTest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

@Nested
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookControllerYamlTest extends AbstractIntegrationTest {
	/*
	 * This class is the integration test designed to assert the
	 * functionality of the CRUD operations for the Book Controller 
	 * type methods; note that instead of specifying the different 
	 * status codes like the Main packages Controllers class, we only
	 * need to verify the functionality of the status code 200, which is
	 * the success status code.
	 *  
	 * Also the entities used in these tests are only of the DTO
	 * class type, since the proper API controller methods only 
	 * negotiates with DTO data types, there are no proper Java 
	 * Entities being tested.
	 * 
	 * 
	 * 
	 * 
	 */
	private static RequestSpecification specification;
	private static YAMLMapper objectMapper;
	
	private static BookDTO book;
	private static TokenDTO tokenDto;
	
	@BeforeAll
	static void setUp() {
		objectMapper = new YAMLMapper();
		book = new BookDTO();
		tokenDto = new TokenDTO();
	}
	@Test
	@Order(0)
	void signin() throws JsonProcessingException{
		//creating an user as the root user for the integration test
		AccountCredentialsDTO credentials = 
				new AccountCredentialsDTO("root", "admin123");
		
		/*
		 * RestAssured methods for a POST request extracting the 
		 * response of the serialized TokenDTO, then deserializing 
		 * it using the YAMLMapper object class instantiated. 
		 * 
		 * These methods sets the Header with the Destination 
		 * and the Origin addresses of the POST request, already taking into
		 * account the response that has to come after it with
		 * the authentication token into its body, that is deserialized
		 * with the static YAMLMapper class object
		 */
		tokenDto = given()
				.config(
						RestAssuredConfig.config()
						.encoderConfig(
								EncoderConfig.encoderConfig()
								.encodeContentTypeAs(
										MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT))
						)
				.basePath("/auth/signin")
				.port(TestConfigs.SERVER_PORT)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.accept(MediaType.APPLICATION_YAML_VALUE)
				.body(credentials, objectMapper)
				.when().post()
				.then().statusCode(200)
				.extract().body()
				.as(TokenDTO.class, objectMapper);
		
		/*
		 * 
		 */
		
		specification = new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCAL)
				.addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenDto.getRefreshToken())
				.setBasePath("/api/book/v1")
				.setPort(TestConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
		
		assertNotNull(tokenDto.getAccessToken());
		assertNotNull(tokenDto.getRefreshToken());
	}
	@Test
	@Order(2)
	void createTest() throws JsonProcessingException{
		mockBook();
			
			var createdBook = given()
					.config(
							RestAssuredConfig.config()
							.encoderConfig(
									EncoderConfig.encoderConfig()
									.encodeContentTypeAs(
											MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT))
					).spec(specification)
					.contentType(MediaType.APPLICATION_YAML_VALUE)
					.accept(MediaType.APPLICATION_YAML_VALUE)
					.body(book, objectMapper)
					.when()
					.post()
					.then()
					.statusCode(200)
					.contentType(MediaType.APPLICATION_YAML_VALUE)
					.extract()
					.body()
					.as(BookDTO.class, objectMapper);
			
			book = createdBook;
			
			assertNotNull(createdBook.getId());
	        assertNotNull(book.getId());
	        assertEquals("Docker Deep Dive", book.getTitle());
	        assertEquals("Nigel Poulton", book.getAuthor());
	        assertEquals(55.99, book.getPrice());
					
		}
	@Test
	@Order(2)
	void updateTest() throws JsonProcessingException{
		
		book.setTitle("Docker Deep Dive - Updated");
		
		var createdBook = given().config(
				RestAssuredConfig.config()
				.encoderConfig(
						EncoderConfig.encoderConfig()
						.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT))
				).spec(specification)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.accept(MediaType.APPLICATION_YAML_VALUE)
				.body(book, objectMapper)
				.when()
				.put()
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.extract()
				.body()
				.as(BookDTO.class, objectMapper);
		
		book = createdBook;

        assertNotNull(createdBook.getId());
        assertNotNull(book.getId());
        assertEquals("Docker Deep Dive - Updated", book.getTitle());
        assertEquals("Nigel Poulton", book.getAuthor());
        assertEquals(55.99, book.getPrice());
		
	}
	
	@Test
	@Order(3)
	void findByIdTest() throws JsonProcessingException{
		//creates the YAML mediaType RestAssured request encoded as text
		//because our YAMLMapper works with text type body convertion
		var createdBook = given().config(
				RestAssuredConfig.config()
					.encoderConfig(
							EncoderConfig.encoderConfig()
							.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT))
		//continues the setup of the request, using the already
		//instantiated specification, which is the reusable
		//request specification
				).spec(specification)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.accept(MediaType.APPLICATION_YAML_VALUE)
				.pathParam("id", book.getId())
				.when()
				.get("{id}")
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.extract()
				.body()
		//returning the body of the response and converts it to
		//a bookDto object using the YAMLMapper
				.as(BookDTO.class, objectMapper);
		
		book = createdBook;

        assertNotNull(createdBook.getId());
        assertTrue(createdBook.getId() > 0);
        assertNotNull(createdBook.getId());
        assertNotNull(book.getId());
        assertEquals("Docker Deep Dive - Updated", book.getTitle());
        assertEquals("Nigel Poulton", book.getAuthor());
        assertEquals(55.99, book.getPrice());
		
	}
	
	@Test
	@Order(4)
	void deleteTest() throws JsonProcessingException{
		
		given(specification)
		.pathParam("id", book.getId())
		.when()
		.delete("{id}")
		.then()
		.statusCode(204);
	}
	
	@Test
	@Order(5)
	void findAllTest() throws JsonProcessingException{
		//using the authorization response specifications
		//sets a GET request to retrieve a Paged List of BookDTO objects
		//then converts the content into a list through the
		//YAMLMapper
		var response = given(specification)
				.accept(MediaType.APPLICATION_YAML_VALUE)
				.queryParams("page", 9 , "size" , 12 , "direction", "asc")
				.when()
				.get()
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.extract()
				.body()
				.as(PagedModelBook.class, objectMapper);
		
		 	List<BookDTO> content = response.getContent();

	        BookDTO bookOne = content.get(0);

	        assertNotNull(bookOne.getId());
	        assertNotNull(bookOne.getTitle());
	        assertNotNull(bookOne.getAuthor());
	        assertNotNull(bookOne.getPrice());
	        assertTrue(bookOne.getId() > 0);
	        assertEquals("The Art of Agile Development", bookOne.getTitle());
	        assertEquals("James Shore e Shane Warden", bookOne.getAuthor());
	        assertEquals(97.21, bookOne.getPrice());

	        BookDTO foundBookSeven = content.get(7);

	        assertNotNull(foundBookSeven.getId());
	        assertNotNull(foundBookSeven.getTitle());
	        assertNotNull(foundBookSeven.getAuthor());
	        assertNotNull(foundBookSeven.getPrice());
	        assertTrue(foundBookSeven.getId() > 0);
	        assertEquals("The Art of Computer Programming, Volume 1: Fundamental Algorithms", foundBookSeven.getTitle());
	        assertEquals("Donald E. Knuth", foundBookSeven.getAuthor());
	        assertEquals(139.69, foundBookSeven.getPrice());
	}
	private void mockBook() {
        book.setTitle("Docker Deep Dive");
        book.setAuthor("Nigel Poulton");
        book.setPrice(Double.valueOf(55.99));
        book.setLaunchDate(new Date());
    }
}


