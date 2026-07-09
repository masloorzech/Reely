package com.example.reely.domain.usecase.user

import com.example.reely.domain.repository.UsersRepository
import javax.inject.Inject

class CreateUserUseCase @Inject constructor(
    private val repository: UsersRepository
) {

    suspend operator fun invoke(
        nickname : String,
        avatarPath : String?
    ){
        repository.addUser(
            nickname = nickname,
            avatarPath = avatarPath
        )
    }

}