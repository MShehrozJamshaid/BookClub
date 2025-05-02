package com.example.bookclub.data.converter

import androidx.room.TypeConverter
import com.example.bookclub.data.model.BookStatus
import java.util.*

/**
 * Type converters for Room database.
 * This class provides methods to convert between complex types and types that Room can persist.
 */
class BookClubTypeConverters {
    // Convert Date to Long (timestamp) for storing in database
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    // Convert Long (timestamp) back to Date when reading from database
    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

    // Convert BookStatus enum to String for storing in database
    @TypeConverter
    fun fromBookStatus(status: BookStatus?): String? {
        return status?.name
    }

    // Convert String back to BookStatus when reading from database
    @TypeConverter
    fun toBookStatus(status: String?): BookStatus? {
        return status?.let { BookStatus.valueOf(it) }
    }
    
    // Convert List<String> to a single String for storing in database
    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        return list?.joinToString(",")
    }

    // Convert stored String back to List<String> when reading from database
    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.split(",")?.filter { it.isNotEmpty() }
    }

    // Add other converters as needed for other complex types
}