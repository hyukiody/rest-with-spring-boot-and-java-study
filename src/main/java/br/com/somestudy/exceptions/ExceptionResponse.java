package br.com.somestudy.exceptions;

import java.io.Serializable;
import java.util.Date;

//exception treatment class, also serializable for response status message code

public record ExceptionResponse(Date timestamp, String message, String details) {}

