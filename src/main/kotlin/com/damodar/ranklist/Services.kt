package com.damodar.ranklist

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class RankListService(
    private val userRepository: UserRepository,
    private val userService: UserService
) {
//    fun getUserRankList(id: String): List<RankRecord> {
//        val user = userRepository.findUserById(id) ?: return emptyList()
//        return user.rankList.split("\n").mapIndexed { index, name ->
//            RankRecord(index + 1, name)
//        }
//    }

    fun getUserRankList(id: String): String {
        val user = userService.getUser(id) ?: return "User not found"
        return user.rankList
    }

    fun upsertRankList(id: String, rankList: String) {
        val user = userService.getUser(id) ?: throw IllegalArgumentException("User not found")
        user.rankList = rankList
        userRepository.save(user)
    }
}

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    val ALLOWED_ENOLLMENT_NUMBERS = setOf("2111EC033","2111EC034")

    fun createUser(id: String, password: String) {
        if (!ALLOWED_ENOLLMENT_NUMBERS.contains(id)) {
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
