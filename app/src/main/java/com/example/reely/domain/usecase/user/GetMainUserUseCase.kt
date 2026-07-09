package com.example.reely.domain.usecase.user

import com.example.reely.domain.model.User
import com.example.reely.domain.repository.UsersRepository
import javax.inject.Inject

class GetMainUserUseCase @Inject constructor(
    private val repository: UsersRepository
) {

    suspend operator fun invoke() : Result<User>{
        return repository.getMainUser()
    }

}