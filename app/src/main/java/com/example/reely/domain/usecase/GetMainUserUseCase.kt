package com.example.reely.domain.usecase

import com.example.reely.domain.model.User
import com.example.reely.domain.repository.UsersRepository
import javax.inject.Inject

class GetMainUserUseCase @Inject constructor(
    private val repository: UsersRepository
) {

    suspend operator fun invoke() : Result<List<User>>{
        return repository.getUsers()
    }

}