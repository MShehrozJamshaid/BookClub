package com.example.bookclub.ui.screens.bookshelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookclub.data.model.Book
import com.example.bookclub.data.model.BookStatus
import com.example.bookclub.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

data class BookshelfUiState(
    val currentlyReading: List<Book> = emptyList(),
    val wantToRead: List<Book> = emptyList(),
    val completed: List<Book> = emptyList(),
    val selectedTab: BookStatus = BookStatus.READING,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class BookshelfViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookshelfUiState(isLoading = true))
    val uiState: StateFlow<BookshelfUiState> = _uiState.asStateFlow()
    
    // Counter to track when all data flows have been processed
    private var dataLoadingJobs = 0
    private var timeoutJob: Job? = null

    init {
        loadBooks()
    }

    private fun loadBooks() {
        // Reset data loading counter
        dataLoadingJobs = 3
        
        // Cancel any existing timeout
        timeoutJob?.cancel()
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // Set a timeout to prevent infinite loading
            timeoutJob = viewModelScope.launch {
                delay(5000) // 5 seconds timeout
                // If still loading after timeout, finish loading with what we have
                if (_uiState.value.isLoading) {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
            
            try {
                // Load reading books
                viewModelScope.launch {
                    bookRepository.getBooksByStatus(BookStatus.READING)
                        .catch { e ->
                            _uiState.update { 
                                it.copy(error = "Failed to load reading books: ${e.message}")
                            }
                            decrementAndCheckLoading()
                        }
                        .collectLatest { books ->
                            _uiState.update { it.copy(currentlyReading = books) }
                            decrementAndCheckLoading()
                            
                            // If no books found at all, load sample data
                            if (books.isEmpty() && 
                                _uiState.value.wantToRead.isEmpty() && 
                                _uiState.value.completed.isEmpty() &&
                                dataLoadingJobs <= 0) {
                                loadSampleBooks()
                            }
                        }
                }
                
                // Load want to read books
                viewModelScope.launch {
                    bookRepository.getBooksByStatus(BookStatus.WANT_TO_READ)
                        .catch { e ->
                            _uiState.update { 
                                it.copy(error = "Failed to load want-to-read books: ${e.message}")
                            }
                            decrementAndCheckLoading()
                        }
                        .collectLatest { books ->
                            _uiState.update { it.copy(wantToRead = books) }
                            decrementAndCheckLoading()
                        }
                }
                
                // Load completed books
                viewModelScope.launch {
                    bookRepository.getBooksByStatus(BookStatus.COMPLETED)
                        .catch { e ->
                            _uiState.update { 
                                it.copy(error = "Failed to load completed books: ${e.message}")
                            }
                            decrementAndCheckLoading()
                        }
                        .collectLatest { books ->
                            _uiState.update { it.copy(completed = books) }
                            decrementAndCheckLoading()
                        }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Failed to load books: ${e.message}",
                        isLoading = false
                    ) 
                }
                
                // If an exception occurs, load sample data for demo purposes
                loadSampleBooks()
            }
        }
    }
    
    private fun decrementAndCheckLoading() {
        dataLoadingJobs--
        if (dataLoadingJobs <= 0) {
            // All data flows have been processed, stop loading state
            _uiState.update { it.copy(isLoading = false) }
            timeoutJob?.cancel() // Cancel the timeout since we're done
        }
    }
    
    private fun loadSampleBooks() {
        // Sample currently reading books
        val currentlyReadingBooks = listOf(
            Book(
                id = 1,
                title = "The Great Gatsby",
                author = "F. Scott Fitzgerald",
                coverImageUrl = "https://covers.openlibrary.org/b/id/8410894-L.jpg",
                description = "A novel about the mysterious millionaire Jay Gatsby and his obsession with Daisy Buchanan.",
                publishedDate = Date(),
                status = BookStatus.READING,
                currentPage = 120,
                totalPages = 200,
                rating = 4.5f,
                genre = "Classic Fiction",
                isbn = "9780743273565"
            ),
            Book(
                id = 2,
                title = "To Kill a Mockingbird",
                author = "Harper Lee",
                coverImageUrl = "https://covers.openlibrary.org/b/id/8651697-L.jpg",
                description = "A novel about racial inequality and moral growth in the American South during the 1930s.",
                publishedDate = Date(),
                status = BookStatus.READING,
                currentPage = 80,
                totalPages = 320,
                rating = 4.8f,
                genre = "Classic Fiction",
                isbn = "9780061120084"
            )
        )
        
        // Sample want to read books
        val wantToReadBooks = listOf(
            Book(
                id = 3,
                title = "1984",
                author = "George Orwell",
                coverImageUrl = "https://covers.openlibrary.org/b/id/8575708-L.jpg",
                description = "A dystopian social science fiction novel about totalitarianism.",
                publishedDate = Date(),
                status = BookStatus.WANT_TO_READ,
                totalPages = 328,
                rating = 4.7f,
                genre = "Dystopian",
                isbn = "9780451524935"
            ),
            Book(
                id = 4,
                title = "Pride and Prejudice",
                author = "Jane Austen",
                coverImageUrl = "https://covers.openlibrary.org/b/id/8751004-L.jpg",
                description = "A romantic novel of manners that follows the character development of Elizabeth Bennet.",
                publishedDate = Date(),
                status = BookStatus.WANT_TO_READ,
                totalPages = 432,
                rating = 4.6f,
                genre = "Classic Romance",
                isbn = "9780141439518"
            ),
            Book(
                id = 5,
                title = "The Hobbit",
                author = "J.R.R. Tolkien",
                coverImageUrl = "https://covers.openlibrary.org/b/id/8405716-L.jpg",
                description = "A fantasy novel about the quest of home-loving Bilbo Baggins to win a share of the treasure guarded by a dragon.",
                publishedDate = Date(),
                status = BookStatus.WANT_TO_READ,
                totalPages = 310,
                rating = 4.9f,
                genre = "Fantasy",
                isbn = "9780547928227"
            )
        )
        
        // Sample completed books
        val completedBooks = listOf(
            Book(
                id = 6,
                title = "The Catcher in the Rye",
                author = "J.D. Salinger",
                coverImageUrl = "https://covers.openlibrary.org/b/id/8231449-L.jpg",
                description = "A novel about teenage angst and alienation.",
                publishedDate = Date(),
                status = BookStatus.COMPLETED,
                currentPage = 224,
                totalPages = 224,
                rating = 4.3f,
                genre = "Coming of Age",
                isbn = "9780316769488"
            ),
            Book(
                id = 7,
                title = "Brave New World",
                author = "Aldous Huxley",
                coverImageUrl = "https://covers.openlibrary.org/b/id/8231446-L.jpg",
                description = "A dystopian novel about a futuristic World State.",
                publishedDate = Date(),
                status = BookStatus.COMPLETED,
                currentPage = 311,
                totalPages = 311,
                rating = 4.4f,
                genre = "Dystopian",
                isbn = "9780060850524"
            )
        )
        
        // Update UI state with all sample data at once
        _uiState.update { 
            it.copy(
                currentlyReading = currentlyReadingBooks,
                wantToRead = wantToReadBooks,
                completed = completedBooks,
                isLoading = false
            ) 
        }
    }

    fun updateSelectedTab(status: BookStatus) {
        _uiState.update { it.copy(selectedTab = status) }
    }

    fun refresh() {
        loadBooks()
    }
}