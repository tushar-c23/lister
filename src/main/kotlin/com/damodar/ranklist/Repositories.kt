package com.damodar.ranklist

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, String> {
    fun findUserById(id: String): User?
}

interface AllowedNameRepository: JpaRepository<AllowedName, Long> {
    fun findAllowedNameByName(name: String): AllowedName?
}

interface AllowedEnrollmentNumberRepository: JpaRepository<AllowedEnrollmentNumber, Long> {
    fun findAllowedEnrollmentNumberByEnrollmentNumber(enrollmentNumber: String): AllowedEnrollmentNumber?
}