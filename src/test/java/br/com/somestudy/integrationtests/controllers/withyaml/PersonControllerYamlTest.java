package br.com.somestudy.integrationtests.controllers.withyaml;

import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertThat;
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
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.core.JsonProcessingException;

import br.com.somestudy.config.TestConfigs;
import br.com.somestudy.integrationtests.controllers.withyaml.mapper.YAMLMapper;
import br.com.somestudy.integrationtests.dto.AccountCredentialsDTO;
import br.com.somestudy.integrationtests.dto.PersonDTO;
import br.com.somestudy.integrationtests.dto.TokenDTO;
import br.com.somestudy.integrationtests.dto.wrappers.xmlandyaml.PagedModelPerson;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonControllerYamlTest {
	
	private static RequestSpecification specification;
	private static YAMLMapper objectMapper;
	
	private static PersonDTO person;
	
	private static TokenDTO tokenDto; 

	@BeforeAll
	static void setUp() {
		
		objectMapper = new YAMLMapper();
		person = new PersonDTO();
		tokenDto = new TokenDTO();
	}
	
	@Test
	@Order(0)
	void signin() throws JsonProcessingException{
		AccountCredentialsDTO credentials = 
				new AccountCredentialsDTO("root", "admin123");
		
		tokenDto = given()
				.config(
						RestAssuredConfig.config()
						.encoderConfig(
								EncoderConfig.encoderConfig()
								.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT))
				)
				.basePath("/auth/signin")
				.port(TestConfigs.SERVER_PORT)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.accept(MediaType.APPLICATION_YAML_VALUE)
				.body(credentials, objectMapper)
				.when()
				.post()
				.then()
				.statusCode(200)
				.extract()
				.body()
				.as(TokenDTO.class, objectMapper);
						
		specification = new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCAL)
				.addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenDto.getRefreshToken())
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
	void createTest() throws JsonProcessingException{
		mockPerson();
				
		var createdPerson = given().config(
				RestAssuredConfig.config()
				.encoderConfig(
						EncoderConfig.encoderConfig()
						.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT))
						).spec(specification)
					.contentType(MediaType.APPLICATION_YAML_VALUE)
					.accept(MediaType.APPLICATION_YAML_VALUE)
					.body(person, objectMapper)
					.when()
					.put()
					.then()
					.statusCode(200)
					.contentType(MediaType.APPLICATION_YAML_VALUE)
					.extract().body()
					.as(PersonDTO.class, objectMapper);
						
		
	}
	@Test
	@Order(2)
	void updateTest() throws JsonProcessingException{
		
		person.setLastName("Benedicts Torvalds");
		
		var createdPerson = given().config(
				RestAssuredConfig.config()
				.encoderConfig(
						EncoderConfig.encoderConfig()
						.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT))
				).spec(specification)
						.contentType(MediaType.APPLICATION_YAML_VALUE)
						.accept(MediaType.APPLICATION_YAML_VALUE)
						.body(person, objectMapper)
						.when()
						.put()
						.then()
						.statusCode(200)
						.contentType(MediaType.APPLICATION_YAML_VALUE)
						.extract()
						.body()
						.as(PersonDTO.class, objectMapper);
		
		person = createdPerson; 
		
		assertNotNull(createdPerson.getFirstName());
		assertTrue(createdPerson.getId() > 0);
		
		assertEquals("Linus", createdPerson.getFirstName());
		assertEquals("Benedict Torvalds", createdPerson.getLastName()	);
		assertEquals("Helsink  - FinLand", createdPerson.getAddress());
		assertEquals("Male", createdPerson.getGender());
		assertTrue(createdPerson.getEnabled());
	}
	
	@Test
	@Order(3)
	void findByIdTest() throws JsonProcessingException{
		
		var createdPerson = given().config(
						RestAssuredConfig.config().encoderConfig(
								EncoderConfig.encoderConfig().
								encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT))
				).spec(specification)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.accept(MediaType.APPLICATION_YAML_VALUE)
				.pathParam("id", person.getId())
				.when()
				.get("{id}")
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.extract()
				.body()
				.as(PersonDTO.class, objectMapper);
		
		person=createdPerson;
		
		assertNotNull(createdPerson.getId());
		assertTrue(createdPerson.getId() > 0);
		
		assertEquals("Linus", createdPerson.getFirstName());
		assertEquals("Benedict Torvalds", createdPerson.getLastName());
		assertEquals("Helsinki - Finland", createdPerson.getAddress());
		assertEquals("Male", createdPerson.getGender());
		assertTrue(createdPerson.getEnabled());
	}
	
	@Test
	@Order(4)
	void disableTest() throws JsonProcessingException{
		
		var createdPerson = given().config(
				RestAssuredConfig.config()
				.encoderConfig(
						EncoderConfig.encoderConfig()
						.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT))
				).spec(specification)
				.accept(MediaType.APPLICATION_YAML_VALUE)
				.pathParam("id", person.getId())
				.when()
				.patch("{id}")
				.then().statusCode(200)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.extract().body()
				.as(PersonDTO.class, objectMapper);
		
		person=createdPerson;
		
		assertNotNull(createdPerson.getId());
		assertTrue(createdPerson.getId() > 0);
		
		assertEquals("Linus", createdPerson.getFirstName());
		assertEquals("Benedict Torvalds", createdPerson.getLastName());
		assertEquals("Helsinki - Finland", createdPerson.getAddress());
		assertEquals("Male", createdPerson.getGender());
		assertFalse(createdPerson.getEnabled());
	}
	
	@Test
	@Order(5)
	void deleteTest() throws JsonProcessingException{
		
		given(specification)
			.pathParam("id", person.getId())
			.when().delete("{id}")
			.then().statusCode(204);
		
	}
	
	void findAlltest() throws JsonProcessingException{
		
		var response = given(specification)
				.accept(MediaType.APPLICATION_YAML_VALUE)
				.queryParams("page", 3, "size", 12, "direction", "asc")
				.when().get().then().statusCode(200)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.extract().body()
				.as(PagedModelPerson.class, objectMapper);
		
		List<PersonDTO> people = response.getContent();
		
		PersonDTO personOne = people.get(0);
		
		assertNotNull(personOne.getId());
		assertTrue(personOne.getId() > 0);
		
		assertEquals("Allin", personOne.getFirstName());
		assertEquals("Emmot", personOne.getLastName());
		assertEquals("7913 Lindbergh Way", personOne.getAddress());
		assertEquals("Male", personOne.getGender());
		assertTrue(personOne.getEnabled());
		
		PersonDTO personFour = people.get(4);
		
		assertNotNull(personFour.getId());
        assertTrue(personFour.getId() > 0);

        assertEquals("Alonso", personFour.getFirstName());
        assertEquals("Luchelli", personFour.getLastName());
        assertEquals("9 Doe Crossing Avenue", personFour.getAddress());
        assertEquals("Male", personFour.getGender());
        assertTrue(personFour.getEnabled());
	}
	
	void findByNameTest() throws JsonProcessingException{
		
		var response = given(specification)
				.accept(MediaType.APPLICATION_YAML_VALUE)
				.pathParams("firstName", "and")
				.queryParams("page", 0, "size" , 12 ,"direction", "asc")
				.when()
				.get("findPeopleByName/{firstName}").then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.extract().body()
				.as(PagedModelPerson.class, objectMapper);
				
		List<PersonDTO> people = response.getContent();
		
		PersonDTO personOne = people.get(0);

        assertNotNull(personOne.getId());
        assertTrue(personOne.getId() > 0);

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
	@Test
	@Order(6)
	void hateoasAndHalTest() throws JsonProcessingException{
		
		Response response = given(specification)
						.accept(MediaType.APPLICATION_YAML_VALUE)
						.queryParams("page", 3, "size" , 12 , "direction" , "asc")
						.when()
						.get()
						.then()
						.statusCode(200)
						.contentType(MediaType.APPLICATION_YAML_VALUE)
						.extract()
						.response();
						
		String yaml = response.getBody().asString();
		
		Yaml yamlParser = new Yaml();
		
		Map<String, Object> parsedYaml = yamlParser.load(yaml);
		
		List<Map<String, Object>> content = (List<Map<String, Object>>) parsedYaml.get("content");
		
		for(Map<String, Object> person: content){
			
			List<Map<String, String>> links = (List<Map<String, String>>)person.get("links");
			
			for(Map<String, String> link : links){
				assertThat("HATEOAS/HAL link rel is missing", link, hasKey("rel"));
				assertThat("HATEOAS/HAL link href is missing", link, hasKey("href"));
				assertThat("HATEOAS/HAL link type is missing", link, hasKey("type"));
				assertThat("HATEOAS/HAL link " + link + "has an invalid URL", link.get("href"), matchesPattern("https?://.+/api/person/v1.*"));
			}
		
		}
		
	}
	 private void mockPerson() {
        person.setFirstName("Linus");
        person.setLastName("Torvalds");
        person.setAddress("Helsinki - Finland");
        person.setGender("Male");
        person.setEnabled(true);
        person.setProfileUrl("https://pub.erudio.com.br/meus-cursos");
        person.setPhotoUrl("https://pub.erudio.com.br/meus-cursos");
    }
}
