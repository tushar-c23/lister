package com.damodar.ranklist

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class RankListService(
    private val userRepository: UserRepository,
    private val userService: UserService
) {
    private val objectMapper = jacksonObjectMapper()

    fun getUserRankList(id: String): RankListDto {
        val user = userService.getUser(id) ?: throw IllegalArgumentException("User not found")
        try {
            val rankListDto = objectMapper.readValue<RankListDto>(user.rankList)
            return rankListDto
        } catch (e: Exception) {
            return RankListDto(
                listOf(
                    RankRecord(
                        0,
                        "Shashank Sati"
                    )
                )
            )
        }
    }

    fun upsertRankList(id: String, rankList: RankListDto) {
        val user = userService.getUser(id) ?: throw IllegalArgumentException("User not found")
        user.rankList = objectMapper.writeValueAsString(rankList)
        userRepository.save(user)
    }

    fun generateCommonRankList(): RankListDto {
        val users = userRepository.findAll()
        val rankMap = mutableMapOf<String, MutableList<Int>>()

        users.forEach { user ->
            val rankListDto = objectMapper.readValue<RankListDto>(user.rankList)
            rankListDto.rankList.forEach { rankRecord ->
                rankMap.computeIfAbsent(rankRecord.name) { mutableListOf() }.add(rankRecord.rank)
            }
        }

        val averageRankList = rankMap.map { (name, ranks) ->
            val averageRank = ranks.average()
            RankRecord(averageRank.toInt(), name)
        }.sortedBy { it.rank }

        return RankListDto(averageRankList.take(10))
    }
}

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val allowedEnrollmentService: AllowedEnrollmentService
) {

    fun createUser(id: String, password: String) {
        if (!allowedEnrollmentService.isAllowed(id)) {
            throw IllegalArgumentException("Invalid enrollment number")
        }

        val hashedPassword = passwordEncoder.encode(password)
        val hashedId = passwordEncoder.encode(id)
        userRepository.save(User(hashedId, hashedPassword, ""))
    }

    fun authenticate(id: String, password: String): Boolean {
        val user = getUser(id) ?: return false
        return passwordEncoder.matches(password, user.password)
    }

    fun getUser(id: String): User? {
        val users = userRepository.findAll()
        val user = users.find { passwordEncoder.matches(id, it.id) } ?: return null
        return user
    }
}

@Service
class AllowedNameService(
    private val allowedNameRepository: AllowedNameRepository
) {
    fun isAllowed(name: String): Boolean {
        return allowedNameRepository.findAllowedNameByName(name) != null
    }

    fun getAllowedNames(): List<String> {
        return allowedNameRepository.findAll().map { it.name }
    }

    fun addAllowedNames(names: List<String>) {
        val namesInDb = allowedNameRepository.findAll().map { it.name }
        names.forEach {
            if (!namesInDb.contains(it)) {
                allowedNameRepository.save(AllowedName(name = it))
            }
        }
    }

    fun deleteAllowedName(name: String) {
        val allowedName = allowedNameRepository.findAllowedNameByName(name) ?: throw IllegalArgumentException("Name not found")
        allowedNameRepository.delete(allowedName)
    }
}

@Service
class AllowedEnrollmentService(
    private val allowedEnrollmentNumberRepository: AllowedEnrollmentNumberRepository
) {
    fun isAllowed(enrollmentNumber: String): Boolean {
        return allowedEnrollmentNumberRepository.findAllowedEnrollmentNumberByEnrollmentNumber(enrollmentNumber) != null
    }

    fun getAllowedEnrollmentNumbers(): List<String> {
        return allowedEnrollmentNumberRepository.findAll().map { it.enrollmentNumber }
    }

    fun addAllowedEnrollmentNumbers(enrollmentNumbers: List<String>) {
        val enrollmentNumbersInDb = allowedEnrollmentNumberRepository.findAll().map { it.enrollmentNumber }
        enrollmentNumbers.forEach {
            if (!enrollmentNumbersInDb.contains(it)) {
                allowedEnrollmentNumberRepository.save(AllowedEnrollmentNumber(enrollmentNumber = it))
            }
        }
    }

    fun deleteAllowedEnrollmentNumber(enrollmentNumber: String) {
        val allowedEnrollmentNumber = allowedEnrollmentNumberRepository.findAllowedEnrollmentNumberByEnrollmentNumber(enrollmentNumber) ?: throw IllegalArgumentException("Enrollment number not found")
        allowedEnrollmentNumberRepository.delete(allowedEnrollmentNumber)
    }
}