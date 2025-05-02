package com.example.bookclub.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookclub.data.model.Book
import com.example.bookclub.data.model.BookClub
import com.example.bookclub.data.model.BookStatus
import com.example.bookclub.data.repository.BookClubRepository
import com.example.bookclub.data.repository.BookRepository
import com.example.bookclub.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

data class HomeUiState(
    val currentlyReading: List<Book> = emptyList(),
    val recommendedBooks: List<Book> = emptyList(),
    val popularClubs: List<BookClub> = emptyList(),
    val myClubs: List<BookClub> = emptyList(),
    val readingStreak: Int = 0,
    val lastReadDate: Date? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val bookClubRepository: BookClubRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // For simplicity, using a hardcoded user ID
    private val currentUserId = 1L
    
    // Counter to track when all data flows have been processed
    private var dataLoadingJobs = 0
    private var timeoutJob: Job? = null

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        // Reset data loading counter for the 3 main data sources
        dataLoadingJobs = 3
        
        // Cancel any existing timeout
        timeoutJob?.cancel()
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // Set a timeout to prevent infinite loading
            timeoutJob = viewModelScope.launch {
                delay(5000) // 5 seconds timeout
                if (_uiState.value.isLoading) {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
            
            try {
                // Try to load real data first
                viewModelScope.launch {
                    // Load currently reading books
                    bookRepository.getBooksByStatus(BookStatus.READING)
                        .catch { e ->
                            _uiState.update { it.copy(error = "Failed to load reading books") }
                            decrementAndCheckLoading()
                        }
                        .collectLatest { books ->
                            _uiState.update { it.copy(currentlyReading = books.take(3)) }
                            decrementAndCheckLoading()
                            
                            // If no data loaded at all, use sample data
                            if (books.isEmpty() && dataLoadingJobs <= 0 &&
                                _uiState.value.recommendedBooks.isEmpty() &&
                                _uiState.value.popularClubs.isEmpty()) {
                                loadSampleData()
                            }
                        }
                }
                
                viewModelScope.launch {
                    // Load recommended books - using want to read for demo
                    bookRepository.getBooksByStatus(BookStatus.WANT_TO_READ)
                        .catch { e ->
                            _uiState.update { it.copy(error = "Failed to load recommended books") }
                            decrementAndCheckLoading()
                        }
                        .collectLatest { books ->
                            _uiState.update { it.copy(recommendedBooks = books.take(5)) }
                            decrementAndCheckLoading()
                        }
                }
                
                viewModelScope.launch {
                    // Load popular clubs
                    bookClubRepository.getPublicClubs()
                        .catch { e ->
                            _uiState.update { it.copy(error = "Failed to load clubs") }
                            decrementAndCheckLoading()
                        }
                        .collectLatest { clubs ->
                            _uiState.update { it.copy(popularClubs = clubs) }
                            
                            // Also load user clubs
                            bookClubRepository.getUserClubs(currentUserId)
                                .catch { /* Ignore errors for user clubs */ }
                                .collectLatest { userClubs ->
                                    _uiState.update { it.copy(myClubs = userClubs) }
                                }
                            
                            decrementAndCheckLoading()
                        }
                }
                
                // Load reading streak from repository
                viewModelScope.launch {
                    try {
                        val readingStreak = userRepository.getReadingStreak(currentUserId)
                        val lastReadDate = userRepository.getLastReadDate(currentUserId)
                        
                        // Check if streak needs to be reset
                        val streak = if (isStreakActive(lastReadDate)) {
                            readingStreak
                        } else {
                            0 // Reset streak if more than 1 day has passed
                        }
                        
                        _uiState.update { 
                            it.copy(
                                readingStreak = streak,
                                lastReadDate = lastReadDate
                            ) 
                        }
                    } catch (e: Exception) {
                        _uiState.update {
                            it.copy(
                                readingStreak = 0,
                                error = "Failed to load reading streak: ${e.message}" 
                            )
                        }
                    }
                }
                
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Failed to load data: ${e.message}",
                        isLoading = false
                    )
                }
                
                // If real data loading fails, use sample data
                loadSampleData()
            }
        }
    }
    
    // Check if the streak is still active (less than 48 hours since last read)
    private fun isStreakActive(lastReadDate: Date?): Boolean {
        if (lastReadDate == null) return false
        
        val calendar = Calendar.getInstance()
        val currentDate = calendar.time
        
        // Calculate the difference in days
        val diffInMillis = currentDate.time - lastReadDate.time
        val diffInDays = diffInMillis / (1000 * 60 * 60 * 24)
        
        return diffInDays <= 1 // Streak is active if less than 48 hours passed
    }
    
    fun updateReadingStreak(increment: Boolean = true) {
        viewModelScope.launch {
            try {
                val currentStreak = _uiState.value.readingStreak
                val newStreak = if (increment) currentStreak + 1 else maxOf(0, currentStreak - 1)
                
                // Update streak in repository
                userRepository.updateReadingStreak(currentUserId, newStreak)
                
                // Update last read date if incrementing
                if (increment) {
                    userRepository.updateLastReadDate(currentUserId, Date())
                }
                
                // Update UI state
                _uiState.update { 
                    it.copy(
                        readingStreak = newStreak,
                        lastReadDate = if (increment) Date() else it.lastReadDate
                    )
                }
                
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to update reading streak: ${e.message}")
                }
            }
        }
    }
    
    fun resetReadingStreak() {
        viewModelScope.launch {
            try {
                // Reset streak in repository
                userRepository.updateReadingStreak(currentUserId, 0)
                
                // Update UI state
                _uiState.update { it.copy(readingStreak = 0) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to reset reading streak: ${e.message}")
                }
            }
        }
    }
    
    /**
     * Sets the reading streak to a specific value
     */
    fun setReadingStreak(newStreak: Int) {
        viewModelScope.launch {
            try {
                // Update streak in repository
                userRepository.updateReadingStreak(currentUserId, newStreak)
                
                // Update last read date to today
                userRepository.updateLastReadDate(currentUserId, Date())
                
                // Update UI state
                _uiState.update { 
                    it.copy(
                        readingStreak = newStreak,
                        lastReadDate = Date()
                    )
                }
                
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to set reading streak: ${e.message}")
                }
            }
        }
    }
    
    private fun decrementAndCheckLoading() {
        dataLoadingJobs--
        if (dataLoadingJobs <= 0) {
            // All data flows have been processed
            _uiState.update { it.copy(isLoading = false) }
            timeoutJob?.cancel() // Cancel the timeout since we're done
        }
    }
    
    private suspend fun loadSampleData() {
        // Create sample books for currently reading
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
        
        // Create sample recommended books
        val recommendedBooks = listOf(
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
        
        // Create sample popular book clubs with better images
        val popularClubs = listOf(
            BookClub(
                id = 1,
                name = "Classic Literature Lovers",
                description = "A club for fans of classic literature from the 19th and early 20th centuries.",
                coverImageUrl = "https://images.unsplash.com/photo-1524578271613-d550eacf6090?q=80&w=800&auto=format&fit=crop",
                memberCount = 45,
                isPublic = true,
                createdBy = currentUserId,
                currentBookId = currentlyReadingBooks[0].id
            ),
            BookClub(
                id = 2,
                name = "Science Fiction Explorers",
                description = "Exploring the vast universe of science fiction literature.",
                coverImageUrl = "https://images.unsplash.com/photo-1501862700950-18382cd41497?q=80&w=800&auto=format&fit=crop",
                memberCount = 78,
                isPublic = true,
                createdBy = currentUserId,
                currentBookId = recommendedBooks[0].id
            ),
            BookClub(
                id = 3,
                name = "Fantasy Realm",
                description = "For readers who enjoy dragons, magic, and epic quests.",
                coverImageUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?q=80&w=800&auto=format&fit=crop",
                memberCount = 62,
                isPublic = true,
                createdBy = currentUserId,
                currentBookId = recommendedBooks[2].id
            )
        )
        
        // User's clubs
        val myClubs = listOf(
            BookClub(
                id = 1,
                name = "Classic Literature Lovers",
                description = "A club for fans of classic literature from the 19th and early 20th centuries.",
                coverImageUrl = "https://images.unsplash.com/photo-1524578271613-d550eacf6090?q=80&w=800&auto=format&fit=crop",
                memberCount = 45,
                isPublic = true,
                createdBy = currentUserId,
                currentBookId = currentlyReadingBooks[0].id
            ),
            BookClub(
                id = 4,
                name = "Book Buddies",
                description = "A private club for friends to discuss their current reads.",
                coverImageUrl = "https://images.unsplash.com/photo-1530538987395-032d1800fdd4?q=80&w=800&auto=format&fit=crop",
                memberCount = 8,
                isPublic = false,
                createdBy = currentUserId,
                currentBookId = currentlyReadingBooks[1].id
            )
        )
        
        // Update UI state with all sample data at once
        _uiState.update { 
            it.copy(
                currentlyReading = currentlyReadingBooks,
                recommendedBooks = recommendedBooks,
                popularClubs = popularClubs,
                myClubs = myClubs,
                readingStreak = 12, // Sample reading streak
                lastReadDate = Date(), // Current date as last read date for sample data
                isLoading = false
            )
        }
    }

    fun joinClub(clubId: Long) {
        viewModelScope.launch {
            try {
                bookClubRepository.joinClub(currentUserId, clubId)
                // Find the club from popular clubs and add it to my clubs
                val clubToJoin = uiState.value.popularClubs.find { it.id == clubId }
                if (clubToJoin != null && !uiState.value.myClubs.any { it.id == clubId }) {
                    val updatedMyClubs = uiState.value.myClubs + clubToJoin
                    _uiState.update { it.copy(myClubs = updatedMyClubs) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun leaveClub(clubId: Long) {
        viewModelScope.launch {
            try {
                bookClubRepository.leaveClub(currentUserId, clubId)
                // Remove the club from my clubs
                val updatedMyClubs = uiState.value.myClubs.filterNot { it.id == clubId }
                _uiState.update { it.copy(myClubs = updatedMyClubs) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun refresh() {
        loadHomeData()
    }
}