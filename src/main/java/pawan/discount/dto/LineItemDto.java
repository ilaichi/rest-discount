package pawan.discount.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LineItemDto {
	private ItemDto item;
	private int count;
}