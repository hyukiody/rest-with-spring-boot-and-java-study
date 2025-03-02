package br.com.somestudy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.somestudy.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.somestudy.model.Person;
import br.com.somestudy.repositories.PersonRepository;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonRepositoryTest extends AbstractIntegrationTest {

	@Autowired
	PersonRepository repository;
	private static Person person;
	
	@BeforeAll
	static void setUp() {
		person = new Person();
	}
	
	@Test
	@Order(1)
	void findPeopleByName() {
		Pageable pageable = PageRequest.of(
				0,
				12,
				Sort.by(Sort.Direction.ASC, "firstName"));
		
		person = repository.findPeopleByName("iko", pageable).getContent().get(0);
		
		assertNotNull(person);
		assertNotNull(person.getId());
		assertEquals("Nikola", person.getFirstName());
		assertEquals("Tesl", person.getLastName());
		assertEquals("Male", person.getGender());
		assertTrue(person.getEnabled());
		
	}
}
