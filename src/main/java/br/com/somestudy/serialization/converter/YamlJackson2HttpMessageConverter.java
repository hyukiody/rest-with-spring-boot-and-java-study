package br.com.somestudy.serialization.converter;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

/*
 * This class sets the extension of the predefined library AbstractJacksson2HttpMessageConverter
 *  for converting Java objects (DTOs, EntityModel) to JSON type for the API responses, by 
 *  calling the objectMapper,  
 *  
 *  
 *   
 */

public class YamlJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {
	public YamlJackson2HttpMessageConverter() {
		super(new YAMLMapper().setSerializationInclusion
		(JsonInclude.Include.NON_NULL),
				MediaType.parseMediaType("application/x-yaml"));
	}

}
