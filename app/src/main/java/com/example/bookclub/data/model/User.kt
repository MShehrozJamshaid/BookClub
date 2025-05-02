package com.example.bookclub.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.bookclub.data.converter.BookClubTypeConverters
import java.util.Date

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String = "",
    val email: String = "",
    val profileImageUrl: String? = null,
    val bio: String = "",
    val readingStreak: Int = 0,
    val lastReadingDate: Date? = null,
    val totalBooksRead: Int = 0,
    val totalPagesRead: Int = 0,
    val averageRating: Float = 0f,
    @TypeConverters(BookClubTypeConverters::class)
    val favoriteGenres: List<String> = emptyList()
)