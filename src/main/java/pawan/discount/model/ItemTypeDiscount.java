package pawan.discount.model;

import java.util.List;
import java.util.Optional;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@DiscriminatorValue(Discount.TYPE_ITEM_TYPE)
@ToString(callSuper=true, includeFieldNames=true)
@Getter
@NoArgsConstructor
public class ItemTypeDiscount extends Discount {
	
	@NotNull
	private String itemType;
	
	public ItemTypeDiscount(String code, String type, int percent, String itemType) {
		super(code, type, percent);
		this.itemType = itemType;
	}	
	
	@Override
	public Optional<DiscountedCost> calculateDiscount(List<LineItem> lineItems) {

		log.info("Discount: {}", this);
		
		double totalCost = Discount.calculateTotalCost(lineItems);

		double itemTypeCost = lineItems.stream()
				.filter(li -> li.getItem().getType().equals(itemType))
				.map(li -> li.getItem().getCost() * li.getCount())
				.mapToDouble(Double::valueOf).sum();
		
		log.info("Total item cost for type {}: {}", itemType, itemTypeCost);

		double discount = itemTypeCost * 0.01 * getPercent();
		
		return Optional.of(new DiscountedCost(discount > 0 ? getCode() : Discount.NO_DISCOUNT_CODE, totalCost - discount));
	}
	
	@Override
	public boolean checkValid() {
		return super.checkValid() && StringUtils.isNotBlank(itemType); 
	}
}
