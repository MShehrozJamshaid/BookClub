package com.example.bookclub.data.repository

import com.example.bookclub.data.dao.UserDao
import com.example.bookclub.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    fun getUserById(userId: Long): Flow<User?> = userDao.getUserById(userId)
    
    // Added a non-flow version for use in viewModels
    suspend fun getUserByIdSync(userId: Long): User? = userDao.getUserById(userId).firstOrNull()

    suspend fun insertUser(user: User): Long = userDao.insertUser(user)

    suspend fun updateUser(user: User) = userDao.updateUser(user)

    suspend fun updateReadingStreak(userId: Long, streak: Int) {
        userDao.updateReadingStreak(userId, streak, Date().time)
    }

    suspend fun updateReadingStats(userId: Long, pages: Int) {
        userDao.updateReadingStats(userId, pages)
    }

    suspend fun updateAverageRating(userId: Long, rating: Float) {
        userDao.updateAverageRating(userId, rating)
    }
    
    suspend fun getReadingStreak(userId: Long): Int {
        return getUserByIdSync(userId)?.readingStreak ?: 0
    }

    suspend fun updateFavoriteGenres(userId: Long, genres: List<String>) {
        getUserById(userId).collect { user ->
            user?.let {
                updateUser(it.copy(favoriteGenres = genres))
            }
        }
    }
}