package pawan.discount.dto;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import pawan.discount.model.Item;
import pawan.discount.model.LineItem;

@Mapper
public interface LineItemMapper {
	
	LineItemMapper INSTANCE = Mappers.getMapper( LineItemMapper.class );

	LineItem map(LineItemDto liDto);
	Item map(ItemDto itemDto);
}
