package com.blog.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.blog.payloads.ApiResponse;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiResponse> resourceNotFoundExceptionHandler(ResourceNotFoundException ex){
	String message = ex.getMessage();
	ApiResponse apiResponse = new ApiResponse(message,false);
	return new ResponseEntity<ApiResponse>(apiResponse,HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handelMethodArgumentNotValidException(MethodArgumentNotValidException ex){
		
		Map<String, String> resp = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error)->{
			String FieldName = ((FieldError) error).getField();
			String message = error.getDefaultMessage();
			resp.put(FieldName, message);
		});
		return new ResponseEntity<Map<String,String>>(resp, HttpStatus.BAD_REQUEST);
	}
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
	    Map<String, String> errors = new HashMap<>();

	    ex.getConstraintViolations().forEach(violation -> {
	        String field = violation.getPropertyPath().toString();
	        String message = violation.getMessage();
	        errors.put(field, message);
	    });

	    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}
	
}
