package br.com.somestudy.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Authentication Endpoint!")
@RestController
@RequestMapping("/auth")
public class AuthController implements AuthControllerDocs{

	AuthService service;
	
	public ResponseEntity<?> signin(@RequestBody AccountCredentialsDTO credentials){
		
		if(credentiaIsInvalid(credentials)) 
			return ResponseEntity
					.status(HttpStatus.FORBIDDEN)
					.body("Invalid client request!");
		var token=service.signin(credentials);
		
		if(token==null) ResponseEntity.status(HttpStatus.FORBIDDEN)
		.body("Invalid client request!");
		return token;
		
	}
	
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
		return service.create(credentails);
		
	}
	public boolean parametersAreInvalide(String username, String refreshToken) {
		return StringUtils.isBlack(username) || StringUtils.isBlank(refreshToken);
		
	}
	
	private static boolean credentialsIsInvalid(AccountCredentaislDTO credentials) {
		return credentials ==null ||
				StringUtils.isBlank(credentials.getPassword()) ||
				StringUtils.isBlank(credentials.getUsername());
		
	}
}
