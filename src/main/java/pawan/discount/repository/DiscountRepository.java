package pawan.discount.repository;

import org.springframework.data.repository.CrudRepository;

import pawan.discount.model.Discount;

public interface DiscountRepository extends CrudRepository<Discount, String> {

}
