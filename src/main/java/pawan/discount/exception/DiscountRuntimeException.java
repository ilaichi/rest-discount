package pawan.discount.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Base runtime exception
 * @author pawan
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class DiscountRuntimeException extends RuntimeException{

	private static final long serialVersionUID = 8769873382484840445L;

	public DiscountRuntimeException(String message){
        super(message);
    }
}
