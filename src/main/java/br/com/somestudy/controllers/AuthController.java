package br.com.somestudy.controllers;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.somestudy.controllers.docs.AuthControllerDocs;
import br.com.somestudy.data.dto.security.AccountCredentialsDTO;
import br.com.somestudy.services.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Authentication Endpoint!")
@RestController
@RequestMapping("/auth")
public class AuthController implements AuthControllerDocs{
	
	@Autowired
	AuthService service;
	
	@PostMapping("/signin")
	@Override
	public ResponseEntity<?> signin(@RequestBody AccountCredentialsDTO credentials){
		
		if(credentialsIsInvalid(credentials)) 
			return ResponseEntity
					.status(HttpStatus.FORBIDDEN)
					.body("Invalid client request!");
		var token=service.signIn(credentials);
		
		if(token==null) ResponseEntity.status(HttpStatus.FORBIDDEN)
		.body("Invalid client request!");
		return token;
		
	}
	
	@PutMapping("/refresh/{username}")
    @Override
	public ResponseEntity<?> refreshToken(
			@PathVariable("username") String username,
			@RequestHeader("Authorization") String refreshToken){
		
		if(parametersAreInvalid(username, refreshToken)) 
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body("Invalid client request!");
		var token = service.refreshToken(username, refreshToken);
		
		if(token == null) ResponseEntity.status(HttpStatus.FORBIDDEN)
		.body("Invalid client request!");
		return token;
	}
	@PostMapping(value = "/createUser",
			consumes= {
					MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_YAML_VALUE,
			},
			produces= {
					MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_YAML_VALUE,
			}
			
			)
	@Override
	public AccountCredentialsDTO create(@RequestBody AccountCredentialsDTO credentials) {
		return service.create(credentials);
		
	}
	public boolean parametersAreInvalid(String username, String refreshToken) {
		return StringUtils.isBlank(username) || StringUtils.isBlank(refreshToken);
		
	}
	
	private static boolean credentialsIsInvalid(AccountCredentialsDTO credentials) {
		return credentials ==null ||
				StringUtils.isBlank(credentials.getPassword()) ||
				StringUtils.isBlank(credentials.getUsername());
		
	}
}
