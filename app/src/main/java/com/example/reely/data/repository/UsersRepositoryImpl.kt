package com.example.reely.data.repository

import com.example.reely.data.local.LocalMediaDataSource
import com.example.reely.data.local.mapper.toDomain
import com.example.reely.data.local.model.LocalUser
import com.example.reely.domain.model.User
import com.example.reely.domain.model.UserId
import com.example.reely.domain.repository.UsersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UsersRepositoryImpl @Inject constructor(
    private val usersLocalDataSource: UsersLocalDataSource,
    private val mediaLocalDataSource: LocalMediaDataSource
) : UsersRepository {

    override fun observeUsers(): Flow<List<User>> {
        return usersLocalDataSource.observeUsers()
            .map { users ->
                users.map { user ->
                    user.toDomain()
                }
            }
    }

    override suspend fun getUsers(): Result<List<User>> {
        return runCatching {
            usersLocalDataSource.getUsers()
                .map { user ->
                    user.toDomain()
                }
        }
    }

    override suspend fun ensureMainUser(): Result<User> {
        return runCatching {
            val existingMainUser = usersLocalDataSource.getMainUserOrNull()

            if (existingMainUser != null) {
                existingMainUser.toDomain()
            } else {
                val newUser = LocalUser(
                    id = 0,
                    nickname = "Ja",
                    avatarPath = null,
                )

                val newId = usersLocalDataSource.insertUser(newUser)

                usersLocalDataSource.getUser(newId.toInt())!!.toDomain()
            }
        }
    }

    override suspend fun getMainUser(): Result<User>{
        return runCatching {
            usersLocalDataSource.getMainUserOrNull()!!.toDomain()
        }
    }


    override suspend fun addUser(
        nickname: String,
        avatarPath: String?
    ): Result<User> {
        return runCatching {
            usersLocalDataSource.addUser(
                nickname = nickname,
                avatarPath = avatarPath
            ).toDomain()
        }
    }

    override suspend fun deleteUser(userId: UserId): Result<Unit> {
        return runCatching {
            val user = usersLocalDataSource.getUser(userId.value)
            mediaLocalDataSource.deleteFile(user?.avatarPath)
            usersLocalDataSource.deleteUser(userId.value)
        }
    }

    override suspend fun updateUserAvatar(
        userId: UserId,
        sourceUri: String
    ): Result<User> {
        return runCatching {
            val path = mediaLocalDataSource.copyImageToAppStorage(
                sourceUri,
                directoryName = "avatar"
            )
            usersLocalDataSource.updateAvatar(userId.value, path).toDomain()
        }
    }

    override suspend fun updateUserUsername(
        userId: UserId,
        username: String
    ): Result<Unit> {

        return runCatching {
            usersLocalDataSource.updateNickname(
                userId.value,
                username
            )
        }

    }


}