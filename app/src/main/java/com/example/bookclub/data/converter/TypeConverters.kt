package com.example.bookclub.data.converter

import androidx.room.TypeConverter
import com.example.bookclub.data.model.BookStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

// Renamed to avoid conflict with BookClubTypeConverters
class ExtendedTypeConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromString(value: String?): List<String> {
        if (value == null) {
            return emptyList()
        }
        val listType = object : TypeToken<List<String>>() {}.type
        return try {
            gson.fromJson<List<String>>(value, listType) // Explicitly specify the type parameter
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromList(list: List<String>?): String {
        return gson.toJson(list ?: emptyList<String>()) // Explicitly specify the type parameter
    }

    @TypeConverter
    fun fromBookStatus(value: BookStatus): String {
        return value.name
    }

    @TypeConverter
    fun toBookStatus(value: String): BookStatus {
        return try {
            BookStatus.valueOf(value)
        } catch (e: IllegalArgumentException) {
            BookStatus.WANT_TO_READ
        }
    }
}