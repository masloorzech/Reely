package com.example.reely.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.reely.data.local.room.entity.ProfileEntity
import com.example.reely.domain.model.ProfilePosition
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
interface ProfileDao {

    @Query(
        """
        SELECT *
        FROM profiles
        ORDER BY CASE position
            WHEN 'FIRST' THEN 0
            WHEN 'SECOND' THEN 1
            ELSE 2
        END
        """
    )
    fun observeAll(): Flow<List<ProfileEntity>>

    @Query(
        """
        SELECT *
        FROM profiles
        ORDER BY CASE position
            WHEN 'FIRST' THEN 0
            WHEN 'SECOND' THEN 1
            ELSE 2
        END
        """
    )
    suspend fun getAll(): List<ProfileEntity>

    @Query(
        """
        SELECT *
        FROM profiles
        WHERE id = :profileId
        LIMIT 1
        """
    )
    suspend fun getById(profileId: Int): ProfileEntity?

    @Query("SELECT COUNT(*) FROM profiles")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(profiles: List<ProfileEntity>)

    @Query(
        """
        UPDATE profiles
        SET name = :name,
            avatarPath = :avatarPath,
            updatedAt = :updatedAt
        WHERE id = :profileId
        """
    )
    suspend fun update(
        profileId: Int,
        name: String,
        avatarPath: String?,
        updatedAt: Instant,
    ): Int

    @Query(
        """
    SELECT *
    FROM profiles
    WHERE position = :position
    LIMIT 1
    """
    )
    suspend fun getByPosition(
        position: ProfilePosition,
    ): ProfileEntity?

}