package com.example.Customer;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;


@SpringBootApplication
@CrossOrigin
@RestController
public class CustomerApplication {
	@Autowired
	private CustomerRepository repository;
	private static final Logger log = LoggerFactory.getLogger(CustomerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(CustomerApplication.class, args);
	}

	@GetMapping("/customerInfo")
	public List<Customer> customerInfo() {
		log.info("Get: " + repository.findAll());
			return repository.findAll();
	}

	@PostMapping("/insertCustomer")
	public ResponseEntity<String> insertCustomer(@RequestBody Customer customer) {
		try {
			Customer savedCustomer = repository.save(customer);
			log.info("POST: Body:" + customer + ", insertedCustomer:" + savedCustomer.id);
			return new ResponseEntity<>("Customer created with ID: " + savedCustomer.id, HttpStatus.CREATED);
		} catch (Exception e) {
			log.info("Ex: " + e);
			return new ResponseEntity<>("Failed to create customer: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/updateCustomer")
	public ResponseEntity<Customer> updateCustomer(@RequestParam String id, @RequestBody Customer customer) {
		try	{
		Optional<Customer> customerData = repository.findById(id);

		if (customerData.isPresent()) {
			Customer updatedCustomer = customerData.get();
			updatedCustomer.nameAndSurname = customer.nameAndSurname;
			updatedCustomer.dateOfBirth = customer.dateOfBirth;
			updatedCustomer.gender = customer.gender;
			updatedCustomer.email = customer.email;
			updatedCustomer.telephone = customer.telephone;
			updatedCustomer.address = customer.address;
			updatedCustomer.typeOfSubscription = customer.typeOfSubscription;
			updatedCustomer.dateOfSubscription = customer.dateOfSubscription;
			updatedCustomer.paymentMethod = customer.paymentMethod;
			updatedCustomer.validTill = customer.validTill;
			updatedCustomer.status = customer.status;
			log.info("PUT: Body:"+customer +". Saved");
			return new ResponseEntity<>(repository.save(updatedCustomer), HttpStatus.OK);
		} else {
			log.info("PUT: Body:"+customer +". Invalid id");
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		}catch (Exception e){
			log.info("Exception:" + e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@DeleteMapping("deleteCustomer")
	public String deleteCustomer(@RequestParam String id){
		try {
			Optional<Customer> customerData = repository.findById(id);
			if (customerData.isPresent()){
				repository.deleteById(id);
				log.info("DELETE: Customer deleted.");
				return "OK";
			} else {
				log.info("DELETE: insertedId:" + id + ".Exception: Can not delete customer, cus this id do not exists in DB.");
				return "DELETE: Can not delete customer, cus this id do not exists in DB.";
			}

		}catch (Exception e){
			log.info("DELETE: insertedId:" + id + ".Exception:" + e);
			return e.toString();
		}

	}
}
