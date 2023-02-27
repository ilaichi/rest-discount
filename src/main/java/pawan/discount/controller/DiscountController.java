package pawan.discount.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import pawan.discount.dto.DiscountCodeDto;
import pawan.discount.dto.DiscountDto;
import pawan.discount.dto.DiscountMapper;
import pawan.discount.dto.DiscountResponseDto;
import pawan.discount.dto.DiscountedCostDto;
import pawan.discount.dto.LineItemDto;
import pawan.discount.dto.LineItemMapper;
import pawan.discount.exception.MalformedRequestException;
import pawan.discount.model.Discount;
import pawan.discount.model.LineItem;
import pawan.discount.service.DiscountService;


@Slf4j
@RestController
@RequestMapping("/api/v1/discounts")
public class DiscountController {

	private final DiscountService discountService;

	/**
	 * Constructor for injection 
	 * 
	 * @param ds discount service
	 * @param om object mapper
	 */
	public DiscountController(DiscountService ds) {
		discountService = ds;
	}
	
	/**
	 * Retrieves all discounts present in the repository
	 * 
	 * @return list of discounts
	 */
	@GetMapping("")
	public List<DiscountResponseDto> getAllDiscounts() {
		return discountService.getAllDiscounts().stream()
				.map(DiscountMapper.INSTANCE::map).toList();
	}

	/**
	 * Adds a new discount. If a discount already exists with the same code, it does not change anything
	 * and returns CONFLICT HTTP status (through common exception handling). 
	 * 
	 * @param discount to be added
	 * @return discount added
	 */
	@PostMapping("")
	public DiscountResponseDto addDiscount(@RequestBody(required = true) DiscountDto discountDto) {
		Discount discount = DiscountMapper.INSTANCE.mapToDiscount(discountDto);
		
		log.debug("Discount Request Body {}, Discount: {}", discountDto, discount);
		
		if (!discount.checkValid()) {
			throw new MalformedRequestException("Discount must have non-empty code and type and percent must be more than 0. Additional fields are required depending on the discount type.");
		}
		discountService.addDiscount(discount);
		
		return DiscountMapper.INSTANCE.map(discount);
	}

	/**
	 * Deletes an existing discount by code. If there is no such discount, it doesn't do anything (no error).
	 * 
	 * @param code of the discount to delete
	 * @return code that was supplied
	 */
	@DeleteMapping("{code}")
	public DiscountCodeDto deleteDiscount(@PathVariable(required = true) String code) {
		discountService.deleteDiscount(code);
		return new DiscountCodeDto(code);
	}
	
	/**
	 * Calculates and returns the best applicable discount from the existing ones. 
	 * - all discounts in the repository are assumed to be active
	 * - only one discount is applied at a time
	 * - the largest discount amount for the list of line items is selected as the best 
	 * 
	 * @param jsonLineItems line items as a JSON array from request header (body is not a good choice with GET)
	 * @return discounted total cost and the applied discount code 
	 */
	@PostMapping("findBestDiscountByItems")
	public DiscountedCostDto findBestDiscountByItems(@RequestBody(required = true) List<LineItemDto> lineItemDtos) {
		DiscountedCostDto result;
		
		List<LineItem> lineItems = lineItemDtos.stream().map(LineItemMapper.INSTANCE::map).toList();
		
		result = discountService.findBestDiscount(lineItems);

		return result;
	}
}
