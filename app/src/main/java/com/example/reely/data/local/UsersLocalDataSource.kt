package com.example.reely.data.local


import com.example.reely.data.local.model.LocalUser
import kotlinx.coroutines.flow.Flow

interface UsersLocalDataSource {

    fun observeUsers(): Flow<List<LocalUser>>

    suspend fun insertUser(user : LocalUser) : Long

    suspend fun getUsers(): List<LocalUser>

    suspend fun getUser(userId: Int) : LocalUser?

    suspend fun getMainUserOrNull(): LocalUser?

    suspend fun addUser(
        nickname: String,
        avatarPath: String? = null
    ): LocalUser

    suspend fun deleteUser(userId: Int)

    suspend fun updateNickname(userId: Int, nickname: String) : Int

    suspend fun updateAvatar(userId : Int, path: String) : LocalUser
}