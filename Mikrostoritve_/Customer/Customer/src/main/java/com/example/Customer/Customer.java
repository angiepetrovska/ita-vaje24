package com.example.Customer;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "customers")
public class Customer {
    @Id
    public String id;
    public String nameAndSurname;
    public String dateOfBirth;
    public String gender;
    public String email;
    public String telephone;
    public String address;
    public String typeOfSubscription;
    public String dateOfSubscription;
    public String paymentMethod;
    public String validTill;
    public  String status;

}
