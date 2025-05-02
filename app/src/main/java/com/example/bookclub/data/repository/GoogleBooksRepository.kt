package com.example.bookclub.data.repository

import com.example.bookclub.data.api.GoogleBooksApi
import com.example.bookclub.data.model.Book
import com.example.bookclub.data.model.BookStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleBooksRepository @Inject constructor(
    private val googleBooksApi: GoogleBooksApi
) {
    fun searchBooks(query: String): Flow<List<Book>> = flow {
        try {
            val response = googleBooksApi.searchBooks(query)
            if (response.isSuccessful) {
                val books = response.body()?.items?.mapNotNull { volumeInfo ->
                    val bookInfo = volumeInfo.volumeInfo
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    val publishedDate = try {
                        bookInfo.publishedDate?.let { dateFormat.parse(it) }
                    } catch (e: Exception) {
                        null
                    }

                    Book(
                        title = bookInfo.title,
                        author = bookInfo.authors?.joinToString(", ") ?: "Unknown Author",
                        description = bookInfo.description ?: "No description available",
                        coverImageUrl = bookInfo.imageLinks?.thumbnail?.replace("http:", "https:"),
                        genre = bookInfo.categories?.firstOrNull() ?: "Uncategorized",
                        isbn = bookInfo.industryIdentifiers?.firstOrNull()?.identifier,
                        publishedDate = publishedDate,
                        rating = bookInfo.averageRating ?: 0f,
                        status = BookStatus.WANT_TO_READ,
                        totalPages = bookInfo.pageCount ?: 0
                    )
                } ?: emptyList()
                emit(books)
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)

    fun getBooksByCategory(category: String): Flow<List<Book>> = flow {
        try {
            val query = "subject:$category"
            val response = googleBooksApi.getBooksByCategory(query)
            if (response.isSuccessful) {
                val books = response.body()?.items?.mapNotNull { volumeInfo ->
                    val bookInfo = volumeInfo.volumeInfo
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    val publishedDate = try {
                        bookInfo.publishedDate?.let { dateFormat.parse(it) }
                    } catch (e: Exception) {
                        null
                    }

                    Book(
                        title = bookInfo.title,
                        author = bookInfo.authors?.joinToString(", ") ?: "Unknown Author",
                        description = bookInfo.description ?: "No description available",
                        coverImageUrl = bookInfo.imageLinks?.thumbnail?.replace("http:", "https:"),
                        genre = bookInfo.categories?.firstOrNull() ?: category,
                        isbn = bookInfo.industryIdentifiers?.firstOrNull()?.identifier,
                        publishedDate = publishedDate,
                        rating = bookInfo.averageRating ?: 0f,
                        status = BookStatus.WANT_TO_READ,
                        totalPages = bookInfo.pageCount ?: 0
                    )
                } ?: emptyList()
                emit(books)
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)
}