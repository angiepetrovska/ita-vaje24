package com.example.demo;


import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface BasketRepository extends MongoRepository<Basket, String> {
	List<Basket> findByCustomerId(String customerId);
}
