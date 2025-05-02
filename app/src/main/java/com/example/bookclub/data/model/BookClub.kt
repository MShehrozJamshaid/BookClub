package com.example.bookclub.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "book_clubs")
data class BookClub(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val coverImageUrl: String? = null,
    val isPublic: Boolean = true,
    val createdBy: Long, // User ID
    val createdAt: Date = Date(),
    val currentBookId: Long? = null,
    val nextMeetingDate: Date? = null,
    val memberCount: Int = 1,
    val discussionCount: Int = 0
) 