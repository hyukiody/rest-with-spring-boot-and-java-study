package br.com.somestudy.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
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

import br.com.somestudy.data.vo.v1.BookVO;
import br.com.somestudy.services.BookServices;
import br.com.somestudy.util.MediaType;
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
    // The @ApiResponse annotations specify the possible response codes and their content. standard interface communication for the client  and api interface
	//for after sql connection, data can be set and unset from the database, and the client can send and receive data from the database.
	
	@Autowired
	private BookServices service;
<<<<<<< HEAD
	/*
	 * Each CRUD operation has it's equivalent Mapping annotation for the binding of the 
	 * class methods and the application HTML query requisition 
	 */
	@GetMapping( //to configure the API requisition to return the database answer types of return
=======

	@GetMapping(// endpoint string address, mostly for formatting specification. 
>>>>>>> f87d1111b0bf24fc81439351eba5feee66fcbc41
			produces = { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML })
		@Operation(summary = "Finds all Book", description = "Finds all Book",
			tags = {"Book"},
			// basically an operation might have multiple responses, so we can specify them here.
			responses = {
				@ApiResponse( //also not so much different from the operation annotation, but here we can specify the response code. But its important to remember these are only standardized auto code for transaction status and not the actual response code.
				//for each transaction there is a specified object type being sent and received;
				//so we need a return response code for each transaction, in order to confirm the transaction status. And action being performed.
				//might call a transaction status code.
					description = "Success", responseCode = "200",
					content = {
						@Content(
							mediaType = "application/json",
							array = @ArraySchema(schema = @Schema(implementation = BookVO.class))
						)
					}), //specifying each possible response code is important for the various possible responses
				//of the API
				@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
				@ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
				@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
				@ApiResponse(description = "Internal Error", responseCode = "500", content = @Content),
			}
			//so for each being specified, we need to specify the response code, and the content being sent and received at each method dependency
		)
		public ResponseEntity <PagedModel<EntityModel<BookVO>>>findAll(
				@RequestParam(value = "page", defaultValue = "0") Integer page,
				@RequestParam(value = "size", defaultValue = "12") Integer size,
				@RequestParam(value = "direction", defaultValue = "asc") String direction
		){
			var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
			
			Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "title"));
			return ResponseEntity.ok(service.findAll(pageable));
		}
		
		@GetMapping(value = "/{id}", //mediatype specification for the response being sent back!
			produces = { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML  })
		@Operation(summary = "Finds a Book", description = "Finds a Book",
			tags = {"Book"},
			responses = {
				@ApiResponse(description = "Success", responseCode = "200",
					content = @Content(schema = @Schema(implementation = BookVO.class))
				),
				@ApiResponse(description = "No Content", responseCode = "204", content = @Content),
				@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
				@ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
				@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
				@ApiResponse(description = "Internal Error", responseCode = "500", content = @Content),
			}
			//again, this is standard type of package being return are specified along with the annotation tags and the response code.
			//basically the response code is the transaction status code, and the content is the  array of objects being sent and received as value objects or arrays of value objects
		)
		public BookVO findById(@PathVariable(value = "id") Long id) {
			return service.findById(id);
			//the pathvariable value defines html query string, and the service method is called to return the object.
			//the object is then sent back to the client as the value object 
			//good to remember that the value object cant be deencripted by the client, so its safe to send back the object as a default object class; for at least a class serialization is enough to encrypt the data being passed along the network.
			//the object is then serialized and sent back to the client as a response object.
			//because the object is serialized, the client can only see the object as a serialized object, and not the actual object. serialization is a good way to encrypt data being passed along the network.
		}
		
		@PostMapping(
			consumes = { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML  },
			produces = { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML  })
		@Operation(summary = "Adds a new Book",
			description = "Adds a new Book by passing in a JSON, XML or YML representation of the book!",
			tags = {"Book"},
			responses = {
				@ApiResponse(description = "Success", responseCode = "200",
					content = @Content(schema = @Schema(implementation = BookVO.class))
				),
				@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
				@ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
				@ApiResponse(description = "Internal Error", responseCode = "500", content = @Content),
			}
		)
		public BookVO create(@RequestBody BookVO book) {
			return service.create(book);
		}
		
		@PutMapping(
			consumes = { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML  },
			produces = { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML  })
		@Operation(summary = "Updates a Book",
			description = "Updates a Book by passing in a JSON, XML or YML representation of the book!",
			tags = {"Book"},
			responses = {
				@ApiResponse(description = "Updated", responseCode = "200",
					content = @Content(schema = @Schema(implementation = BookVO.class))
				),
				@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
				@ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
				@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
				@ApiResponse(description = "Internal Error", responseCode = "500", content = @Content),
			}
		)
		public BookVO update(@RequestBody BookVO book) {
			return service.update(book);
		}
		
		
		@DeleteMapping(value = "/{id}")
		@Operation(summary = "Deletes a Book",
			description = "Deletes a Book by passing in a JSON, XML or YML representation of the book!",
			tags = {"Book"},
			responses = {
				@ApiResponse(description = "No Content", responseCode = "204", content = @Content),
				@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
				@ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
				@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
				@ApiResponse(description = "Internal Error", responseCode = "500", content = @Content),
			}
		)
		public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) {
			service.delete(id);
			return ResponseEntity.noContent().build();
		}
	
}