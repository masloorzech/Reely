package com.example.reely.domain.usecase.user

import com.example.reely.domain.model.User
import com.example.reely.domain.repository.UsersRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUsersUseCase @Inject constructor(
    private val repository: UsersRepository
){

    suspend operator fun invoke(): Flow<List<User>> {
        return repository.observeUsers()
    }

}