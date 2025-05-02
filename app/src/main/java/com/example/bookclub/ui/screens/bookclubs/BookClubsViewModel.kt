package com.example.bookclub.ui.screens.bookclubs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookclub.data.model.Book
import com.example.bookclub.data.model.BookClub
import com.example.bookclub.data.model.BookStatus
import com.example.bookclub.data.repository.BookClubRepository
import com.example.bookclub.data.repository.UserRepository
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

data class BookClubsUiState(
    val publicClubs: List<BookClub> = emptyList(),
    val myClubs: List<BookClub> = emptyList(),
    val selectedTab: ClubTab = ClubTab.PUBLIC,
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class ClubTab {
    PUBLIC, MY_CLUBS
}

@HiltViewModel
class BookClubsViewModel @Inject constructor(
    private val bookClubRepository: BookClubRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookClubsUiState(isLoading = true))
    val uiState: StateFlow<BookClubsUiState> = _uiState.asStateFlow()
    
    // Current user ID (using mock for now)
    private val currentUserId = 1L

    init {
        loadClubs()
    }

    private fun loadClubs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // Load clubs from repository using Flow
                // Public clubs
                bookClubRepository.getPublicClubs()
                    .catch { e ->
                        _uiState.update { 
                            it.copy(
                                error = "Failed to load public clubs: ${e.message}",
                                isLoading = false
                            ) 
                        }
                    }
                    .collectLatest { clubs ->
                        _uiState.update { it.copy(publicClubs = clubs) }
                        
                        // After public clubs are loaded, check if we have sufficient data
                        if (_uiState.value.myClubs.isNotEmpty()) {
                            _uiState.update { it.copy(isLoading = false) }
                        }
                        
                        // If no clubs are found, load sample data for demo purposes
                        if (clubs.isEmpty()) {
                            loadSampleClubs()
                        }
                    }
                
                // User's clubs
                bookClubRepository.getUserClubs(currentUserId)
                    .catch { e ->
                        _uiState.update { 
                            it.copy(
                                error = "Failed to load your clubs: ${e.message}",
                                isLoading = false
                            ) 
                        }
                    }
                    .collectLatest { clubs ->
                        _uiState.update { 
                            it.copy(
                                myClubs = clubs,
                                isLoading = false
                            ) 
                        }
                    }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Failed to load clubs: ${e.message}",
                        isLoading = false
                    ) 
                }
                
                // If an exception occurs, load sample data for demo purposes
                loadSampleClubs()
            }
        }
    }
    
    private fun loadSampleClubs() {
        // Sample books for clubs
        val sampleBook1 = Book(
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
        )
        
        val sampleBook2 = Book(
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
        )
        
        val sampleBook3 = Book(
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
        
        // Sample public clubs
        val publicClubs = listOf(
            BookClub(
                id = 1,
                name = "Classic Literature Lovers",
                description = "A club for fans of classic literature from the 19th and early 20th centuries.",
                coverImageUrl = "https://via.placeholder.com/800x200/3498db/ffffff?text=Classic+Literature",
                memberCount = 45,
                isPublic = true,
                createdBy = currentUserId,
                currentBookId = sampleBook1.id
            ),
            BookClub(
                id = 2,
                name = "Science Fiction Explorers",
                description = "Exploring the vast universe of science fiction literature.",
                coverImageUrl = "https://via.placeholder.com/800x200/9b59b6/ffffff?text=Sci-Fi+Explorers",
                memberCount = 78,
                isPublic = true,
                createdBy = currentUserId,
                currentBookId = sampleBook2.id
            ),
            BookClub(
                id = 3,
                name = "Fantasy Realm",
                description = "For readers who enjoy dragons, magic, and epic quests.",
                coverImageUrl = "https://via.placeholder.com/800x200/27ae60/ffffff?text=Fantasy+Realm",
                memberCount = 62,
                isPublic = true,
                createdBy = currentUserId,
                currentBookId = sampleBook3.id
            ),
            BookClub(
                id = 5,
                name = "Mystery & Thrillers",
                description = "Delving into the suspenseful world of mystery and thriller novels.",
                coverImageUrl = "https://via.placeholder.com/800x200/e74c3c/ffffff?text=Mystery+Club",
                memberCount = 53,
                isPublic = true,
                createdBy = currentUserId
            ),
            BookClub(
                id = 6,
                name = "Non-Fiction Enthusiasts",
                description = "Discussing biographies, history, science, and other non-fiction topics.",
                coverImageUrl = "https://via.placeholder.com/800x200/f39c12/000000?text=Non-Fiction",
                memberCount = 41,
                isPublic = true,
                createdBy = currentUserId
            )
        )
        
        // Sample user's clubs
        val myClubs = listOf(
            BookClub(
                id = 1,
                name = "Classic Literature Lovers",
                description = "A club for fans of classic literature from the 19th and early 20th centuries.",
                coverImageUrl = "https://via.placeholder.com/800x200/3498db/ffffff?text=Classic+Literature",
                memberCount = 45,
                isPublic = true,
                createdBy = currentUserId,
                currentBookId = sampleBook1.id
            ),
            BookClub(
                id = 4,
                name = "Book Buddies",
                description = "A private club for friends to discuss their current reads.",
                coverImageUrl = "https://via.placeholder.com/800x200/2c3e50/ffffff?text=Book+Buddies",
                memberCount = 8,
                isPublic = false,
                createdBy = currentUserId
            )
        )
        
        // Update UI state with all sample data at once
        _uiState.update { 
            it.copy(
                publicClubs = publicClubs,
                myClubs = myClubs,
                isLoading = false
            ) 
        }
    }

    fun updateSelectedTab(tab: ClubTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun joinClub(clubId: Long) {
        viewModelScope.launch {
            try {
                // First, update the local UI state for immediate feedback
                val clubToJoin = _uiState.value.publicClubs.find { it.id == clubId }
                if (clubToJoin != null && !_uiState.value.myClubs.any { it.id == clubId }) {
                    val updatedMyClubs = _uiState.value.myClubs + clubToJoin
                    _uiState.update { it.copy(myClubs = updatedMyClubs) }
                }
                
                // Then, update the data in the repository
                bookClubRepository.joinClub(currentUserId, clubId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun leaveClub(clubId: Long) {
        viewModelScope.launch {
            try {
                // First, update the local UI state for immediate feedback
                val updatedMyClubs = _uiState.value.myClubs.filterNot { it.id == clubId }
                _uiState.update { it.copy(myClubs = updatedMyClubs) }
                
                // Then, update the data in the repository
                bookClubRepository.leaveClub(currentUserId, clubId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun refresh() {
        loadClubs()
    }
}