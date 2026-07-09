package com.example.reely.data.local.room.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.reely.data.local.room.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.annotation.meta.TypeQualifierNickname

@Dao
interface UserDao {

    @Query("SELECT * FROM users ORDER BY id ASC")
    fun observeUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users ORDER BY id ASC")
    suspend fun getUsers(): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Int): UserEntity?

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: Int)

    @Query("""
        UPDATE users 
        SET avatarPath = :avatarPath 
        WHERE id = :userId
    """)
    suspend fun updateAvatarPath(
        userId: Int,
        avatarPath: String?
    ): Int

    @Query("""
        UPDATE users
        SET nickname = :nickname
        WHERE id = :userId
    """)
    suspend fun updateUserNickname(
        userId: Int,
        nickname: String
    ): Int

    @Transaction
    suspend fun updateAvatarPathAndGetUser(
        userId: Int,
        avatarPath: String?
    ): UserEntity {
        val updatedRows = updateAvatarPath(
            userId = userId,
            avatarPath = avatarPath
        )

        if (updatedRows == 0) {
            error("User with id=$userId not found")
        }

        return getUserById(userId)
            ?: error("User with id=$userId not found after avatar update")
    }
}