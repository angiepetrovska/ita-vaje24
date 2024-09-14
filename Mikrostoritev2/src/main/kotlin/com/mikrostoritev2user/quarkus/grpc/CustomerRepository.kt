package com.mikrostoritev2user.quarkus.grpc

import com.mongodb.client.model.Filters
import io.smallrye.mutiny.Uni
import org.bson.Document
import com.mongodb.client.MongoClient
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class CustomerRepository(val mongoClient: MongoClient) {

    private val collection = mongoClient.getDatabase("FitnessApp")
        .getCollection("customers", Document::class.java)

    fun findCustomersByName(nameAndSurname: String): Uni<List<Customer>> {
      return Uni.createFrom().item {
      val filter = Filters.regex("nameAndSurname", "^$nameAndSurname$", "i")  // Case-insensitive
        val customers = collection.find(filter).map { docToCustomer(it) }.toList()
        customers
      }
    }

    private fun docToCustomer(document: Document): Customer {
        return Customer(
            nameAndSurname = document.getString("nameAndSurname"),
            dateOfBirth = document.getString("dateOfBirth"),
            gender = document.getString("gender"),
            email = document.getString("email"),
            telephone = document.getString("telephone"),
            address = document.getString("address"),
            note = document.getString("note"),
            dateInserted = document.getString("dateInserted"),
            dateUpdated = document.getString("dateUpdated"),
            customerId = document.getObjectId("_id").toString()
        )
    }
}
