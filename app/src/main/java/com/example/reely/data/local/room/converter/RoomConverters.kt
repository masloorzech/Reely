package com.example.reely.data.local.room.converter

import androidx.room.TypeConverter
import com.example.reely.domain.model.ProfilePosition
import java.time.Instant
import java.time.LocalDate

class RoomConverters {

    @TypeConverter
    fun instantToLong(value: Instant?): Long? =
        value?.toEpochMilli()

    @TypeConverter
    fun longToInstant(value: Long?): Instant? =
        value?.let(Instant::ofEpochMilli)

    @TypeConverter
    fun localDateToLong(value: LocalDate?): Long? =
        value?.toEpochDay()

    @TypeConverter
    fun longToLocalDate(value: Long?): LocalDate? =
        value?.let(LocalDate::ofEpochDay)

    @TypeConverter
    fun profilePositionToString(
        value: ProfilePosition?,
    ): String? = value?.name

    @TypeConverter
    fun stringToProfilePosition(
        value: String?,
    ): ProfilePosition? = value?.let(ProfilePosition::valueOf)
}