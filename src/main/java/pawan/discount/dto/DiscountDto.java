package pawan.discount.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data class to receive the request body and convert it to the specific discunt type
 * 
 * @author pawan
 */

@Setter
@Getter
@NoArgsConstructor
public class DiscountDto {
	private String code;
	private String type;
	private int percent;	
	private double minCost;
	private int itemId;
	private int minCount;
	private String itemType;
}
