package com.example.reely.data.local.room

import com.example.reely.data.local.ProfileLocalDataSource
import com.example.reely.data.local.room.dao.ProfileDao
import com.example.reely.data.local.room.entity.ProfileEntity
import com.example.reely.data.local.room.transaction.RoomProfileTransactions
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import java.time.Clock


class RoomProfileLocalDataSource @Inject constructor(
    private val profileDao: ProfileDao,
    private val profileTransactions: RoomProfileTransactions,
    private val clock: Clock,
) : ProfileLocalDataSource {

    override fun observeProfiles(): Flow<List<ProfileEntity>> =
        profileDao.observeAll()

    override suspend fun getProfiles(): List<ProfileEntity> =
        profileDao.getAll()

    override suspend fun getProfileById(
        profileId: Int,
    ): ProfileEntity? =
        profileDao.getById(profileId)

    override suspend fun areProfilesConfigured(): Boolean =
        profileDao.count() == 2

    override suspend fun createInitialProfiles(
        firstName: String,
        secondName: String,
    ) {
        profileTransactions.createInitialProfiles(
            firstName = firstName,
            secondName = secondName,
        )
    }

    override suspend fun updateProfile(
        profileId: Int,
        name: String,
        avatarPath: String?,
    ): Boolean {
        require(name.isNotBlank())

        val updatedRows = profileDao.update(
            profileId = profileId,
            name = name.trim(),
            avatarPath = avatarPath,
            updatedAt = clock.instant(),
        )

        return updatedRows == 1
    }
}