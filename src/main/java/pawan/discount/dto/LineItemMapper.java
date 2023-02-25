package pawan.discount.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import pawan.discount.exception.MalformedRequestException;
import pawan.discount.model.Discount;
import pawan.discount.model.Item;
import pawan.discount.model.ItemCostDiscount;
import pawan.discount.model.ItemCountDiscount;
import pawan.discount.model.ItemTypeDiscount;
import pawan.discount.model.LineItem;

@Mapper
public interface LineItemMapper {
	
	LineItemMapper INSTANCE = Mappers.getMapper( LineItemMapper.class );

	LineItem map(LineItemDto liDto);
	Item map(ItemDto itemDto);
}
