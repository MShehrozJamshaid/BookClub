package com.example.bookclub.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookclub.data.model.Book
import com.example.bookclub.data.repository.BookRepository
import com.example.bookclub.data.repository.GoogleBooksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnlineBookSearchViewModel @Inject constructor(
    private val googleBooksRepository: GoogleBooksRepository,
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnlineBookSearchState())
    val uiState: StateFlow<OnlineBookSearchState> = _uiState.asStateFlow()

    fun searchBooks(query: String) {
        if (query.isBlank()) return
        
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        
        viewModelScope.launch {
            googleBooksRepository.searchBooks(query)
                .catch { exception ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = "Error searching books: ${exception.localizedMessage}"
                        )
                    }
                }
                .collectLatest { books ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            searchResults = books
                        )
                    }
                }
        }
    }

    fun searchBooksByCategory(category: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        
        viewModelScope.launch {
            googleBooksRepository.getBooksByCategory(category)
                .catch { exception ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = "Error searching books: ${exception.localizedMessage}"
                        )
                    }
                }
                .collectLatest { books ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            searchResults = books
                        )
                    }
                }
        }
    }

    fun importBook(book: Book) {
        viewModelScope.launch {
            try {
                val bookId = bookRepository.insertBook(book)
                if (bookId > 0) {
                    _uiState.update { 
                        it.copy(
                            lastImportedBook = book.copy(id = bookId),
                            importSuccess = true
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            errorMessage = "Failed to import book",
                            importSuccess = false
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        errorMessage = "Error importing book: ${e.localizedMessage}",
                        importSuccess = false
                    ) 
                }
            }
        }
    }
    
    fun resetImportStatus() {
        _uiState.update { it.copy(importSuccess = false, lastImportedBook = null) }
    }
}

data class OnlineBookSearchState(
    val searchResults: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val importSuccess: Boolean = false,
    val lastImportedBook: Book? = null
)