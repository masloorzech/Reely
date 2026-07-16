package com.example.reely.data.repository

import com.example.reely.data.local.LocalMediaDataSource
import com.example.reely.data.local.ProfileLocalDataSource
import com.example.reely.data.local.room.mapper.toDomain
import com.example.reely.domain.model.Profile
import com.example.reely.domain.model.ProfileId
import com.example.reely.domain.repository.ProfileRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val localDataSource: ProfileLocalDataSource,
    private val localMediaDataSource: LocalMediaDataSource,
    ) : ProfileRepository {

    override fun observeProfiles(): Flow<List<Profile>> {
        return localDataSource
            .observeProfiles()
            .map { entities ->
                entities.map { entity ->
                    entity.toDomain()
                }
            }
    }

    override suspend fun getProfiles(): List<Profile> {
        return localDataSource
            .getProfiles()
            .map { entity ->
                entity.toDomain()
            }
    }

    override suspend fun areProfilesConfigured(): Boolean {
        return localDataSource.areProfilesConfigured()
    }

    override suspend fun createInitialProfiles(
        firstName: String,
        secondName: String,
    ): Result<Unit> {
        return runCatching {
            localDataSource.createInitialProfiles(
                firstName = firstName,
                secondName = secondName,
            )
        }
    }

    override suspend fun updateProfile(
        profileId: ProfileId,
        name: String,
        avatarPath: String?,
    ): Result<Unit> {
        return runCatching {
            val storedAvatarPath = when {
                avatarPath.isNullOrBlank() -> null

                avatarPath.startsWith("content://") -> {
                    localMediaDataSource.copyImageToAppStorage(
                        sourceUri = avatarPath,
                        directoryName = PROFILE_AVATARS_DIRECTORY,
                    )
                }

                else -> {
                    avatarPath
                }
            }

            val updated = localDataSource.updateProfile(
                profileId = profileId.value,
                name = name.trim(),
                avatarPath = storedAvatarPath,
            )

            check(updated) {
                "Profile ${profileId.value} could not be updated"
            }
        }
    }
    private companion object {
        const val PROFILE_AVATARS_DIRECTORY = "profile_avatars"
    }
}