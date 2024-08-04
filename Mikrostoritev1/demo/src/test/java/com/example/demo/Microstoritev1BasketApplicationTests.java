package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;


@SpringBootTest
class Microstoritev1BasketApplicationTests {
	 @Autowired
	    private WebApplicationContext webApplicationContext;

	    private MockMvc mockMvc;

	    @MockBean
	    private BasketRepository repository;

	    private Basket basket;

	    @BeforeEach
	    void setUp() {
	        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
	        basket = new Basket();
	        basket.id = "1"; 
	        basket.customerId = "customer1";
	        basket.typeOfSubscription = "OneTime";
	        basket.dateAndTime = LocalDateTime.now().toString();
	        basket.subscriptionFrom = LocalDate.now().toString();
	        basket.subscriptionTill =LocalDate.now().toString();
	        basket.status = "Voucher";
	    }

	    @Test
	    void testBasketInfo() throws Exception {
	        List<Basket> baskets = new ArrayList<>();
	        baskets.add(basket);
	        
	        when(repository.findAll()).thenReturn(baskets);
	        
	        mockMvc.perform(get("/basketInfo"))
	                .andExpect(status().isOk())
	                .andExpect(jsonPath("$[0].customerId").value("customer1"));
	    }

	    @Test
	    void testCreateBasket() throws Exception {
	        Basket savedBasket = new Basket();
	        savedBasket.id = "1";  
	        savedBasket.customerId = basket.customerId;
	        savedBasket.typeOfSubscription = basket.typeOfSubscription;
	        savedBasket.dateAndTime = basket.dateAndTime;
	        savedBasket.paymentMethod = basket.paymentMethod;
	        savedBasket.subscriptionFrom = basket.subscriptionFrom;
	        savedBasket.subscriptionTill = basket.subscriptionTill;
	        savedBasket.status = basket.status;

	        when(repository.save(any(Basket.class))).thenReturn(savedBasket);
	        
	        mockMvc.perform(post("/createBasket")
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(new ObjectMapper().writeValueAsString(basket)))
	                .andExpect(status().isCreated())
	                .andExpect(content().string("Basket created with ID: 1"));
	    }

	    @Test
	    void testUpdateBasket() throws Exception {
	        when(repository.findById("1")).thenReturn(Optional.of(basket));
	        when(repository.save(any(Basket.class))).thenReturn(basket);
	        
	        mockMvc.perform(put("/updateBasket?id=1")
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(new ObjectMapper().writeValueAsString(basket)))
	                .andExpect(status().isOk())
	                .andExpect(jsonPath("$.customerId").value("customer1"));
	    }

	    @Test
	    void testDeleteBasket() throws Exception {
	        when(repository.findById("1")).thenReturn(Optional.of(basket));
	        
	        mockMvc.perform(delete("/deleteBasket?id=1"))
	                .andExpect(status().isOk())
	                .andExpect(content().string("OK"));
	    }
	/*@Test
	void contextLoads() {
	}
*/
	
	
}
