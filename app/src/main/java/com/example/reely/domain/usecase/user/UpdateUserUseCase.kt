package com.example.reely.domain.usecase.user

import com.example.reely.domain.model.UserId
import com.example.reely.domain.repository.UsersRepository
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val repository: UsersRepository
) {

    suspend operator fun invoke(
        userId : UserId,
        nickname : String,
        avatarPath : String?
    ){
        repository.updateUserUsername(
            userId,
            nickname
        )
        if (avatarPath!=null){
            repository.updateUserAvatar(
                userId,
                avatarPath
            )
        }
    }

}