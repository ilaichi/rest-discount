package pawan.discount.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Data object for a single item
 * @author pawan
 */
@Getter
@AllArgsConstructor
public class  Item {
	
	private long id; 
	private String type; 
	private double cost;
	
	// some item type constants
	public static final String TYPE_CLOTHES = "CLOTHES";
	public static final String TYPE_ELECTRONICS = "ELECTRONICS";
	public static final String TYPE_MEATS = "MEATS";
	public static final String TYPE_DAIRY = "DAIRY";
	public static final String TYPE_PRODUCE = "PRODUCE";
	public static final String TYPE_TOYS = "TOYS";

	public boolean checkValid() {
		return id > 0 && cost > 0;
	}
}