package com.example.reely.data.local.room.transaction

import androidx.room.withTransaction
import com.example.reely.data.local.room.ReelyDatabase
import com.example.reely.data.local.room.dao.ProfileDao
import com.example.reely.data.local.room.entity.ProfileEntity
import com.example.reely.domain.model.ProfilePosition
import java.time.Clock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomProfileTransactions @Inject constructor(
    private val database: ReelyDatabase,
    private val profileDao: ProfileDao,
    private val clock: Clock,
) {

    suspend fun createInitialProfiles(
        firstName: String,
        secondName: String,
    ) {
        require(firstName.isNotBlank()) {
            "First profile name cannot be blank"
        }

        require(secondName.isNotBlank()) {
            "Second profile name cannot be blank"
        }

        database.withTransaction {
            check(profileDao.count() == 0) {
                "Profiles have already been configured"
            }

            val now = clock.instant()

            profileDao.insertAll(
                listOf(
                    ProfileEntity(
                        id = 1,
                        position = ProfilePosition.FIRST,
                        name = firstName.trim(),
                        avatarPath = null,
                        createdAt = now,
                        updatedAt = now,
                    ),
                    ProfileEntity(
                        id = 2,
                        position = ProfilePosition.SECOND,
                        name = secondName.trim(),
                        avatarPath = null,
                        createdAt = now,
                        updatedAt = now,
                    ),
                )
            )

            check(profileDao.count() == 2) {
                "Exactly two profiles must be created"
            }
        }
    }
}