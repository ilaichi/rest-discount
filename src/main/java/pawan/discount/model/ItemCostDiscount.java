package pawan.discount.model;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.Min;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import pawan.discount.dto.DiscountedCostDto;

/**
 * Discount based on item cost. If the item cost equals or exceeds a specified minimum then 
 * the discount applies. If multiple items in the line items qualify, the discount is applied
 * to each item. 
 *   
 * @author pawan
 */

@Slf4j
@Entity
@DiscriminatorValue(Discount.TYPE_ITEM_COST)
@ToString(callSuper = true, includeFieldNames = true)
@Getter
@NoArgsConstructor
public class ItemCostDiscount extends Discount {

	// item must meet or exceed this cost to qualify
	@Min(0)
	private double minCost;

	public ItemCostDiscount(String code, String type, int percent, double minCost) {
		super(code, type, percent);
		this.minCost = minCost;
	}

	/**
	 * Calculate discount amount on line items using this item cost discount. 
	 */
	@Override
	public Optional<DiscountedCostDto> calculateDiscount(List<LineItem> lineItems) {

		log.info("Discount: {}", this);

		double totalCost = Discount.calculateTotalCost(lineItems);

		Double itemsTotalCost = lineItems.stream()
				.filter(li -> li.getItem().getCost() >= minCost)
				.map(li -> li.getItem().getCost() * li.getCount())
				.collect(Collectors.summingDouble(Double::doubleValue));

		log.info("Total cost of items (min cost {}) getting discount: {}", minCost, itemsTotalCost);

		double discount = 0.01 * getPercent() * itemsTotalCost;

		return Optional.of(new DiscountedCostDto(discount > 0 ? getCode() : Discount.NO_DISCOUNT_CODE, totalCost - discount));
	}
	
	@Override
	public boolean checkValid() {
		return super.checkValid() && minCost >= 0; 
	}
}
