package pawan.discount;

import pawan.discount.model.Discount;
import pawan.discount.model.Item;
import pawan.discount.model.ItemCostDiscount;
import pawan.discount.model.ItemCountDiscount;
import pawan.discount.model.ItemTypeDiscount;

public class DiscountTestData {
	
	public static final ItemTypeDiscount ITEM_TYPE_DISCOUNT_1 = 
			new ItemTypeDiscount("ABC", Discount.TYPE_ITEM_TYPE, 10, Item.TYPE_CLOTHES);
	public static final ItemCostDiscount ITEM_COST_DISCOUNT_1 = 
			new ItemCostDiscount("CDE", Discount.TYPE_ITEM_COST, 15, 100);
	public static final ItemCountDiscount ITEM_COUNT_DISCOUNT_1 = 
			new ItemCountDiscount("FGH", Discount.TYPE_ITEM_COUNT, 20, 123, 2);

	public static final Item CHEAP_SHIRT = new Item(122, Item.TYPE_CLOTHES, 15);
	public static final Item EXPENSIVE_SHIRT = new Item(123, Item.TYPE_CLOTHES, 50);
	public static final Item CHEAP_TV = new Item(456, Item.TYPE_ELECTRONICS, 300);
	public static final Item EXPENSIVE_TV = new Item(457, Item.TYPE_ELECTRONICS, 2000);
}
