package com.example.reely.data.local.room.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.reely.domain.model.ProfilePosition
import java.time.Instant

@Entity(
    tableName = "profiles",
    indices = [
        Index (
            value = ["position"],
            unique = true
        ),
    ],
)
data class ProfileEntity(
    @PrimaryKey
    val id: Int,
    val position: ProfilePosition,
    val name: String,
    val avatarPath: String?,
    val createdAt: Instant,
    val updatedAt: Instant
)
