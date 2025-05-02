package com.example.bookclub.data.dao

import androidx.room.*
import com.example.bookclub.data.model.Book
import com.example.bookclub.data.model.BookStatus
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface BookDao {
    @Query("SELECT * FROM books")
    fun getAllBooks(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getBookById(id: Long): Book?

    @Query("SELECT * FROM books WHERE status = :status")
    fun getBooksByStatus(status: BookStatus): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%'")
    fun searchBooks(query: String): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE genre LIKE '%' || :genre || '%'")
    fun getBooksByGenre(genre: String): Flow<List<Book>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book): Long

    @Update
    suspend fun updateBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)

    @Query("UPDATE books SET currentPage = :currentPage, lastReadDate = :date WHERE id = :bookId")
    suspend fun updateReadingProgress(bookId: Long, currentPage: Int, date: Date)

    @Query("UPDATE books SET status = :status WHERE id = :bookId")
    suspend fun updateBookStatus(bookId: Long, status: BookStatus)

    @Query("UPDATE books SET rating = :rating WHERE id = :bookId")
    suspend fun updateBookRating(bookId: Long, rating: Float)
}