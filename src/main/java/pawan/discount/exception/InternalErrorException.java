package pawan.discount.exception;

/**
 * Badly formed request
 * @author pawan
 *
 */
public class InternalErrorException extends DiscountRuntimeException {

	private static final long serialVersionUID = -5351114509956989322L;

	public InternalErrorException(String message){
        super(message);
    }
}
