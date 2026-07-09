package com.example.reely.domain.repository

import com.example.reely.domain.model.User
import com.example.reely.domain.model.UserId
import kotlinx.coroutines.flow.Flow

interface UsersRepository {

    fun observeUsers(): Flow<List<User>>

    suspend fun getUsers(): Result<List<User>>

    suspend fun getMainUser(): Result<User>

    suspend fun ensureMainUser(): Result<User>

    suspend fun addUser(
        nickname: String,
        avatarPath: String? = null
    ): Result<User>

    suspend fun deleteUser(userId: UserId): Result<Unit>

    suspend fun updateUserAvatar(userId: UserId, sourceUri : String): Result<User>

    suspend fun updateUserUsername(userId: UserId, username: String): Result<Unit>
}