package com.example.reely.domain.usecase.user

import com.example.reely.domain.model.ProfileId
import com.example.reely.domain.repository.ProfileRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {

    suspend operator fun invoke(
        profileId: ProfileId,
        name: String,
        avatarPath: String?,
    ): Result<Unit> {
        val normalizedName = name.trim()

        if (normalizedName.isBlank()) {
            return Result.failure(
                IllegalArgumentException(
                    "Profile name cannot be blank"
                )
            )
        }

        return repository.updateProfile(
            profileId = profileId,
            name = normalizedName,
            avatarPath = avatarPath,
        )
    }
}