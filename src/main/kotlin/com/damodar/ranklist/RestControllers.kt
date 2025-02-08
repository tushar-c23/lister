package com.damodar.ranklist

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rank")
class RankListControllers(
    private val rankListService: RankListService,
    private val userService: UserService
) {
    val rankList = mutableListOf<RankRecord>(
        RankRecord(0, "Sati"),
        RankRecord(1, "Nishchay")
    )

    @GetMapping
    fun getCommonList(): MutableList<RankRecord> {
        return rankList
    }

    @GetMapping("/{userId}")
    fun getUserRankList(@PathVariable userId: String): String {
        return rankListService.getUserRankList(userId)
    }

    @PostMapping("/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    fun upsertUserRankList(@RequestBody rankList: String, @PathVariable userId: String) {
        rankListService.upsertRankList(userId, rankList)
    }
}

@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserService
) {
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(@RequestBody user: UserDto) {
        userService.createUser(user.id, user.password)
    }

    @PostMapping("/login")
    fun authenticate(@RequestBody user: UserDto): Boolean {
        return userService.authenticate(user.id, user.password)
    }
}

@RestController
@RequestMapping("/names")
class AllowedNameController(
    private val allowedNameService: AllowedNameService
) {
    @GetMapping
    fun getAllowedNames(): List<String> {
        return allowedNameService.getAllowedNames()
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addAllowedName(@RequestBody allowedNames: AllowedNamesDto) {
        allowedNameService.addAllowedNames(allowedNames.names)
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteAllowedName(@RequestBody allowedNames: AllowedNamesDto) {
        allowedNames.names.forEach { name ->
            allowedNameService.deleteAllowedName(name)
        }
    }
}