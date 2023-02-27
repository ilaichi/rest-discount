package pawan.discount.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import pawan.discount.dto.DiscountedCostDto;
import pawan.discount.exception.DuplicateResourceException;
import pawan.discount.exception.MalformedRequestException;
import pawan.discount.model.Discount;
import pawan.discount.model.LineItem;
import pawan.discount.repository.DiscountRepository;

@Slf4j
@Service
public class DiscountService {

	private DiscountRepository discountRepository;    
	
	public DiscountService(DiscountRepository dr) {
		discountRepository = dr;
	}

	/**
	 * 
	 * @param discount
	 * @return
	 */
	public Discount addDiscount(Discount discount) {
		// prevent duplicate addition/update
		discountRepository.findById(discount.getCode())
		.ifPresent(s -> {
                throw new DuplicateResourceException("Discount already present with code: " + discount.getCode());
            });
		
    	return discountRepository.save(discount);
	}

	/**
	 * 
	 * @return
	 */
	public List<Discount> getAllDiscounts() {
		List<Discount> discounts = new ArrayList<>();    
		discountRepository.findAll().forEach(discounts::add);    
		return discounts;    
	}
	
	/**
	 * 
	 * @param lineItems
	 * @return
	 */
	public DiscountedCostDto findBestDiscount(List<LineItem> lineItems) {

		if (lineItems.isEmpty()) {
			throw new MalformedRequestException("At least one line item is required.");
		}

		if (!LineItem.areValid(lineItems)) {
			throw new MalformedRequestException("A line item is invalid.");
		}
		
		if (!LineItem.areConsistent(lineItems)) {
			throw new MalformedRequestException("An item has multiple types or costs in the line items.");
		}
		
		/**
		 * loop over discounts
		 * find discounted cost for each
		 * keep the max discount or min cost
		 */
		List<Discount> discounts = getAllDiscounts();
		
		Optional<DiscountedCostDto> minCost = discounts.stream()
		.map(d -> d.calculateDiscount(lineItems)) 
		.filter(Optional::isPresent)
		.map(Optional::get)
		.reduce(this::minDC);
		
		DiscountedCostDto result = minCost.orElse(new DiscountedCostDto(Discount.NO_DISCOUNT_CODE, Discount.calculateTotalCost(lineItems)));
		
		log.info("Min cost: {}", result);
		
		return result;
	}

	
	private DiscountedCostDto minDC(DiscountedCostDto a, DiscountedCostDto b) {
		return a.amount() < b.amount() ? a : b;
	}
	
	/**
	 * Deletes if present. Otherwise, silently ignore.
	 * @param code
	 */
	public void deleteDiscount(String code) {
		try {
			discountRepository.deleteById(code);
		} catch (EmptyResultDataAccessException e) {
			// deleting non-existent entity, ignore
		}
	}
}
