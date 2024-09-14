package com.mikrostoritev2user.quarkus.grpc

import io.grpc.stub.StreamObserver
import io.quarkus.grpc.GrpcService
import jakarta.inject.Inject
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import org.bson.Document
import org.bson.types.ObjectId
import org.slf4j.LoggerFactory

@GrpcService
class MutinyCustomerService : CustomerServiceGrpc.CustomerServiceImplBase() {

    @Inject
    lateinit var mongoClient: MongoClient

    private val logger = LoggerFactory.getLogger(MutinyCustomerService::class.java)

    override fun getCustomer(
        request: CustomerOuterClass.CustomerGetRequest,
        responseObserver: StreamObserver<CustomerOuterClass.CustomerListResponse>
    ) {
        logger.info("Received request to get customer with name and surname: ${request.nameAndSurname}")

        val db = mongoClient.getDatabase("FitnessApp")
        val collection: MongoCollection<Document> = db.getCollection("customers")

        val customers = if (request.nameAndSurname.isNullOrEmpty()) {
            collection.find().map { docToCustomer(it) }.toList()
        } else {
            val filter = Document("nameAndSurname", request.nameAndSurname)
            collection.find(filter).map { docToCustomer(it) }.toList()
        }

        val responseBuilder = CustomerOuterClass.CustomerListResponse.newBuilder()
        customers.forEach { customer ->
            responseBuilder.addCustomers(
                CustomerOuterClass.Customer.newBuilder()
                    .setNameAndSurname(customer.nameAndSurname)
                    .setDateOfBirth(customer.dateOfBirth)
                    .setGender(customer.gender)
                    .setEmail(customer.email)
                    .setTelephone(customer.telephone)
                    .setAddress(customer.address)
                    .setNote(customer.note)
                    .setDateInserted(customer.dateInserted)
                    .setDateUpdated(customer.dateUpdated)
                    .setCustomerId(customer.customerId)
                    .build()
            )
        }

        val response = responseBuilder.build()
        responseObserver.onNext(response)
        responseObserver.onCompleted()
        logger.info("Successfully retrieved customer(s) with name and surname: ${request.nameAndSurname}")
    }

    override fun insertCustomer(
        request: CustomerOuterClass.Customer,
        responseObserver: StreamObserver<CustomerOuterClass.Customer>
    ) {
        logger.info("Received request to insert a new customer")

        val db = mongoClient.getDatabase("FitnessApp")
        val collection: MongoCollection<Document> = db.getCollection("customers")

        if (request.nameAndSurname.isNullOrBlank() ||
            request.dateOfBirth.isNullOrBlank() ||
            request.gender.isNullOrBlank() ||
            request.email.isNullOrBlank() ||
            request.telephone.isNullOrBlank() ||
            request.address.isNullOrBlank()
        ) {
            logger.error("Failed to insert customer, missing required fields")
            responseObserver.onError(
                IllegalArgumentException("All fields except customerId must be filled.")
            )
            return
        }

        val customerDoc = Document()
            .append("nameAndSurname", request.nameAndSurname)
            .append("dateOfBirth", request.dateOfBirth)
            .append("gender", request.gender)
            .append("email", request.email)
            .append("telephone", request.telephone)
            .append("address", request.address)
            .append("note", request.note ?: "")
            .append("dateInserted", request.dateInserted ?: "")
            .append("dateUpdated", request.dateUpdated ?: "")

        collection.insertOne(customerDoc)

        val insertedId = customerDoc.getObjectId("_id").toString()

        val insertedCustomer = CustomerOuterClass.Customer.newBuilder()
            .setNameAndSurname(request.nameAndSurname)
            .setDateOfBirth(request.dateOfBirth)
            .setGender(request.gender)
            .setEmail(request.email)
            .setTelephone(request.telephone)
            .setAddress(request.address)
            .setNote(request.note ?: "")
            .setDateInserted(request.dateInserted ?: "")
            .setDateUpdated(request.dateUpdated ?: "")
            .setCustomerId(insertedId)
            .build()

        responseObserver.onNext(insertedCustomer)
        responseObserver.onCompleted()
        logger.info("Successfully inserted new customer with ID: $insertedId")
    }

    override fun updateCustomer(
        request: CustomerOuterClass.Customer,
        responseObserver: StreamObserver<CustomerOuterClass.Customer>
    ) {
        logger.info("Received request to update customer with ID: ${request.customerId}")

        val db = mongoClient.getDatabase("FitnessApp")
        val collection: MongoCollection<Document> = db.getCollection("customers")

        if (request.customerId.isNullOrBlank()) {
            logger.error("Customer ID is missing in the update request")
            responseObserver.onError(IllegalArgumentException("Customer ID must be provided for update."))
            return
        }

        val filter = Document("_id", ObjectId(request.customerId))
        val existingCustomer = collection.find(filter).firstOrNull()

        if (existingCustomer == null) {
            logger.warn("Customer with ID ${request.customerId} not found for update.")
            responseObserver.onError(IllegalArgumentException("Customer with ID ${request.customerId} not found."))
            return
        }

        val updateDoc = Document()
        if (!request.nameAndSurname.isNullOrBlank()) updateDoc.append("nameAndSurname", request.nameAndSurname)
        if (!request.dateOfBirth.isNullOrBlank()) updateDoc.append("dateOfBirth", request.dateOfBirth)
        if (!request.gender.isNullOrBlank()) updateDoc.append("gender", request.gender)
        if (!request.email.isNullOrBlank()) updateDoc.append("email", request.email)
        if (!request.telephone.isNullOrBlank()) updateDoc.append("telephone", request.telephone)
        if (!request.address.isNullOrBlank()) updateDoc.append("address", request.address)
        if (!request.note.isNullOrBlank()) updateDoc.append("note", request.note)
        updateDoc.append("dateUpdated", request.dateUpdated ?: System.currentTimeMillis().toString())

        val updateResult = collection.updateOne(filter, Document("\$set", updateDoc))

        if (updateResult.matchedCount == 0L) {
            logger.error("Failed to update customer with ID ${request.customerId}")
            responseObserver.onError(IllegalArgumentException("Customer with ID ${request.customerId} not found for update."))
            return
        }

        val updatedCustomerDoc = collection.find(filter).firstOrNull()

        if (updatedCustomerDoc == null) {
            logger.error("Failed to fetch updated customer with ID ${request.customerId}")
            responseObserver.onError(IllegalStateException("Failed to fetch updated customer with ID ${request.customerId}."))
            return
        }

        val updatedCustomer = CustomerOuterClass.Customer.newBuilder()
            .setNameAndSurname(updatedCustomerDoc.getString("nameAndSurname"))
            .setDateOfBirth(updatedCustomerDoc.getString("dateOfBirth"))
            .setGender(updatedCustomerDoc.getString("gender"))
            .setEmail(updatedCustomerDoc.getString("email"))
            .setTelephone(updatedCustomerDoc.getString("telephone"))
            .setAddress(updatedCustomerDoc.getString("address"))
            .setNote(updatedCustomerDoc.getString("note") ?: "")
            .setDateInserted(updatedCustomerDoc.getString("dateInserted"))
            .setDateUpdated(updatedCustomerDoc.getString("dateUpdated"))
            .setCustomerId(request.customerId)
            .build()

        responseObserver.onNext(updatedCustomer)
        responseObserver.onCompleted()
        logger.info("Successfully updated customer with ID: ${request.customerId}")
    }

    override fun deleteCustomer(
        request: CustomerOuterClass.CustomerDeleteRequest,
        responseObserver: StreamObserver<CustomerOuterClass.Customer>
    ) {
        logger.info("Received request to delete customer with ID: ${request.customerId}")

        val db = mongoClient.getDatabase("FitnessApp")
        val collection: MongoCollection<Document> = db.getCollection("customers")

        if (request.customerId.isNullOrBlank()) {
            logger.error("Customer ID is missing in the delete request")
            responseObserver.onError(IllegalArgumentException("Customer ID must be provided for deletion."))
            return
        }

        val filter = Document("_id", ObjectId(request.customerId))
        val existingCustomer = collection.find(filter).firstOrNull()

        if (existingCustomer == null) {
            logger.warn("Customer with ID ${request.customerId} not found for deletion.")
            responseObserver.onError(IllegalArgumentException("Customer with ID ${request.customerId} not found."))
            return
        }

        val deleteResult = collection.deleteOne(filter)

        if (deleteResult.deletedCount == 0L) {
            logger.error("Failed to delete customer with ID: ${request.customerId}")
            responseObserver.onError(IllegalStateException("Failed to delete customer with ID ${request.customerId}."))
            return
        }

        val deletedCustomer = CustomerOuterClass.Customer.newBuilder()
            .setNameAndSurname(existingCustomer.getString("nameAndSurname"))
            .setDateOfBirth(existingCustomer.getString("dateOfBirth"))
            .setGender(existingCustomer.getString("gender"))
            .setEmail(existingCustomer.getString("email"))
            .setTelephone(existingCustomer.getString("telephone"))
            .setAddress(existingCustomer.getString("address"))
            .setNote(existingCustomer.getString("note") ?: "")
            .setDateInserted(existingCustomer.getString("dateInserted"))
            .setDateUpdated(existingCustomer.getString("dateUpdated"))
            .setCustomerId(request.customerId)
            .build()

        responseObserver.onNext(deletedCustomer)
        responseObserver.onCompleted()
        logger.info("Successfully deleted customer with ID: ${request.customerId}")
    }

    // Helper function to convert a MongoDB document to a Customer
    private fun docToCustomer(document: Document): Customer {
        return Customer(
            nameAndSurname = document.getString("nameAndSurname") ?: "Unknown Name",
            dateOfBirth = document.getString("dateOfBirth") ?: "Unknown Date",
            gender = document.getString("gender") ?: "Unknown Gender",
            email = document.getString("email") ?: "Unknown Email",
            telephone = document.getString("telephone") ?: "Unknown Telephone",
            address = document.getString("address") ?: "Unknown Address",
            note = document.getString("note") ?: "No Note",
            dateInserted = document.getString("dateInserted") ?: "Unknown Date",
            dateUpdated = document.getString("dateUpdated") ?: "Unknown Date",
            customerId = document.getObjectId("_id")?.toString() ?: "Unknown ID"
        )
    }
}
