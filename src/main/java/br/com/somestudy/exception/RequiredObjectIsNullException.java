package br.com.somestudy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// The @ResponseStatus annotation marks the exception with a specific HTTP status code.
// In this case, HttpStatus.BAD_REQUEST (400) is used to indicate that the request was invalid.
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RequiredObjectIsNullException extends RuntimeException {

    // The serialVersionUID is a unique identifier for Serializable classes.
    // It is used during deserialization to verify that the sender and receiver of a serialized object
    // have loaded classes for that object that are compatible with respect to serialization.
    private static final long serialVersionUID = 1L;

    // Default constructor with a predefined error message.
    public RequiredObjectIsNullException() {
        super("It is not allowed to persist a null object!");
    }

    // Constructor that accepts a custom error message.
    public RequiredObjectIsNullException(String ex) {
        super(ex);
    }
}
