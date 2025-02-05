package br.com.somestudy.mapper.ObjectMapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import br.com.somestudy.data.vo.v1.PersonVO; // Correct import
import br.com.somestudy.model.Person;       // Correct import

public class PersonMapper {
    private static final ModelMapper modelMapper = new ModelMapper();

    static {
        // 1. Mapping 'key' (PersonVO) to 'id' (Person)
        modelMapper.addMappings(new PropertyMap<PersonVO, Person>() {
            @Override
            protected void configure() {
                map().setId(source.getKey()); // Maps PersonVO's 'key' to Person's 'id'
                // Or, using the more concise method reference with typeMap():
                // modelMapper.typeMap(PersonVO.class, Person.class).addMapping(PersonVO::getKey, Person::setId);
            }
        });

        // 2. Automatic Mapping (Other Fields)
        // Other fields in PersonVO and Person will be mapped automatically
        // if the field names are the same. No configuration is needed unless
        // you have different formats or custom logic.

        // Mapping back from Person to PersonVO
        modelMapper.typeMap(Person.class, PersonVO.class).addMapping(Person::getId, PersonVO::setKey);

        // or using addMappings with PropertyMap:
        // modelMapper.addMappings(new PropertyMap<Person, PersonVO>() {
        //     @Override
        //     protected void configure() {
        //         map().key(source.getId());
        //     }
        // });
    }

    public static Person mapToEntity(PersonVO vo) {
        return modelMapper.map(vo, Person.class);
    }

    public static PersonVO mapToVO(Person entity) {
        return modelMapper.map(entity, PersonVO.class);
    }
}