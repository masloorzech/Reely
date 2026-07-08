package com.example.reely.domain.usecase

import com.example.reely.domain.model.User
import com.example.reely.domain.repository.UsersRepository
import jakarta.inject.Inject

class EnsureMainUserUseCase @Inject constructor(
    private val repository: UsersRepository
) {
    suspend operator fun invoke(): Result<User> {
        return repository.ensureMainUser()
    }
}