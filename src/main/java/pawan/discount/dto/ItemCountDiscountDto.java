package pawan.discount.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemCountDiscountDto extends DiscountResponseDto {
	private int itemId;
	private int minCount;
}
