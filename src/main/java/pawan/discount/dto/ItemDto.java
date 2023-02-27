package pawan.discount.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemDto {
	private long id;
	private String type;
	private double cost;
}
