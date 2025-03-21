package br.com.somestudy.unittests.mapper;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.somestudy.data.dto.PersonDTO;
import br.com.somestudy.mapper.ObjectMapper;
import br.com.somestudy.model.Person;
import br.com.somestudy.unittests.mapper.mocks.MockPerson;

public class ObjectMapperTests {
	
	MockPerson inputObject;
	
	@BeforeEach
	public void setUp() {
		inputObject = new MockPerson();
	}
	@Test
	public void parseEntityToDTOTest() {
		PersonDTO output = ObjectMapper.parseObject(inputObject.mockEntity(),PersonDTO.class);
		assertEquals(Long.valueOf(0L),output.getId());
		assertEquals("First Name Test0", output.getFirstName());
		assertEquals("Last Name Test0", output.getLastName());
		assertEquals("Address Test0", output.getAddress());
		assertEquals("Male", output.getGender());
			
	}
	@Test
	public void parseEntity() {
		List<PersonDTO> outputList = ObjectMapper.parseListObjects(inputObject.mockEntityList(),PersonDTO.class);
		PersonDTO outputZero = outputList.get(0);
		
		assertEquals(Long.valueOf(0L), outputZero.getId());
		assertEquals("First Name Test0", outputZero.getFirstName());
		assertEquals("Last Name Test0", outputZero.getLastName());
		assertEquals("Address Test0", outputZero.getAddress());
		assertEquals("Male", outputZero.getGender());
		
		PersonDTO outputSeven = outputList.get(7);

        assertEquals(Long.valueOf(7L), outputSeven.getId());
        assertEquals("First Name Test7", outputSeven.getFirstName());
        assertEquals("Last Name Test7", outputSeven.getLastName());
        assertEquals("Address Test7", outputSeven.getAddress());
        assertEquals("Female", outputSeven.getGender());

        PersonDTO outputTwelve = outputList.get(12);

        assertEquals(Long.valueOf(12L), outputTwelve.getId());
        assertEquals("First Name Test12", outputTwelve.getFirstName());
        assertEquals("Last Name Test12", outputTwelve.getLastName());
        assertEquals("Address Test12", outputTwelve.getAddress());
        assertEquals("Male", outputTwelve.getGender());
    }
	@Test
	public void parseDTOListToEntity() {
		List<Person> outputList = ObjectMapper.parseListObjects(inputObject.mockDTOList(), Person.class);
		Person outputZero = outputList.get(0);
		
		assertEquals(Long.valueOf(0L), outputZero.getId());
        assertEquals("First Name Test0", outputZero.getFirstName());
        assertEquals("Last Name Test0", outputZero.getLastName());
        assertEquals("Address Test0", outputZero.getAddress());
        assertEquals("Male", outputZero.getGender());

        Person outputSeven = outputList.get(7);

        assertEquals(Long.valueOf(7L), outputSeven.getId());
        assertEquals("First Name Test7", outputSeven.getFirstName());
        assertEquals("Last Name Test7", outputSeven.getLastName());
        assertEquals("Address Test7", outputSeven.getAddress());
        assertEquals("Female", outputSeven.getGender());

        Person outputTwelve = outputList.get(12);

        assertEquals(Long.valueOf(12L), outputTwelve.getId());
        assertEquals("First Name Test12", outputTwelve.getFirstName());
        assertEquals("Last Name Test12", outputTwelve.getLastName());
        assertEquals("Address Test12", outputTwelve.getAddress());
        assertEquals("Male", outputTwelve.getGender());
    }
	
}
