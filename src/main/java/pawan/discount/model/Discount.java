package pawan.discount.model;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pawan.discount.dto.DiscountedCostDto;

@Slf4j
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="type", discriminatorType = DiscriminatorType.STRING)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public abstract class Discount {

	// discount types
	public static final String TYPE_ITEM_TYPE = "ITEM_TYPE";
	public static final String TYPE_ITEM_COUNT = "ITEM_COUNT";
	public static final String TYPE_ITEM_COST = "ITEM_COST";

	public static final String NO_DISCOUNT_CODE = "NONE";
	
	@Id
	private String code;
	
	@NotNull
	@Column(insertable = false, updatable = false)
	private String type;

	@NotNull
	@Min(1)
	private Integer percent;

	/**
	 * Calculates the total cost for given line items without applying any discounts
	 * 
	 * @param lineItems
	 * @return total undiscounted cost for line items 
	 */
	public static double calculateTotalCost(List<LineItem> lineItems) {
		double totalCost = lineItems.stream()
				.map(li -> li.getCount() * li.getItem().getCost())
				.collect(Collectors.summingDouble(Double::doubleValue));
		
		log.info("Total cost: {}", totalCost);
		return totalCost;
	}

	/**
	 * Base method to calculate discount. Supposed to be overridden for each specific type of discount.
	 * @param lineItems
	 * @return
	 */
	public abstract Optional<DiscountedCostDto> calculateDiscount(List<LineItem> lineItems);
	
	// naming isValid makes it appear like an attribute
	public boolean checkValid() {
		return StringUtils.isNotBlank(code) && StringUtils.isNotBlank(type) && percent > 0; 
	}
}
