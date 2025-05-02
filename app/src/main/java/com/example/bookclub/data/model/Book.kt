package com.example.bookclub.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val author: String,
    val description: String,
    val coverImageUrl: String?,
    val genre: String,
    val isbn: String?,
    val publishedDate: Date?,
    val rating: Float = 0f,
    val status: BookStatus = BookStatus.WANT_TO_READ,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val addedDate: Date = Date(),
    val lastReadDate: Date? = null
)