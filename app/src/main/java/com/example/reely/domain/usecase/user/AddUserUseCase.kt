package com.example.reely.domain.usecase.user

import com.example.reely.domain.model.User
import com.example.reely.domain.repository.UsersRepository
import javax.inject.Inject

class AddUserUseCase @Inject constructor(
    private val repository: UsersRepository
) {
    suspend operator fun invoke(
        nickname: String,
        avatarPath: String? = null
    ): Result<User> {
        return repository.addUser(
            nickname = nickname,
            avatarPath = avatarPath
        )
    }
}