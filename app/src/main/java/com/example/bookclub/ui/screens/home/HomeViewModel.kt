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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class HomeUiState(
    val currentlyReading: List<Book> = emptyList(),
    val recommendedBooks: List<Book> = emptyList(),
    val popularClubs: List<BookClub> = emptyList(),
    val myClubs: List<BookClub> = emptyList(),
    val readingStreak: Int = 0,
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

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // Load sample data since we can't rely on the Flow collections to complete
                loadSampleData()
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Failed to load data: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    private suspend fun loadSampleData() {
        // Create sample books for currently reading
        val currentlyReadingBooks = listOf(
            Book(
                id = 1,
                title = "The Great Gatsby",
                author = "F. Scott Fitzgerald",
                coverImageUrl = "https://via.placeholder.com/150/3498db/ffffff?text=Gatsby",
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
                coverImageUrl = "https://via.placeholder.com/150/e74c3c/ffffff?text=Mockingbird",
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
                coverImageUrl = "https://via.placeholder.com/150/9b59b6/ffffff?text=1984",
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
                coverImageUrl = "https://via.placeholder.com/150/f1c40f/000000?text=Pride",
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
                coverImageUrl = "https://via.placeholder.com/150/27ae60/ffffff?text=Hobbit",
                description = "A fantasy novel about the quest of home-loving Bilbo Baggins to win a share of the treasure guarded by a dragon.",
                publishedDate = Date(),
                status = BookStatus.WANT_TO_READ,
                totalPages = 310,
                rating = 4.9f,
                genre = "Fantasy",
                isbn = "9780547928227"
            )
        )
        
        // Create sample popular book clubs
        val popularClubs = listOf(
            BookClub(
                id = 1,
                name = "Classic Literature Lovers",
                description = "A club for fans of classic literature from the 19th and early 20th centuries.",
                coverImageUrl = "https://via.placeholder.com/800x200/3498db/ffffff?text=Classic+Literature",
                memberCount = 45,
                isPublic = true,
                createdBy = currentUserId,
                currentBookId = currentlyReadingBooks[0].id
            ),
            BookClub(
                id = 2,
                name = "Science Fiction Explorers",
                description = "Exploring the vast universe of science fiction literature.",
                coverImageUrl = "https://via.placeholder.com/800x200/9b59b6/ffffff?text=Sci-Fi+Explorers",
                memberCount = 78,
                isPublic = true,
                createdBy = currentUserId,
                currentBookId = recommendedBooks[0].id
            ),
            BookClub(
                id = 3,
                name = "Fantasy Realm",
                description = "For readers who enjoy dragons, magic, and epic quests.",
                coverImageUrl = "https://via.placeholder.com/800x200/27ae60/ffffff?text=Fantasy+Realm",
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
                coverImageUrl = "https://via.placeholder.com/800x200/3498db/ffffff?text=Classic+Literature",
                memberCount = 45,
                isPublic = true,
                createdBy = currentUserId,
                currentBookId = currentlyReadingBooks[0].id
            ),
            BookClub(
                id = 4,
                name = "Book Buddies",
                description = "A private club for friends to discuss their current reads.",
                coverImageUrl = "https://via.placeholder.com/800x200/e74c3c/ffffff?text=Book+Buddies",
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