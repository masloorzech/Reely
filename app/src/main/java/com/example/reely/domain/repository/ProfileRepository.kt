package com.example.reely.domain.repository

import com.example.reely.domain.model.Profile
import com.example.reely.domain.model.ProfileId
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {

    fun observeProfiles(): Flow<List<Profile>>

    suspend fun getProfiles(): List<Profile>

    suspend fun areProfilesConfigured(): Boolean

    suspend fun createInitialProfiles(
        firstName: String,
        secondName: String,
    ): Result<Unit>

    suspend fun updateProfile(
        profileId: ProfileId,
        name: String,
        avatarPath: String?,
    ): Result<Unit>
}