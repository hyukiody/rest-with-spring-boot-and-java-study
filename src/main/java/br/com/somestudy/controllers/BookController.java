package br.com.somestudy.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.somestudy.data.dto.BookDTO;
import br.com.somestudy.services.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/*
 * Controller classes are the classes where the communication methods between the internal java 
 * application run and the SQL database; intermediated by standard pre-configured web applications
 * set by the Spring and Swagger libraries.
 * 
 * The configuration of the GET, POST, PUT and DELETE API methods itself builds the
 * relation of web interface that is being hosted; also the binding with the classes
 * correspondent methods ensures that the Swagger GUI summons the correct class methods,
 * while triggering the database HTML query communications
 */
@RestController
@RequestMapping("/api/book/v1")
@Tag(name = "Book", description = "Endpoints for Managing Book")
public class BookController {
	// This method handles HTTP GET requests to retrieve all books.
	// It produces responses in JSON, XML, and YML formats.
	// The @Operation annotation provides metadata for Swagger documentation.
	// The @ApiResponse annotations specify the possible response codes and their
	// content. standard interface communication for the client and API interface
	// for after sql connection, data can be set and unset from the database, and
	// the client can send and receive data from the database.

	@Autowired
	private BookService service;
	/*
	 * Each CRUD operation has it's equivalent Mapping annotation for the binding of
	 * the class methods and the application HTML query requisition to configure the
	 * API requisitions that bring the various database answer types of return
	 */

	@GetMapping( // endpoint string address, mostly for formatting specification.
			produces = { 
					MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE, 
					MediaType.APPLICATION_YAML_VALUE })
	@Operation(summary = "Finds all Book", description = "Finds all Book", tags = { "Book" },
			// basically an operation might have multiple responses, so we can specify them
			// here.
			responses = { @ApiResponse( 
					
					/*
					 * also not so much different from the operation annotation, but here we can
					 * specify the response codes. Its important to remember these are only standard
					 * auto code for transaction status and not the actual data package being
					 * returned. for each transaction there is a specified object type being sent
					 * and received; so we need a return response code for each transaction, in
					 * order to confirm the transaction status. And action being performed. might
					 * call a transaction status code.
					 * 
					 */
					description = "Success", responseCode = "200", content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BookDTO.class))) }),
					/*
					 * specifying each possible response code is important for the various possible
					 * responses of the API
					 */
					@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
					@ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
					@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
					@ApiResponse(description = "Internal Error", responseCode = "500", content = @Content), }
	
	/*
	 * For each being API Response specified, we need to specify the response code, and the
	 * content being sent and received at each method dependency
	 */
	)
	
	public ResponseEntity<PagedModel<EntityModel<BookDTO>>> findAll(
			@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "size", defaultValue = "12") Integer size,
			@RequestParam(value = "direction", defaultValue = "asc") String direction) {
		/*
		 * This method finishes the interaction of the API with the service configuration for the original object VO,
		 * beginning as a whole new method, but eventually calling the correspondent Service Method findALL() like the
		 * other methods of the Controller class; this is necessary because the API needs another type of 
		 * return class, that must be set through the procedure below of retrieving parameters from the API;
		 * since the application requests are made by calling the controller methods.  
		 * 
		 * When the client sends a GET request to the endpoint, the local instance intercepts it
		 */
		var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
		//then Pageable object is created with parameters
		Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "title"));
		return ResponseEntity.ok(service.findAll(pageable));
	}

	@GetMapping(value = "/{id}",
			// media type specification for the response being sent back!
			produces = { 
					MediaType.APPLICATION_JSON_VALUE, 
					MediaType.APPLICATION_XML_VALUE, 
					MediaType.APPLICATION_YAML_VALUE })
	@Operation(summary = "Finds a Book", description = "Finds a Book", tags = { "Book" }, responses = {
			@ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = BookDTO.class))),
			@ApiResponse(description = "No Content", responseCode = "204", content = @Content),
			@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
			@ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
			@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
			@ApiResponse(description = "Internal Error", responseCode = "500", content = @Content), }
	/*
	 * Again, this is standard setup for package transaction status code specified along with
	 * the annotation tags and the response code. Basically the response code is the 
	 * transaction status response, and the content might be array of objects being sent 
	 * or received as value objects (DTOs, since we have the primary model objects being 
	 * occluded for initial security measures in the database requisitions)
	
	 */
	)
	public BookDTO findById(@PathVariable(value = "id") Long id) {
		return service.findById(id);
		
		/*
		 * The path variable value defines HTML query string, and the service method is
		 * called to return the object. The object is then sent back to the client as 
		 * the value object. Good to remember that the value object can't be deencripted 
		 * by the client, so its safe to send back the object as a default object class;
		 * for at least a class serialization is enough to encrypt the data being passed 
		 * along the network. The object is then serialized and sent back to the client 
		 * as the standard response object.
		 * Because the object is serialized, the client can only see the object as a
		 * serialized object, and not the original object model. Serialization is a good way to
		 * encrypt data being passed along the network.
		
		 */
	}

	@PostMapping(
			consumes = { 
					MediaType.APPLICATION_JSON_VALUE, 
					MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_YAML_VALUE }, 
			produces = { 
					MediaType.APPLICATION_JSON_VALUE, 
					MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_YAML_VALUE })
	@Operation(summary = "Adds a new Book", description = "Adds a new Book by passing in a JSON, XML or YML representation of the book!", tags = {
			"Book" }, responses = {
					@ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = BookDTO.class))),
					@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
					@ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
					@ApiResponse(description = "Internal Error", responseCode = "500", content = @Content), })
	public BookDTO create(@RequestBody BookDTO book) {
		return service.create(book);
	}

	@PutMapping(consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_YAML_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_YAML_VALUE })
	@Operation(summary = "Updates a Book", description = "Updates a Book by passing in a JSON, XML or YML representation of the book!", tags = {
			"Book" }, responses = {
					@ApiResponse(description = "Updated", responseCode = "200", content = @Content(schema = @Schema(implementation = BookDTO.class))),
					@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
					@ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
					@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
					@ApiResponse(description = "Internal Error", responseCode = "500", content = @Content), })
	public BookDTO update(@RequestBody BookDTO book) {
		return service.update(book);
	}

	@DeleteMapping(value = "/{id}")
	@Operation(summary = "Deletes a Book", description = "Deletes a Book by passing in a JSON, XML or YML representation of the book!", tags = {
			"Book" }, responses = { @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
					@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
					@ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
					@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
					@ApiResponse(description = "Internal Error", responseCode = "500", content = @Content), })
	public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

}