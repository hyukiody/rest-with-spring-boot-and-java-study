package br.com.somestudy.unittests.mapper;

import org.junit.jupiter.api.BeforeEach;

import br.com.somestudy.data.dto.PersonDTO;
import br.com.somestudy.unittests.mapper.mocks.MockPerson;

public class ObjectMapperTests {
	
	MockPerson inputObject;
	
	@BeforeEach
	public void setUp() {
		inputObject = new MockPerson();
	}
	
	public void parseEntityToDTOTest() {
		PersonDTO output = parseObject(inputObject.mockEntity(),PersonDTO.class);
	}

}
