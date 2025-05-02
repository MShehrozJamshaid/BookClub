package com.example.bookclub.ui.screens.profile

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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
    val currentlyReading: List<Book> = emptyList(),
    val wantToRead: List<Book> = emptyList(),
    val read: List<Book> = emptyList(),
    val clubs: List<BookClub> = emptyList(),
    val readingStreak: Int = 0,
    val totalBooksRead: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val bookRepository: BookRepository,
    private val bookClubRepository: BookClubRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // For now, using a mock user ID
                val user = userRepository.getUserByIdSync(1)
                if (user != null) {
                    _uiState.update { it.copy(user = user) }

                    // Load books by status
                    bookRepository.getBooksByStatus(BookStatus.READING)
                        .collect { books ->
                            _uiState.update { it.copy(currentlyReading = books) }
                        }

                    bookRepository.getBooksByStatus(BookStatus.WANT_TO_READ)
                        .collect { books ->
                            _uiState.update { it.copy(wantToRead = books) }
                        }

                    bookRepository.getBooksByStatus(BookStatus.COMPLETED)
                        .collect { books ->
                            _uiState.update { it.copy(read = books) }
                        }

                    // Load user's clubs
                    bookClubRepository.getClubsByMember(user.id)
                        .collect { clubs ->
                            _uiState.update { it.copy(clubs = clubs) }
                        }

                    // Calculate statistics
                    val totalBooksRead = bookRepository.getBooksByStatus(BookStatus.COMPLETED).first().size
                    val readingStreak = userRepository.getReadingStreak(user.id)
                    _uiState.update { it.copy(totalBooksRead = totalBooksRead, readingStreak = readingStreak) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateProfile(
        name: String,
        bio: String,
        profileImageUrl: String?
    ) {
        viewModelScope.launch {
            try {
                _uiState.value.user?.let { user ->
                    val updatedUser = user.copy(
                        name = name,
                        bio = bio,
                        profileImageUrl = profileImageUrl
                    )
                    userRepository.updateUser(updatedUser)
                    _uiState.update { it.copy(user = updatedUser) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun refresh() {
        loadProfileData()
    }
}