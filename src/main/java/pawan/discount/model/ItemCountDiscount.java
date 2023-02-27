package pawan.discount.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.Min;

import org.springframework.data.util.Pair;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import pawan.discount.dto.DiscountedCostDto;
import pawan.discount.exception.MalformedRequestException;

@Slf4j
@Entity
@DiscriminatorValue(Discount.TYPE_ITEM_COUNT)
@ToString(callSuper=true, includeFieldNames=true)
@Getter
@NoArgsConstructor
public class ItemCountDiscount extends Discount {
	
	@Min(0)
	private int itemId;
	
	@Min(1)
	private int minCount;

	public ItemCountDiscount(String code, String type, int percent, int itemId, int minCount) {
		super(code, type, percent);
		this.itemId = itemId;
		this.minCount = minCount;
	}

	/**
	 * Calculate discount amount on line items using this item count discount. 
	 */
	@Override
	public Optional<DiscountedCostDto> calculateDiscount(List<LineItem> lineItems) {
		
		log.info("Discount: {}", this);
		
		if (!LineItem.areConsistent(lineItems)) {
			throw new MalformedRequestException("An item has multiple types or costs in the line items.");
		}
		
		double totalCost = Discount.calculateTotalCost(lineItems);

		// if one item appears in multiple lines, we aggregate the counts
		Map<Double, Integer> itemCostCountMap = lineItems.stream()
				.filter(li -> li.getItem().getId() == itemId)
				.map(li -> Pair.of(li.getItem().getCost(), li.getCount()))
				.collect(
			            Collectors.groupingBy(
			                    Pair::getFirst, Collectors.summingInt(Pair<Double, Integer>::getSecond)));
		
		if (itemCostCountMap.isEmpty()) {
			return Optional.empty();

		} else {
			// there will be only one entry
			Map.Entry<Double, Integer> itemCostCountEntry = itemCostCountMap.entrySet().iterator().next();
			
			log.info("Item count and cost with Id {}: {}, {}", itemId, itemCostCountEntry.getValue(), itemCostCountEntry.getValue());
	
			double discount = itemCostCountEntry.getValue() >= minCount ? 
					0.01 * getPercent() * itemCostCountEntry.getValue() * itemCostCountEntry.getKey() : 0;
			
			return Optional.of(new DiscountedCostDto(discount > 0 ? getCode() : Discount.NO_DISCOUNT_CODE, totalCost - discount));
		} 
	}
	
	@Override
	public boolean checkValid() {
		return super.checkValid() && itemId >= 0 && minCount > 0; 
	}

}
