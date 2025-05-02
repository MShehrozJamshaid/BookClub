package com.example.bookclub.ui.screens.bookdetail

import androidx.lifecycle.SavedStateHandle
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
import javax.inject.Inject

data class BookDetailUiState(
    val book: Book? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 0,
    val isUpdatingProgress: Boolean = false
)

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val bookId: Long = checkNotNull(savedStateHandle["bookId"])

    private val _uiState = MutableStateFlow(BookDetailUiState())
    val uiState: StateFlow<BookDetailUiState> = _uiState.asStateFlow()

    init {
        loadBook()
    }

    private fun loadBook() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                bookRepository.getBookById(bookId)?.let { book ->
                    _uiState.update { 
                        it.copy(
                            book = book,
                            currentPage = book.currentPage
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateReadingProgress(page: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingProgress = true) }
            try {
                bookRepository.updateReadingProgress(bookId, page)
                _uiState.update { it.copy(currentPage = page) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isUpdatingProgress = false) }
            }
        }
    }

    fun updateBookStatus(status: BookStatus) {
        viewModelScope.launch {
            try {
                bookRepository.updateBookStatus(bookId, status)
                _uiState.update { state ->
                    state.copy(book = state.book?.copy(status = status))
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun updateRating(rating: Float) {
        viewModelScope.launch {
            try {
                bookRepository.updateBookRating(bookId, rating)
                _uiState.update { state ->
                    state.copy(book = state.book?.copy(rating = rating))
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun refresh() {
        loadBook()
    }
} 