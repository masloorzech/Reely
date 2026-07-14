package com.example.reely.data.local

import com.example.reely.data.local.room.entity.ProfileEntity
import kotlinx.coroutines.flow.Flow

interface ProfileLocalDataSource {

    fun observeProfiles(): Flow<List<ProfileEntity>>

    suspend fun getProfiles(): List<ProfileEntity>

    suspend fun getProfileById(
        profileId: Int,
    ): ProfileEntity?

    suspend fun areProfilesConfigured(): Boolean

    suspend fun createInitialProfiles(
        firstName: String,
        secondName: String,
    )

    suspend fun updateProfile(
        profileId: Int,
        name: String,
        avatarPath: String?,
    ): Boolean
}