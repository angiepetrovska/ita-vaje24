package com.example.demo;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "basket")
public class Basket {
    @Id
    public String id;
    public String customerId;
    public String typeOfSubscription;
    public String dateAndTime;
    public String paymentMethod;
    public String subscriptionFrom;
    public String subscriptionTill;
    public  String status;

}
