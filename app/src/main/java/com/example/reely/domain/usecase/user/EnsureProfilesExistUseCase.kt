package com.example.reely.domain.usecase.user

import com.example.reely.domain.repository.ProfileRepository
import javax.inject.Inject

class EnsureProfilesExistUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {

    suspend operator fun invoke(): Result<Unit> {
        return runCatching {
            val profiles = repository.getProfiles()

            when (profiles.size) {
                0 -> {
                    repository.createInitialProfiles(
                        firstName = DEFAULT_FIRST_PROFILE_NAME,
                        secondName = DEFAULT_SECOND_PROFILE_NAME,
                    ).getOrThrow()
                }

                2 -> Unit

                else -> error(
                    "Invalid profile count: ${profiles.size}"
                )
            }
        }
    }

    private companion object {
        const val DEFAULT_FIRST_PROFILE_NAME = "Ja"
        const val DEFAULT_SECOND_PROFILE_NAME = "Osoba 2"
    }
}