package com.mikrostoritev2user.quarkus.grpc

    data class Customer(
    val nameAndSurname: String,
    val dateOfBirth: String,
    val gender: String,
    val email: String,
    val telephone: String,
    val address: String,
    val note: String,
    val dateInserted: String,
    val dateUpdated: String,
    val customerId: String
    )