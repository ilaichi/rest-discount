package pawan.discount.service;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static pawan.discount.DiscountTestData.ITEM_TYPE_DISCOUNT_1;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

import pawan.discount.model.Discount;
import pawan.discount.repository.DiscountRepository;
/*
 * Tests for Discount service. These run without running Spring. 
 */
@ExtendWith(MockitoExtension.class)
class DiscountServiceTest {
	@Mock
	private DiscountRepository discountRepository;

	@InjectMocks
	private DiscountService discountService;

	@Test
	void testAddDiscountSuccess() {
		// given
		Discount discount = ITEM_TYPE_DISCOUNT_1;
		given(discountRepository.findById(discount.getCode())).willReturn(Optional.ofNullable(null));
		//given(discountRepository.save(discount)).willReturn(discount);
		
		// when
		discountService.addDiscount(discount);

		// then
		then(discountRepository).should().save(eq(discount));
	}

	@Test
	void testAddDiscountFailure() {
		// given
		Discount discount = ITEM_TYPE_DISCOUNT_1;
		given(discountRepository.findById(discount.getCode())).willReturn(Optional.of(discount));
		
		// when
		try {
			discountService.addDiscount(discount);
		    fail("Should throw exception");
		} catch (RuntimeException ex) { }
		

		// then
		then(discountRepository).should(never()).save(discount);
	}

	@Test
	void testDeleteDiscountSuccess_existingDiscount() {
		// given
		Discount discount = ITEM_TYPE_DISCOUNT_1;
		willThrow(new EmptyResultDataAccessException(1)).given(discountRepository).deleteById(anyString());
		
		// when
		discountService.deleteDiscount(discount.getCode());

		// then
		// no exception
		then(discountRepository).should().deleteById(anyString());
	}

	@Test
	void testDeleteDiscountSuccess_nonexistentDiscount() {
		// given
		Discount discount = ITEM_TYPE_DISCOUNT_1;
		willDoNothing().given(discountRepository).deleteById(anyString());
		
		// when
		discountService.deleteDiscount(discount.getCode());

		// then
		// no exception
		then(discountRepository).should().deleteById(anyString());
	}
	
}
