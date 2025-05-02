package com.example.bookclub.data.repository

import com.example.bookclub.data.dao.BookDao
import com.example.bookclub.data.model.Book
import com.example.bookclub.data.model.BookStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepository @Inject constructor(
    private val bookDao: BookDao
) {
    fun getAllBooks(): Flow<List<Book>> = bookDao.getAllBooks()

    suspend fun getBookById(id: Long): Book? = bookDao.getBookById(id)

    fun getBooksByStatus(status: BookStatus): Flow<List<Book>> = bookDao.getBooksByStatus(status)

    fun getBooksByRating(minRating: Float): Flow<List<Book>> {
        return getAllBooks().filter { books ->
            books.any { it.rating >= minRating }
        }
    }

    fun searchBooks(query: String): Flow<List<Book>> = bookDao.searchBooks(query)

    suspend fun insertBook(book: Book): Long = bookDao.insertBook(book)

    suspend fun updateBook(book: Book) = bookDao.updateBook(book)

    suspend fun deleteBook(book: Book) = bookDao.deleteBook(book)

    fun getBooksByGenre(genre: String): Flow<List<Book>> = bookDao.getBooksByGenre(genre)

    suspend fun updateReadingProgress(bookId: Long, currentPage: Int) {
        bookDao.updateReadingProgress(bookId, currentPage, Date())
    }

    suspend fun updateBookStatus(bookId: Long, status: BookStatus) {
        bookDao.updateBookStatus(bookId, status)
    }

    suspend fun updateBookRating(bookId: Long, rating: Float) {
        bookDao.updateBookRating(bookId, rating)
    }
}