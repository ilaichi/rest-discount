package pawan.discount.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data class to receive the request body and convert it to the specific discount type
 * 
 * @author pawan
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DiscountDto extends DiscountResponseDto {
	private int itemId;
	private int minCount;
	private String itemType;
	private double minCost;
}
