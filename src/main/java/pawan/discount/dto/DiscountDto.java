package pawan.discount.dto;

import javax.validation.constraints.AssertTrue;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pawan.discount.model.Discount;

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
	
    @AssertTrue(message = "minCost should be more than 0")
    private boolean isItemCostDiscountValid() {
        return !Discount.TYPE_ITEM_COST.equals(getType()) || minCost > 0;
    }
	
    @AssertTrue(message = "itemId should be more than 0 and minCount should be more than 0")
    private boolean isItemCountDiscountValid() {
        return !Discount.TYPE_ITEM_COUNT.equals(getType()) || itemId > 0 && minCount > 0;
    }
	
    @AssertTrue(message = "itemType should not be blank")
    private boolean isItemTypeDiscountValid() {
        return !Discount.TYPE_ITEM_TYPE.equals(getType()) || StringUtils.isNotBlank(itemType);
    }
}
