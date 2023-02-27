package pawan.discount.dto;

/**
 * Data object to hold discount code and the discounted amount
 * @author pawan
 */
public record DiscountedCostDto(String discountCode, double amount) {}
