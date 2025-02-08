package com.damodar.ranklist

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
class User(
    @Id val id: String,
    var password: String,
    var rankList: String
)

@Entity
class AllowedName(
    @Id @GeneratedValue val id: Long? = null,
    val name: String
)

@Entity
class AllowedEnrollmentNumber(
    @Id @GeneratedValue val id: Long? = null,
    val enrollmentNumber: String
)