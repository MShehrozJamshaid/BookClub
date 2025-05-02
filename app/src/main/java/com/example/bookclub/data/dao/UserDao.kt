package com.example.bookclub.data.dao

import androidx.room.*
import com.example.bookclub.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: Long): Flow<User?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE users SET readingStreak = :streak, lastReadingDate = :date WHERE id = :userId")
    suspend fun updateReadingStreak(userId: Long, streak: Int, date: Long)

    @Query("UPDATE users SET totalBooksRead = totalBooksRead + 1, totalPagesRead = totalPagesRead + :pages WHERE id = :userId")
    suspend fun updateReadingStats(userId: Long, pages: Int)

    @Query("UPDATE users SET averageRating = :rating WHERE id = :userId")
    suspend fun updateAverageRating(userId: Long, rating: Float)
} 