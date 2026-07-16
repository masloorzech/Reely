package com.example.reely.domain.usecase.user

import com.example.reely.domain.repository.ProfileRepository
import javax.inject.Inject

class CreateInitialProfilesUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {

    suspend operator fun invoke(
        firstProfileName: String,
        secondProfileName: String,
    ): Result<Unit> {
        val firstName = firstProfileName.trim()
        val secondName = secondProfileName.trim()

        if (firstName.isBlank()) {
            return Result.failure(
                IllegalArgumentException(
                    "First profile name cannot be blank"
                )
            )
        }

        if (secondName.isBlank()) {
            return Result.failure(
                IllegalArgumentException(
                    "Second profile name cannot be blank"
                )
            )
        }

        return repository.createInitialProfiles(
            firstName = firstName,
            secondName = secondName,
        )
    }
}