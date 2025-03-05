package br.com.somestudy.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import br.com.somestudy.serialization.converter.YamlJackson2HttpMessageConverter;


/*
 * This class customizes Spring MVC's behavior for content 
 * negotiation, CORS, and message conversion. It enables 
 * YAML support, configures CORS to allow requests from 
 * specific origins, and sets up content negotiation to 
 * handle JSON, XML, and YAML requests
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	private static final MediaType MEDIA_TYPE_APPLICATION_YML = MediaType.valueOf("application/x-yaml");

	@Value("${cors.originPatterns:default}")
	private String corsOriginPatterns = "";

	
	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(new YamlJackson2HttpMessageConverter());
	}
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		var allowedOrigins = corsOriginPatterns.split(",");
		registry.addMapping("/**").allowedMethods("*")
		.allowedOrigins(allowedOrigins)
		.allowCredentials(true);
		
	}
	
	@Override
	public void configureContentNegotiation(
			ContentNegotiationConfigurer configurer) {
		configurer
		//Disables content negotiation based on query parameters.
		.favorParameter(false)
		//Enables content negotiation based on the Accept header
		.ignoreAcceptHeader(false)
		//Allows content negotiation based on file extensions
		.useRegisteredExtensionsOnly(false)
		
		//
		.defaultContentType(
			//Sets the default content type to JSON
			MediaType.APPLICATION_JSON)
			//Registers media types for JSON, XML, and YAML.
			.mediaType("json", MediaType.APPLICATION_JSON)
			.mediaType("xml", MediaType.APPLICATION_XML)
			.mediaType("x-yaml", MEDIA_TYPE_APPLICATION_YML);

	}

}
