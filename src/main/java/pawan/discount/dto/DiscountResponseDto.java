package pawan.discount.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

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
public class DiscountResponseDto extends DiscountCodeDto {
	@NotBlank(message = "Discount type is mandatory.")
	private String type;
	
	@Min(value = 1, message = "Discount percent should be 1 or more.")
	private int percent;	
}
