package com.damodar.ranklist

data class UserDto(
    val id: String,
    val password: String
)

data class AllowedNamesDto(
    val names: List<String>
)

data class AllowedEnrollmentDto(
    val enrollmentNumber: List<String>
)

data class RankListDto(
    val rankList: List<RankRecord>
)

data class RankRecord(
    val rank: Int,
    val name: String,
)