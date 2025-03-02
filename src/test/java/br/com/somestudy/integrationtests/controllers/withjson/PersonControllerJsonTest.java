package br.com.somestudy.integrationtests.controllers.withjson;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

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
import br.com.somestudy.integrationtests.dto.PersonDTO;
import br.com.somestudy.integrationtests.dto.TokenDTO;
import br.com.somestudy.integrationtests.testcontainers.AbstractIntegrationTest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonControllerJsonTest extends AbstractIntegrationTest {

	private static RequestSpecification specification;
	private static ObjectMapper objectMapper;
	private static PersonDTO person;
	private static TokenDTO tokenDto;

	@BeforeAll
	static void setUp() {
		objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

		person = new PersonDTO();
		tokenDto = new TokenDTO();
	}

	@Test
	@Order(0)
	void signin() {
		AccountCredentialsDTO credentials = new AccountCredentialsDTO("user", "admin123");
	
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
			.setBasePath("/api/person/v1")
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
			mockPerson();
			
			var content = given(specification)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.body(person)
					.when()
					.post()
					.then()
					.statusCode(200)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.extract()
					.body()
					.asString();
			
			PersonDTO createdPerson = objectMapper.readValue(content, PersonDTO.class);
			person = createdPerson;
			
			assertNotNull(createdPerson.getId());
			assertTrue(createdPerson.getId()> 0);
			
			assertEquals("Linus", createdPerson.getFirstName());
			assertEquals("Torvalds", createdPerson.getLastName());
			assertEquals("Helsinki - Finland", createdPerson.getAddress());
			assertEquals("Male", createdPerson.getGender());
			assertTrue(createdPerson.getEnabled());
		}
	@Test
	@Order(2)
	void updateTest() throws JsonProcessingException{
		person.setLastName("Benedict Torvalds");
		
		var content = given(specification)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(person)
				.when()
				.put()
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.extract()
				.body()
				.asString();
		
		PersonDTO createdPerson = objectMapper.readValue(content, PersonDTO.class);
		person = createdPerson;
		
		assertNotNull(createdPerson.getId());
		assertTrue(createdPerson.getId()>0);
		
		assertEquals("Linus", createdPerson.getFirstName());
		assertEquals("Torvalds", createdPerson.getLastName());
		assertEquals("Helsinki - Finland ", createdPerson.getAddress());
		assertEquals("Male", createdPerson.getGender());
		assertTrue(createdPerson.getEnabled());
		
	}
	@Test
	@Order(3)
	void findByIdTest() throws JsonProcessingException {
		var content = given(specification)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.pathParam("id", person.getId())
				.when()
				.get("id")
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.extract()
				.body()
				.asString();
		
		PersonDTO createdPerson = objectMapper.readValue(content, PersonDTO.class);
		person = createdPerson;
		
		assertNotNull(createdPerson.getId());
		assertNotNull(createdPerson.getId()>0);
		
		assertEquals("Linus", createdPerson.getFirstName());
        assertEquals("Benedict Torvalds", createdPerson.getLastName());
        assertEquals("Helsinki - Finland", createdPerson.getAddress());
        assertEquals("Male", createdPerson.getGender());
        assertTrue(createdPerson.getEnabled());
	}
	@Test
	@Order(4)
	void disabledTest() throws JsonProcessingException{
		
		var content = given(specification)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.pathParam("id", person.getId())
				.when()
				.patch("{id}")
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.extract()
				.body()
				.asString();
		
		PersonDTO createdPerson = objectMapper.readValue(content,PersonDTO.class);
		person = createdPerson; 
		
		assertNotNull(createdPerson.getId());
		assertTrue(createdPerson.getId()>0);
		
		assertEquals("Linus", createdPerson.getFirstName());
		assertEquals("Benedict Torvalds", createdPerson.getLastName());
		assertEquals("Helsinki - Finland" , createdPerson.getAddress());
		assertEquals("Male", createdPerson.getGender());
		assertFalse(createdPerson.getEnabled());
				
	}
	@Test
	@Order(5)
	void deleteTest() throws JsonProcessingException{
		
		given(specification)
		.pathParam("id", person.getId())
		.when()
		.delete("{id}")
		.then()
		.statusCode(2004);
	}
	
	@Test
	@Order(6)
	void findAllTest() throws JsonProcessingException{
	
		var content = given(specification)
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.queryParams("page", 3, "size", 12, "direction", "asc")
			.when()
			.get()
			.then()
			.statusCode(200)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.extract()
			.body()
			.asString();
			
		WrapperPersonDTO wrapper = objectMapper.readValue(content, WrapperPersonDTO.class);
	
	}
	@Test
	@Order(7)
	void findByNameTest() throws JsonProcessingException{
		var content = given(specification)
		.accept(MediaType.APPLICATION_JSON_VALUE)
		.pathParams("firstName", "and")
		.queryParams("page", 0, "size", 12, "direction", "asc")
		.when()
		.get("findPeopleByName/{firstName}")
		.then()
		.statusCode(200)
		.contentType(MediaType.APPLICATION_JSON_VALUE)
		.extract()
		.body()
		.asString();	
		
		WrapperPersonDTO wrapper = objectWrapper.readValue(content, WrapperPersonDTO.class);
		List<PersonDTO> people = wrapper.getEmbedded().getPeople();
	
		PersonDTO personOne = people.get(0);
		
		assertEquals("Alessandro", personOne.getFirstName());
        assertEquals("McFaul", personOne.getLastName());
        assertEquals("5 Lukken Plaza", personOne.getAddress());
        assertEquals("Male", personOne.getGender());
        assertTrue(personOne.getEnabled());

        PersonDTO personFour = people.get(4);

        assertNotNull(personFour.getId());
        assertTrue(personFour.getId() > 0);

        assertEquals("Brandyn", personFour.getFirstName());
        assertEquals("Grasha", personFour.getLastName());
        assertEquals("96 Mosinee Parkway", personFour.getAddress());
        assertEquals("Male", personFour.getGender());
        assertTrue(personFour.getEnabled());
	}
	
	void hateoasAndHalTest() throws JsonProcessingException{
	
		Response response = (Response) given(specification)
		.accept(MediaType.APPLICATION_JSON_VALUE)
		.queryParams("page", 3, "size", 12, "direction", "asc")
		.when()
		.get()
		.then()
		.statusCode(200)
		.contentType(MediaType.APPLICATION_JSON_VALUE)
		.extract()
		.body();
		
		String json = response.getBody().asString();
		List<Map<String, Object>> people  = response.jsonPath().getList("_embedded.people");
		
		for(Map<String, Object> person : people){
			Map<String, Object> links =(Map<String, Object>) person.get("_links");
			
			assertThat("HATEOAS/HAS link 'self' is missing", links, hasKey("self"));
			assertThat("HATEOAS/HAS link 'findAll' is missing", links, hasKey("findAll"));
			assertThat("HATEOAS/HAS link 'findByName' is missing", links, hasKey("findByName"));
			assertThat("HATEOAS/HAS link 'create' is missing", links, hasKey("create"));
			assertThat("HATEOAS/HAS link 'update' is missing", links, hasKey("update"));
			assertThat("HATEOAS/HAS link 'delete' is missing", links, hasKey("delete"));
			assertThat("HATEOAS/HAS link 'disable' is missing", links, hasKey("disable"));
			assertThat("HATEOAS/HAS link 'massCreation' is missing", links, hasKey("massCreation"));
			assertThat("HATEOAS/HAS link 'exportPage' is missing", links, hasKey("exportPage"));
			
			links.forEach((key, value) -> {
				String href=((Map<String, String>) value).get("href");
				assertThat("HATEOAS/HAL link " + key + "has an invalid URL", href, matchesPattern("https?://.+/api/person/v1.*") );
				assertThat("HATEOAS/HAL link " + key + "has an invalid HTTP method", ((Map<String, String>) value).get("type"), notNullValue());
			});	
		}	
	
}
