package com.example.bookclub.ui.screens.selectbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookclub.data.model.Book
import com.example.bookclub.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SelectBookUiState(
    val books: List<Book> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SelectBookViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SelectBookUiState())
    val uiState: StateFlow<SelectBookUiState> = _uiState.asStateFlow()

    init {
        loadBooks()
    }

    private fun loadBooks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                bookRepository.getAllBooks()
                    .collect { books ->
                        _uiState.update { it.copy(books = books) }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun refresh() {
        loadBooks()
    }

    fun getFilteredBooks(): List<Book> {
        return if (_uiState.value.searchQuery.isBlank()) {
            _uiState.value.books
        } else {
            _uiState.value.books.filter { book ->
                book.title.contains(_uiState.value.searchQuery, ignoreCase = true) ||
                book.author.contains(_uiState.value.searchQuery, ignoreCase = true)
            }
        }
    }
} 