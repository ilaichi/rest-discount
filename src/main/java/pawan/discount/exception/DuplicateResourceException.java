package pawan.discount.exception;

/**
 * Uniqueness violation
 * @author pawan
 */
public class DuplicateResourceException extends DiscountRuntimeException {

	private static final long serialVersionUID = -8395025204764522320L;

	public DuplicateResourceException(String message) {
		super(message);
	}
}
