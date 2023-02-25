package pawan.discount.exception;

/**
 * Badly formed request
 * @author pawan
 *
 */
public class MalformedRequestException extends DiscountRuntimeException {

	private static final long serialVersionUID = 2686007934376388976L;

	public MalformedRequestException(String message){
        super(message);
    }
}
