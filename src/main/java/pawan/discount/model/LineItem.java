package pawan.discount.model;

import java.util.HashMap;
import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LineItem {
	private Item item;
	private int count;

	/**
	 * Check if an item appears multiple times it has the same item type and cost in each instance
	 * 
	 * @param lineItems
	 * @return true if consistent else false
	 */
	public static boolean areConsistent(List<LineItem> lineItems) {
		Boolean result = true;
		HashMap<Long, Item> map = new HashMap<>();
		
		for (LineItem x : lineItems) {
			long id = x.getItem().getId();
			if (map.containsKey(id)) {
				if (!x.getItem().equals(map.get(id))) {
					result = false;
					break;
				} else {
					// consistent
				}
			} else {
				map.put(x.getItem().getId(), x.getItem());
			}			
		}
		
		return result;
	}	
	
	public boolean checkValid() {
		return item.checkValid() && count > 0; 
	}

	public static boolean areValid(List<LineItem> lineItems) {
		return lineItems != null &&
				!lineItems.stream().anyMatch(li -> !li.checkValid());
	}
}
