 package com.example.bookclub.data.dao

import androidx.room.*
import com.example.bookclub.data.model.BookClub
import kotlinx.coroutines.flow.Flow

@Dao
interface BookClubDao {
    @Query("SELECT * FROM book_clubs ORDER BY createdAt DESC")
    fun getAllClubs(): Flow<List<BookClub>>

    @Query("SELECT * FROM book_clubs WHERE isPublic = 1 ORDER BY createdAt DESC")
    fun getPublicClubs(): Flow<List<BookClub>>

    @Query("SELECT * FROM book_clubs WHERE id = :clubId")
    fun getClubById(clubId: Long): Flow<BookClub?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClub(club: BookClub): Long

    @Update
    suspend fun updateClub(club: BookClub)

    @Delete
    suspend fun deleteClub(club: BookClub)

    @Query("SELECT * FROM book_clubs WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchClubs(query: String): Flow<List<BookClub>>

    @Query("UPDATE book_clubs SET memberCount = memberCount + 1 WHERE id = :clubId")
    suspend fun incrementMemberCount(clubId: Long)

    @Query("UPDATE book_clubs SET memberCount = memberCount - 1 WHERE id = :clubId AND memberCount > 0")
    suspend fun decrementMemberCount(clubId: Long)

    @Query("UPDATE book_clubs SET discussionCount = discussionCount + 1 WHERE id = :clubId")
    suspend fun incrementDiscussionCount(clubId: Long)
}