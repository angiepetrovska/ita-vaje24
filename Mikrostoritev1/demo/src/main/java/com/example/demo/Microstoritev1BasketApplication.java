package com.example.demo;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;

@OpenAPIDefinition(info = @Info(title = "Basket API", version = "1.0", description = "Documentation Basket API v1.0"))
@SpringBootApplication
@CrossOrigin
@RestController
public class Microstoritev1BasketApplication {
	@Autowired
	private BasketRepository repository;
	private static final Logger log = LoggerFactory.getLogger(Microstoritev1BasketApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(Microstoritev1BasketApplication.class, args);
	}


	@GetMapping("/basketInfo")
	public List<Basket> basketInfo(@RequestParam(required = false) String customerId) {
		List<Basket> result = null;
		try {
	    
		    if (customerId != null) {
		        result = repository.findByCustomerId(customerId);
		    } else {
		       
		        result = repository.findAll();
		    }
		    
		    log.info("Get mapping - basketInfo: numberOfElements" + result.size());
		    
		}catch (Exception e){
			log.info("Get mapping - basketInfo:: Exception:" + e);
		}
		return result;
	}

	@PostMapping("/createBasket")
	public ResponseEntity<String> createBasket(@RequestBody Basket basket) {
		try {
			Basket saved = repository.save(basket);
			log.info("Post mapping - createBaskert: Body:" + basket + ", createdBasket:" + saved.id);
			return new ResponseEntity<>("Basket created with ID: " + saved.id, HttpStatus.CREATED);
		} catch (Exception e) {
			log.info("Ex: " + e);
			return new ResponseEntity<>("Failed to create basket: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/updateBasket")
	public ResponseEntity<Basket> updateBasket(@RequestParam String id, @RequestBody Basket basket) {
		try	{
		Optional<Basket> basketData = repository.findById(id);

		if (basketData.isPresent()) {
			Basket updatedBasket = basketData.get();
			updatedBasket.customerId = basket.customerId;
			updatedBasket.typeOfSubscription = basket.typeOfSubscription;
			updatedBasket.dateAndTime = basket.dateAndTime;
			updatedBasket.paymentMethod = basket.paymentMethod;
			updatedBasket.subscriptionFrom = basket.subscriptionFrom;
			updatedBasket.subscriptionTill = basket.subscriptionTill;
			updatedBasket.typeOfSubscription = basket.typeOfSubscription;
			updatedBasket.status = basket.status;
			
			log.info("Put mapping - updateBasket: Body:"+basket +". Saved");
			return new ResponseEntity<>(repository.save(updatedBasket), HttpStatus.OK);
		} else {
			log.info("Put mapping - updateBasket: Body:"+basket +". Invalid id");
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		}catch (Exception e){
			log.info("Put mapping - updateBasket: Exception:" + e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@DeleteMapping("deleteBasket")
	public String deleteBasket(@RequestParam String id){
		try {
			Optional<Basket> data = repository.findById(id);
			if (data.isPresent()){
				repository.deleteById(id);
				log.info("Delete mapping - deleteBasket: Basket deleted. ID:" + id);
				return "OK";
			} else {
				log.info("Delete mapping - deleteBasket: insertedId:" + id + ".Exception: Can not delete customer, cus this id do not exists in DB.");
				return "DELETE: Can not delete basket, cus this id do not exists in DB.";
			}

		}catch (Exception e){
			log.info("Delete mapping - deleteBasket: insertedId:" + id + ".Exception:" + e);
			return e.toString();
		}

	}


}
