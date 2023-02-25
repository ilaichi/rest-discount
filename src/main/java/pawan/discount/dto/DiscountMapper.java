package pawan.discount.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import pawan.discount.exception.MalformedRequestException;
import pawan.discount.model.Discount;
import pawan.discount.model.ItemCostDiscount;
import pawan.discount.model.ItemCountDiscount;
import pawan.discount.model.ItemTypeDiscount;

@Mapper
public interface DiscountMapper {
	
	DiscountMapper INSTANCE = Mappers.getMapper( DiscountMapper.class );

	default Discount mapToDiscount(DiscountDto discountDto)	
	{
		if (discountDto == null || discountDto.getType() == null) {
			throw new MalformedRequestException("Discount and its type cannot be null");
		}
		
		return switch (discountDto.getType()) {
		case Discount.TYPE_ITEM_COST: {
			yield new ItemCostDiscount(discountDto.getCode(), discountDto.getType(), discountDto.getPercent(), discountDto.getMinCost());
		}
		case Discount.TYPE_ITEM_COUNT: {
			yield new ItemCountDiscount(discountDto.getCode(), discountDto.getType(), discountDto.getPercent(), discountDto.getItemId(), discountDto.getMinCount());
		}
		case Discount.TYPE_ITEM_TYPE: {
			yield new ItemTypeDiscount(discountDto.getCode(), discountDto.getType(), discountDto.getPercent(), discountDto.getItemType());
		}
		default:
			throw new MalformedRequestException("Unexpected value: " + discountDto.getType());
		};		
	}

}
