package com.example.bookclub.ui.screens.clubdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookclub.data.model.Book
import com.example.bookclub.data.model.BookClub
import com.example.bookclub.data.model.BookStatus
import com.example.bookclub.data.model.User
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
import java.util.Date
import javax.inject.Inject

data class ClubDetailUiState(
    val club: BookClub? = null,
    val currentBook: Book? = null,
    val previousBooks: List<Book> = emptyList(),
    val members: List<User> = emptyList(),
    val creatorInfo: User? = null,
    val isMember: Boolean = false,
    val isLoading: Boolean = false,
    val upcomingMeetingDate: Date? = null,
    val discussions: Int = 0,
    val error: String? = null
)

@HiltViewModel
class ClubDetailViewModel @Inject constructor(
    private val bookClubRepository: BookClubRepository,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClubDetailUiState())
    val uiState: StateFlow<ClubDetailUiState> = _uiState.asStateFlow()
    
    // Current user ID (using mock for now)
    private val currentUserId = 1L

    fun loadClubDetails(clubId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Load club details with Flow
                bookClubRepository.getClubById(clubId)
                    .catch { e ->
                        _uiState.update { 
                            it.copy(
                                error = "Failed to load club details: ${e.message}",
                                isLoading = false
                            )
                        }
                    }
                    .collectLatest { club ->
                        if (club != null) {
                            _uiState.update { it.copy(
                                club = club,
                                upcomingMeetingDate = club.nextMeetingDate,
                                discussions = club.discussionCount
                            ) }
                            
                            // Load creator information
                            try {
                                val creator = userRepository.getUserByIdSync(club.createdBy)
                                _uiState.update { it.copy(creatorInfo = creator) }
                            } catch (e: Exception) {
                                // If creator info fails to load, we can still show other details
                            }
                            
                            // Load current book if available
                            if (club.currentBookId != null) {
                                try {
                                    val book = bookRepository.getBookById(club.currentBookId)
                                    _uiState.update { it.copy(currentBook = book) }
                                } catch (e: Exception) {
                                    // If current book fails to load, we can still show other details
                                }
                            }
                            
                            // Check if the current user is a member
                            // In a real app, we would query a members table
                            val isMember = true // Mock implementation
                            _uiState.update { it.copy(isMember = isMember) }
                            
                            // Load previous books (this would ideally come from a club history table)
                            // For now, we'll just show a few sample books as previous reads
                            loadSamplePreviousBooks()
                            
                            // Load members
                            loadSampleMembers()
                        } else {
                            _uiState.update { it.copy(
                                error = "Club not found",
                                isLoading = false
                            ) }
                        }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    error = "Failed to load club details: ${e.message}",
                    isLoading = false
                ) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
    
    private fun loadSamplePreviousBooks() {
        // In a real app, this would come from repository
        val previousBooks = listOf(
            Book(
                id = 10L,
                title = "The Alchemist",
                author = "Paulo Coelho",
                coverImageUrl = "https://via.placeholder.com/150/3498db/ffffff?text=Alchemist",
                description = "A story about following your dreams.",
                publishedDate = Date(),
                status = BookStatus.COMPLETED,
                totalPages = 197,
                rating = 4.2f,
                genre = "Fiction",
                isbn = "9780061122415"
            ),
            Book(
                id = 11L,
                title = "Dune",
                author = "Frank Herbert",
                coverImageUrl = "https://via.placeholder.com/150/9b59b6/ffffff?text=Dune",
                description = "A science fiction novel set in the distant future.",
                publishedDate = Date(),
                status = BookStatus.COMPLETED,
                totalPages = 412,
                rating = 4.6f,
                genre = "Science Fiction",
                isbn = "9780441172719"
            )
        )
        
        _uiState.update { it.copy(previousBooks = previousBooks) }
    }
    
    private fun loadSampleMembers() {
        // In a real app, this would come from repository
        val members = listOf(
            User(
                id = 1L,
                name = "Alice Reader",
                email = "alice@example.com",
                profileImageUrl = "https://via.placeholder.com/150/3498db/ffffff?text=A",
                bio = "Avid reader and book club organizer",
                totalBooksRead = 42
            ),
            User(
                id = 2L,
                name = "Bob Bookworm",
                email = "bob@example.com",
                profileImageUrl = "https://via.placeholder.com/150/e74c3c/ffffff?text=B",
                bio = "I love science fiction and fantasy books",
                totalBooksRead = 28
            ),
            User(
                id = 3L,
                name = "Carol Stories",
                email = "carol@example.com",
                profileImageUrl = "https://via.placeholder.com/150/2ecc71/ffffff?text=C",
                bio = "Mystery and thriller enthusiast",
                totalBooksRead = 35
            )
        )
        
        _uiState.update { it.copy(members = members) }
    }

    fun joinClub() {
        viewModelScope.launch {
            try {
                _uiState.value.club?.let { club ->
                    // Update repository
                    bookClubRepository.joinClub(currentUserId, club.id)
                    
                    // Update local state immediately for responsive UI
                    val updatedClub = club.copy(memberCount = club.memberCount + 1)
                    _uiState.update { it.copy(
                        club = updatedClub,
                        isMember = true,
                        error = null
                    ) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to join club: ${e.message}") }
            }
        }
    }

    fun leaveClub() {
        viewModelScope.launch {
            try {
                _uiState.value.club?.let { club ->
                    // Update repository
                    bookClubRepository.leaveClub(currentUserId, club.id)
                    
                    // Update local state immediately for responsive UI
                    val updatedMemberCount = (club.memberCount - 1).coerceAtLeast(0)
                    val updatedClub = club.copy(memberCount = updatedMemberCount)
                    _uiState.update { it.copy(
                        club = updatedClub,
                        isMember = false,
                        error = null
                    ) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to leave club: ${e.message}") }
            }
        }
    }

    fun updateCurrentBook(bookId: Long) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                
                _uiState.value.club?.let { club ->
                    // Update repository
                    bookClubRepository.updateCurrentBook(club.id, bookId)
                    
                    // Get updated book details
                    bookRepository.getBookById(bookId)?.let { book ->
                        // Update local state
                        val updatedClub = club.copy(currentBookId = bookId)
                        _uiState.update { it.copy(
                            club = updatedClub,
                            currentBook = book,
                            error = null
                        ) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to update book: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun scheduleNextMeeting(date: Long) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                
                _uiState.value.club?.let { club ->
                    // Update repository
                    bookClubRepository.scheduleMeeting(club.id, Date(date))
                    
                    // Update local state
                    val updatedClub = club.copy(nextMeetingDate = Date(date))
                    _uiState.update { it.copy(
                        club = updatedClub,
                        upcomingMeetingDate = Date(date),
                        error = null
                    ) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to schedule meeting: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
    
    fun addDiscussion() {
        viewModelScope.launch {
            try {
                _uiState.value.club?.let { club ->
                    // Update repository
                    bookClubRepository.addDiscussion(club.id)
                    
                    // Update local state
                    val newDiscussionCount = (_uiState.value.discussions ?: 0) + 1
                    val updatedClub = club.copy(discussionCount = newDiscussionCount)
                    _uiState.update { it.copy(
                        club = updatedClub,
                        discussions = newDiscussionCount,
                        error = null
                    ) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to add discussion: ${e.message}") }
            }
        }
    }
    
    fun refresh() {
        _uiState.value.club?.let { club ->
            loadClubDetails(club.id)
        }
    }
}