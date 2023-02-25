package pawan.discount.exception;

import java.util.Date;

/**
 * Container for error info to be returned.
 * @author pawan
 */
public record ErrorMessage(int statusCode, Date timestamp, String message, String description) {
}