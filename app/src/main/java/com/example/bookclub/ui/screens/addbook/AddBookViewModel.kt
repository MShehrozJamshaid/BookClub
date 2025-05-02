package com.example.bookclub.ui.screens.addbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookclub.data.model.Book
import com.example.bookclub.data.model.BookStatus
import com.example.bookclub.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class AddBookUiState(
    val title: String = "",
    val author: String = "",
    val description: String = "",
    val coverImageUrl: String? = null,
    val genre: String = "",
    val isbn: String = "",
    val totalPages: String = "",
    val publishedDate: Date? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isBookAdded: Boolean = false
)

@HiltViewModel
class AddBookViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddBookUiState())
    val uiState: StateFlow<AddBookUiState> = _uiState.asStateFlow()

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun updateAuthor(author: String) {
        _uiState.update { it.copy(author = author) }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun updateCoverImageUrl(url: String) {
        _uiState.update { it.copy(coverImageUrl = url) }
    }

    fun updateGenre(genre: String) {
        _uiState.update { it.copy(genre = genre) }
    }

    fun updateIsbn(isbn: String) {
        _uiState.update { it.copy(isbn = isbn) }
    }

    fun updateTotalPages(pages: String) {
        _uiState.update { it.copy(totalPages = pages) }
    }

    fun updatePublishedDate(date: Date) {
        _uiState.update { it.copy(publishedDate = date) }
    }

    fun addBook() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val book = Book(
                    title = uiState.value.title,
                    author = uiState.value.author,
                    description = uiState.value.description,
                    coverImageUrl = uiState.value.coverImageUrl,
                    genre = uiState.value.genre,
                    isbn = uiState.value.isbn.takeIf { it.isNotBlank() },
                    totalPages = uiState.value.totalPages.toIntOrNull() ?: 0,
                    publishedDate = uiState.value.publishedDate,
                    status = BookStatus.WANT_TO_READ
                )
                bookRepository.insertBook(book)
                _uiState.update { it.copy(isBookAdded = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun resetState() {
        _uiState.update { AddBookUiState() }
    }
} 