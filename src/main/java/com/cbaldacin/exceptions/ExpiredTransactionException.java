package com.cbaldacin.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception to be thrown when old transactions comes in
 * 
 * @author carlos
 *
 */
@ResponseStatus(value = HttpStatus.NO_CONTENT)
public class ExpiredTransactionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

}
