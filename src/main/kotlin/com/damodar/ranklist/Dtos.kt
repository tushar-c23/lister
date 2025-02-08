package com.damodar.ranklist

data class UserDto(
    val id: String,
    val password: String
)

data class AllowedNamesDto(
    val names: List<String>
)