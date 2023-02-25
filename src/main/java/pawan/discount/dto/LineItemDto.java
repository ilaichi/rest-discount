package pawan.discount.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LineItemDto {
	private ItemDto item;
	private int count;
}