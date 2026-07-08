package com.example.reely.data.local.room

import com.example.reely.data.local.UsersLocalDataSource
import com.example.reely.data.local.model.LocalUser
import com.example.reely.data.local.room.dao.UserDao
import com.example.reely.data.local.room.entity.UserEntity
import com.example.reely.data.local.room.mapper.toLocalUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomUsersLocalDataSource @Inject constructor(
    private val userDao: UserDao
) : UsersLocalDataSource {

    override fun observeUsers(): Flow<List<LocalUser>> {
        return userDao.observeUsers()
            .map { users ->
                users.map { user ->
                    user.toLocalUser()
                }
            }
    }

    override suspend fun getUsers(): List<LocalUser> {
        return userDao.getUsers()
            .map { user ->
                user.toLocalUser()
            }
    }

    override suspend fun getMainUser(): LocalUser? {
        return userDao.getMainUser()
            ?.toLocalUser()
    }

    override suspend fun addUser(
        nickname: String,
        avatarPath: String?
    ): LocalUser {
        val id = userDao.insertUser(
            UserEntity(
                nickname = nickname,
                avatarPath = avatarPath
            )
        ).toInt()

        return LocalUser(
            id = id,
            nickname = nickname,
            avatarPath = avatarPath
        )
    }

    override suspend fun deleteUser(userId: Int) {
        userDao.deleteUser(userId)
    }
}