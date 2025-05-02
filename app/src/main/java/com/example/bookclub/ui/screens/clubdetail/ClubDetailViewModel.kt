package com.example.bookclub.ui.screens.clubdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookclub.data.model.Book
import com.example.bookclub.data.model.BookClub
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

data class ClubDetailUiState(
    val club: BookClub? = null,
    val currentBook: Book? = null,
    val isMember: Boolean = false,
    val isLoading: Boolean = false,
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

    fun loadClubDetails(clubId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                bookClubRepository.getClubById(clubId).collect { club ->
                    _uiState.update { it.copy(club = club) }
                    
                    // Load current book if available
                    if (club?.currentBookId != null) {
                        val book = bookRepository.getBookById(club.currentBookId)
                        _uiState.update { it.copy(currentBook = book) }
                    }
                    
                    // Check if the current user is a member
                    // For now, let's assume user 1 is always logged in
                    val userId = 1L
                    val isMember = true // Mock implementation - in a real app would check membership
                    _uiState.update { it.copy(isMember = isMember) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun joinClub() {
        viewModelScope.launch {
            try {
                _uiState.value.club?.let { club ->
                    // For now, using a mock user ID
                    bookClubRepository.addMember(club.id, 1)
                    _uiState.update { it.copy(isMember = true) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun leaveClub() {
        viewModelScope.launch {
            try {
                _uiState.value.club?.let { club ->
                    // For now, using a mock user ID
                    bookClubRepository.removeMember(club.id, 1)
                    _uiState.update { it.copy(isMember = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun updateCurrentBook(bookId: Long) {
        viewModelScope.launch {
            try {
                _uiState.value.club?.let { club ->
                    bookClubRepository.updateCurrentBook(club.id, bookId)
                    bookRepository.getBookById(bookId)?.let { book ->
                        _uiState.update { it.copy(currentBook = book) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun scheduleNextMeeting(date: Long) {
        viewModelScope.launch {
            try {
                _uiState.value.club?.let { club ->
                    bookClubRepository.updateNextMeetingDate(club.id, date)
                    _uiState.update { it.copy(club = club.copy(nextMeetingDate = Date(date))) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}