package pawan.discount.controller;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import pawan.discount.exception.DiscountRuntimeException;
import pawan.discount.exception.DuplicateResourceException;
import pawan.discount.exception.ErrorMessage;
import pawan.discount.exception.MalformedRequestException;

/**
 * Common exception handler for controllers
 * @author pawan 
 */

@ControllerAdvice
@ResponseBody
public class ControllerExceptionHandler {

	@ExceptionHandler(value = { DuplicateResourceException.class })
	@ResponseStatus(value = HttpStatus.CONFLICT)
	public ErrorMessage handleConflictException(DiscountRuntimeException ex, WebRequest request) {
		return new ErrorMessage(HttpStatus.CONFLICT.value(), new Date(), ex.getMessage(),
				request.getDescription(false));
	}

	@ExceptionHandler(value = { MalformedRequestException.class })
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ErrorMessage handleBadRequestException(DiscountRuntimeException ex, WebRequest request) {
		return new ErrorMessage(HttpStatus.BAD_REQUEST.value(), new Date(), ex.getMessage(),
				request.getDescription(false));
	}

	@ExceptionHandler(value = { MissingRequestHeaderException.class })
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ErrorMessage handleBadRequestException(MissingRequestHeaderException ex, WebRequest request) {
		return new ErrorMessage(HttpStatus.BAD_REQUEST.value(), new Date(), ex.getMessage(),
				request.getDescription(false));
	}
}
