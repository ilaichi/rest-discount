package pawan.discount.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pawan.discount.DiscountTestData.CHEAP_SHIRT;
import static pawan.discount.DiscountTestData.CHEAP_TV;
import static pawan.discount.DiscountTestData.EXPENSIVE_SHIRT;
import static pawan.discount.DiscountTestData.EXPENSIVE_TV;
import static pawan.discount.DiscountTestData.ITEM_COST_DISCOUNT_1;
import static pawan.discount.DiscountTestData.ITEM_COUNT_DISCOUNT_1;
import static pawan.discount.DiscountTestData.ITEM_TYPE_DISCOUNT_1;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import pawan.discount.dto.DiscountedCostDto;
import pawan.discount.model.Discount;
import pawan.discount.model.Item;
import pawan.discount.model.ItemCostDiscount;
import pawan.discount.model.ItemCountDiscount;
import pawan.discount.model.ItemTypeDiscount;
import pawan.discount.model.LineItem;
import pawan.discount.repository.DiscountRepository;

/**
 * Integration tests for Discount controller.
 * It tests each operation with success and failure use cases.
 * 
 * @author pawan
 */
@SuppressWarnings("serial")
@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class DiscountControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private DiscountRepository discountRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static final String BEST_DISCOUNT_URI = "/api/v1/discounts/findBestDiscountByItems";
	private static final String DISCOUNTS_URI = "/api/v1/discounts";
	private static final String SINGLE_DISCOUNT_URI = DISCOUNTS_URI + "/{code}";

	@AfterEach
	void tearDown() {
		// clean up the discount table
		JdbcTestUtils.deleteFromTables(jdbcTemplate, "discount");
	}

	/**************************************************************
	 * Get all discounts 
	 **************************************************************/
	
	@Test
	void getAllDiscountsSuccess() throws Exception {
		discountRepository.save(ITEM_COST_DISCOUNT_1);

		mockMvc.perform(get(DISCOUNTS_URI).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$[0].code").value(ITEM_COST_DISCOUNT_1.getCode()))
		.andExpect(jsonPath("$[0].type").value(ITEM_COST_DISCOUNT_1.getType()))
		.andExpect(jsonPath("$[0].percent").value(ITEM_COST_DISCOUNT_1.getPercent()))
		.andExpect(jsonPath("$[0].minCost").value(ITEM_COST_DISCOUNT_1.getMinCost()));
	}
	
	/**************************************************************
	 * Add discount 
	 **************************************************************/
	
	@Test
	void addItemTypeDiscountSuccess() throws Exception {
		addDiscountSuccess(ITEM_TYPE_DISCOUNT_1);
	}

	@Test
	void addItemCountDiscountSuccess() throws Exception {
		addDiscountSuccess(ITEM_COUNT_DISCOUNT_1);
	}

	@Test
	void addTotalCostDiscountSuccess() throws Exception {
		addDiscountSuccess(ITEM_COST_DISCOUNT_1);
	}

	void addDiscountSuccess(Discount discount) throws Exception {

		MvcResult callResult = mockMvc.perform(post(DISCOUNTS_URI).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(discount))).andExpect(status().isOk()).andReturn();

		log.info("Call result: {}", callResult.getResponse().getContentAsString());
		
		Optional<Discount> result = discountRepository.findById(discount.getCode());

		assertThat(result).isPresent();
		log.info("Retrieved discount: {}", result.get());
		assertThat(discount.getCode()).isEqualTo(result.get().getCode());
	}

	
	@Test
	void addDiscountFailure_duplicateDiscount() throws Exception {
		discountRepository.save(ITEM_COST_DISCOUNT_1);
		
		addDiscountFailure(ITEM_COST_DISCOUNT_1, HttpStatus.CONFLICT.value());
	}

	
	@Test
	void addDiscountFailure_zeroPercent() throws Exception {
		Discount discount = new ItemCostDiscount(ITEM_COST_DISCOUNT_1.getCode(), ITEM_COST_DISCOUNT_1.getType(), 0, ITEM_COST_DISCOUNT_1.getMinCost());
		addDiscountFailure(discount, HttpStatus.BAD_REQUEST.value());
	}

	@Test
	void addDiscountFailure_negativePercent() throws Exception {
		Discount discount = new ItemCostDiscount(ITEM_COST_DISCOUNT_1.getCode(), ITEM_COST_DISCOUNT_1.getType(), -10, ITEM_COST_DISCOUNT_1.getMinCost());
		addDiscountFailure(discount, HttpStatus.BAD_REQUEST.value());
	}

	@Test
	void addDiscountFailure_emptyCode() throws Exception {
		Discount discount = new ItemCostDiscount("  ", ITEM_COST_DISCOUNT_1.getType(), 10, ITEM_COST_DISCOUNT_1.getMinCost());
		addDiscountFailure(discount, HttpStatus.BAD_REQUEST.value());
	}

	@Test
	void addDiscountFailure_nullCode() throws Exception {
		Discount discount = new ItemCostDiscount(null , ITEM_COST_DISCOUNT_1.getType(), 10, ITEM_COST_DISCOUNT_1.getMinCost());
		addDiscountFailure(discount, HttpStatus.BAD_REQUEST.value());
	}

	@Test
	void addDiscountFailure_emptyType() throws Exception {
		Discount discount = new ItemCostDiscount(ITEM_COST_DISCOUNT_1.getCode(), " " , 10, ITEM_COST_DISCOUNT_1.getMinCost());
		addDiscountFailure(discount, HttpStatus.BAD_REQUEST.value());
	}

	@Test
	void addDiscountFailure_nullType() throws Exception {
		Discount discount = new ItemCostDiscount(ITEM_COST_DISCOUNT_1.getCode(), null , 10, ITEM_COST_DISCOUNT_1.getMinCost());
		addDiscountFailure(discount, HttpStatus.BAD_REQUEST.value());
	}

	@Test
	void addDiscountFailure_itemCostNegativeMinCost() throws Exception {
		ItemCostDiscount discount = new ItemCostDiscount(ITEM_COST_DISCOUNT_1.getCode(), ITEM_COST_DISCOUNT_1.getType(), ITEM_COST_DISCOUNT_1.getPercent(), -10);
		addDiscountFailure(discount, HttpStatus.BAD_REQUEST.value());
	}

	@Test
	void addDiscountFailure_itemTypeBlankItemType() throws Exception {
		ItemTypeDiscount discount = new ItemTypeDiscount(ITEM_TYPE_DISCOUNT_1.getCode(), ITEM_TYPE_DISCOUNT_1.getType(), ITEM_TYPE_DISCOUNT_1.getPercent(), " ");
		addDiscountFailure(discount, HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	void addDiscountFailure_itemTypeNullItemType() throws Exception {
		ItemTypeDiscount discount = new ItemTypeDiscount(ITEM_TYPE_DISCOUNT_1.getCode(), ITEM_TYPE_DISCOUNT_1.getType(), ITEM_TYPE_DISCOUNT_1.getPercent(), null);
		addDiscountFailure(discount, HttpStatus.BAD_REQUEST.value());
	}

	@Test
	void addDiscountFailure_itemCountNegativeMinCount() throws Exception {
		ItemCountDiscount discount = new ItemCountDiscount(ITEM_COUNT_DISCOUNT_1.getCode(), ITEM_COUNT_DISCOUNT_1.getType(), ITEM_COUNT_DISCOUNT_1.getPercent(), ITEM_COUNT_DISCOUNT_1.getItemId(), -10);
		addDiscountFailure(discount, HttpStatus.BAD_REQUEST.value());
	}

	@Test
	void addDiscountFailure_unknownDiscountCode() throws Exception {
		ItemCountDiscount discount = new ItemCountDiscount(ITEM_COUNT_DISCOUNT_1.getCode(), "BLAH" ,ITEM_COUNT_DISCOUNT_1.getPercent(), ITEM_COUNT_DISCOUNT_1.getItemId(), 10);
		addDiscountFailure(discount, HttpStatus.BAD_REQUEST.value());
	}

	void addDiscountFailure(Discount discount, int expectedStatus) throws Exception {
		mockMvc.perform(post(DISCOUNTS_URI).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(discount))).andExpect(status().is(expectedStatus));
	}

	/**************************************************************
	 * Delete discount 
	 **************************************************************/
	
	@Test
	void deleteItemTypeDiscountSuccess() throws Exception {
		deleteDiscountSuccess(ITEM_TYPE_DISCOUNT_1);
	}

	@Test
	void deleteItemCountDiscountSuccess() throws Exception {
		deleteDiscountSuccess(ITEM_COUNT_DISCOUNT_1);
	}

	@Test
	void deleteTotalCostDiscountSuccess() throws Exception {
		deleteDiscountSuccess(ITEM_COST_DISCOUNT_1);
	}


	@Test
	void deleteItemTypeDiscountSuccess_nonexistentDiscount() throws Exception {
		deleteDiscountSuccessWithoutAddingFirst(ITEM_TYPE_DISCOUNT_1);
	}

	@Test
	void deleteItemCountDiscountSuccess_nonexistentDiscount() throws Exception {
		deleteDiscountSuccessWithoutAddingFirst(ITEM_COUNT_DISCOUNT_1);
	}

	@Test
	void deleteTotalCostDiscountSuccess_nonexistentDiscount() throws Exception {
		deleteDiscountSuccessWithoutAddingFirst(ITEM_COST_DISCOUNT_1);
	}

	// utility method
	private void deleteDiscountSuccess(Discount discount) throws Exception {
		discountRepository.save(discount);
		deleteDiscountSuccessWithoutAddingFirst(discount);
	}

	// utility method
	private void deleteDiscountSuccessWithoutAddingFirst(Discount discount) throws Exception {

		mockMvc.perform(delete(SINGLE_DISCOUNT_URI, discount.getCode()).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

		Optional<Discount> result = discountRepository.findById(discount.getCode());

		assertThat(result).isEmpty();
	}
	
	/**************************************************************
	 * Find best discount 
	 **************************************************************/

	@Test
	void findBestDiscountSuccess_oneItemWithApplicableType() throws Exception {
		ArrayList<LineItem> lineItems = new ArrayList<>() {
			{
				add(new LineItem(EXPENSIVE_SHIRT, 1));
			}
		};

		List<Discount> discounts = new ArrayList<>() {
			{
				add(ITEM_TYPE_DISCOUNT_1);
				add(ITEM_COST_DISCOUNT_1);
			}
		};
		DiscountedCostDto expectedCost = new DiscountedCostDto(ITEM_TYPE_DISCOUNT_1.getCode(), 45);

		findBestDiscountSuccess(lineItems, discounts, expectedCost);
	}


	@Test
	void findBestDiscountSuccess_multiItemsOverMinCount() throws Exception {
		ArrayList<LineItem> lineItems = new ArrayList<>() {
			{
				add(new LineItem(EXPENSIVE_SHIRT, 5));
			}
		};

		List<Discount> discounts = new ArrayList<>() {
			{
				add(ITEM_TYPE_DISCOUNT_1);
				add(ITEM_COUNT_DISCOUNT_1);
				add(ITEM_COST_DISCOUNT_1);
			}
		};
		DiscountedCostDto expectedCost = new DiscountedCostDto(ITEM_COUNT_DISCOUNT_1.getCode(), 200);

		findBestDiscountSuccess(lineItems, discounts, expectedCost);
	}
	
	@Test
	void findBestDiscountSuccess_oneItemOverMinCost() throws Exception {
		ArrayList<LineItem> lineItems = new ArrayList<>() {
			{
				add(new LineItem(EXPENSIVE_SHIRT, 1));
				add(new LineItem(CHEAP_TV, 1));
			}
		};

		List<Discount> discounts = new ArrayList<>() {
			{
				add(ITEM_TYPE_DISCOUNT_1);
				add(ITEM_COST_DISCOUNT_1);
			}
		};
		DiscountedCostDto expectedCost = new DiscountedCostDto(ITEM_COST_DISCOUNT_1.getCode(), 305);

		findBestDiscountSuccess(lineItems, discounts, expectedCost);
	}	

	
	@Test
	void findBestDiscountSuccess_multiItemsOverMinCost() throws Exception {
		ArrayList<LineItem> lineItems = new ArrayList<>() {
			{
				add(new LineItem(EXPENSIVE_SHIRT, 1));
				add(new LineItem(CHEAP_TV, 2));
				add(new LineItem(EXPENSIVE_TV, 1));
			}
		};

		List<Discount> discounts = new ArrayList<>() {
			{
				add(ITEM_TYPE_DISCOUNT_1);
				add(ITEM_COST_DISCOUNT_1);
			}
		};
		DiscountedCostDto expectedCost = new DiscountedCostDto(ITEM_COST_DISCOUNT_1.getCode(), 2260);

		findBestDiscountSuccess(lineItems, discounts, expectedCost);
	}	

	@Test
	void findBestDiscountSuccess_noDiscountsPresent() throws Exception {
		ArrayList<LineItem> lineItems = new ArrayList<>() {
			{
				add(new LineItem(EXPENSIVE_SHIRT, 1));
				add(new LineItem(CHEAP_TV, 2));
				add(new LineItem(EXPENSIVE_TV, 1));
			}
		};

		List<Discount> discounts = new ArrayList<>();
		DiscountedCostDto expectedCost = new DiscountedCostDto(Discount.NO_DISCOUNT_CODE, 2650);

		findBestDiscountSuccess(lineItems, discounts, expectedCost);
	}	


	@Test
	void findBestDiscountSuccess_noDiscountsApplicable() throws Exception {
		ArrayList<LineItem> lineItems = new ArrayList<>() {
			{
				add(new LineItem(CHEAP_SHIRT, 1));
				add(new LineItem(EXPENSIVE_SHIRT, 1));
			}
		};

		List<Discount> discounts = new ArrayList<>() {
			{
				add(ITEM_COUNT_DISCOUNT_1);
				add(ITEM_COST_DISCOUNT_1);
			}
		};
		DiscountedCostDto expectedCost = new DiscountedCostDto(Discount.NO_DISCOUNT_CODE, 65);

		findBestDiscountSuccess(lineItems, discounts, expectedCost);
	}	

	@Test
	void findBestDiscountSuccess_lineitemAndDiscountHaveDifferentItemId() throws Exception {
		ArrayList<LineItem> lineItems = new ArrayList<>() {
			{
				add(new LineItem(EXPENSIVE_TV, 1));
			}
		};

		List<Discount> discounts = new ArrayList<>() {
			{
				add(ITEM_COUNT_DISCOUNT_1);
			}
		};
		DiscountedCostDto expectedCost = new DiscountedCostDto(Discount.NO_DISCOUNT_CODE, 2000);

		findBestDiscountSuccess(lineItems, discounts, expectedCost);
	}	
	
	
	// utility method
	private void findBestDiscountSuccess(List<LineItem> lineItems, List<Discount> discounts, DiscountedCostDto expectedCost)
			throws Exception {
		// save the discounts
		discounts.stream().forEach(d -> discountRepository.save(d));

		mockMvc.perform(get(BEST_DISCOUNT_URI).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("lineItems", objectMapper.writeValueAsString(lineItems)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.discountCode").value(expectedCost.discountCode()))
				.andExpect(jsonPath("$.amount").value(expectedCost.amount()));
	}

	@Test
	void findBestDiscountFailure_oneItemDifferentCosts() throws Exception {
		ArrayList<LineItem> lineItems = new ArrayList<>() {
			{
				Item duplicateItem = new Item(EXPENSIVE_SHIRT.getId(), EXPENSIVE_SHIRT.getType(), 2*EXPENSIVE_SHIRT.getCost());
				add(new LineItem(EXPENSIVE_SHIRT, 1));
				add(new LineItem(duplicateItem, 1));
			}
		};

		List<Discount> discounts = new ArrayList<>() {
			{
				add(ITEM_COUNT_DISCOUNT_1);
			}
		};
		
		findBestDiscountFailure(lineItems, discounts, HttpStatus.BAD_REQUEST.value());
	}	

	@Test
	void findBestDiscountFailure_oneItemDifferentTypes1() throws Exception {
		ArrayList<LineItem> lineItems = new ArrayList<>() {
			{
				Item duplicateItem = new Item(EXPENSIVE_SHIRT.getId(), CHEAP_TV.getType(), EXPENSIVE_SHIRT.getCost());
				add(new LineItem(EXPENSIVE_SHIRT, 1));
				add(new LineItem(duplicateItem, 1));
			}
		};

		List<Discount> discounts = new ArrayList<>() {
			{
				add(ITEM_TYPE_DISCOUNT_1);
			}
		};
		
		findBestDiscountFailure(lineItems, discounts, HttpStatus.BAD_REQUEST.value());
	}	
	@Test
	void findBestDiscountFailure_oneItemDifferentTypes2() throws Exception {
		ArrayList<LineItem> lineItems = new ArrayList<>() {
			{
				Item duplicateItem = new Item(EXPENSIVE_SHIRT.getId(), CHEAP_TV.getType(), EXPENSIVE_SHIRT.getCost());
				add(new LineItem(EXPENSIVE_SHIRT, 1));
				add(new LineItem(duplicateItem, 1));
			}
		};

		List<Discount> discounts = new ArrayList<>() {
			{
				add(ITEM_COST_DISCOUNT_1);
			}
		};
		
		findBestDiscountFailure(lineItems, discounts, HttpStatus.BAD_REQUEST.value());
	}	
	@Test
	void findBestDiscountFailure_oneItemDifferentTypes3() throws Exception {
		ArrayList<LineItem> lineItems = new ArrayList<>() {
			{
				Item duplicateItem = new Item(EXPENSIVE_SHIRT.getId(), CHEAP_TV.getType(), EXPENSIVE_SHIRT.getCost());
				add(new LineItem(EXPENSIVE_SHIRT, 1));
				add(new LineItem(duplicateItem, 1));
			}
		};

		List<Discount> discounts = new ArrayList<>() {
			{
				add(ITEM_COUNT_DISCOUNT_1);
			}
		};
		
		findBestDiscountFailure(lineItems, discounts, HttpStatus.BAD_REQUEST.value());
	}	

	@Test
	void findBestDiscountFailure_noLineItems() throws Exception {
		ArrayList<LineItem> lineItems = new ArrayList<>();

		List<Discount> discounts = new ArrayList<>() {
			{
				add(ITEM_TYPE_DISCOUNT_1);
				add(ITEM_COST_DISCOUNT_1);
			}
		};
		
		findBestDiscountFailure(lineItems, discounts, HttpStatus.BAD_REQUEST.value());
	}	

	@Test
	void findBestDiscountFailure_noHeader() throws Exception {
		mockMvc.perform(get(BEST_DISCOUNT_URI).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
	}	
	
	@Test
	void findBestDiscountFailure_invalidLineItems() throws Exception {
		mockMvc.perform(get(BEST_DISCOUNT_URI).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("lineItems", "[{\"count\": 2, \"item\": {\"cost\": 20, \"type\": \"CLOTHES\"}}]")) 
				.andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
	}	

	@Test
	void findBestDiscountFailure_malformedLineItems() throws Exception {
		mockMvc.perform(get(BEST_DISCOUNT_URI).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("lineItems", "[{\"count\": 2, ")) 
				.andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
	}	
	
	// utility method
	private void findBestDiscountFailure(List<LineItem> lineItems, List<Discount> discounts, int expectedStatus)
			throws Exception {
		// save the discounts
		discounts.stream().forEach(d -> discountRepository.save(d));

		mockMvc.perform(get(BEST_DISCOUNT_URI).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("lineItems", objectMapper.writeValueAsString(lineItems)))
				.andExpect(status().is(expectedStatus));
	}	
	
}
