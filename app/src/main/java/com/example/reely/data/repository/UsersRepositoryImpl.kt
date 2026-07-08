package com.example.reely.data.repository

import com.example.reely.data.local.UsersLocalDataSource
import com.example.reely.data.local.mapper.toDomain
import com.example.reely.domain.model.User
import com.example.reely.domain.model.UserId
import com.example.reely.domain.repository.UsersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UsersRepositoryImpl @Inject constructor(
    private val localDataSource: UsersLocalDataSource
) : UsersRepository {

    override fun observeUsers(): Flow<List<User>> {
        return localDataSource.observeUsers()
            .map { users ->
                users.map { user ->
                    user.toDomain()
                }
            }
    }

    override suspend fun getUsers(): Result<List<User>> {
        return runCatching {
            localDataSource.getUsers()
                .map { user ->
                    user.toDomain()
                }
        }
    }

    override suspend fun ensureMainUser(): Result<User> {
        return runCatching {
            val existingMainUser = localDataSource.getMainUser()

            if (existingMainUser != null) {
                existingMainUser.toDomain()
            } else {
                localDataSource.addUser(
                    nickname = "Ja",
                    avatarPath = null
                ).toDomain()
            }
        }
    }

    override suspend fun addUser(
        nickname: String,
        avatarPath: String?
    ): Result<User> {
        return runCatching {
            localDataSource.addUser(
                nickname = nickname,
                avatarPath = avatarPath
            ).toDomain()
        }
    }

    override suspend fun deleteUser(userId: UserId): Result<Unit> {
        return runCatching {
            localDataSource.deleteUser(userId.value)
        }
    }
}