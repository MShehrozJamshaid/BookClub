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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
    
    // Keep track of collection jobs to cancel them when needed
    private var dataLoadingJobs = mutableListOf<Job>()
    private var timeoutJob: Job? = null

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        // Cancel existing jobs
        dataLoadingJobs.forEach { it.cancel() }
        dataLoadingJobs.clear()
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
                // For now, using a mock user ID
                val user = userRepository.getUserByIdSync(1)
                if (user != null) {
                    _uiState.update { it.copy(user = user) }

                    // Load books by status in parallel
                    val currentlyReadingJob = viewModelScope.launch {
                        bookRepository.getBooksByStatus(BookStatus.READING)
                            .catch { e -> 
                                _uiState.update { it.copy(error = "Error loading books: ${e.message}") }
                            }
                            .collectLatest { books ->
                                _uiState.update { it.copy(currentlyReading = books) }
                            }
                    }
                    dataLoadingJobs.add(currentlyReadingJob)

                    val wantToReadJob = viewModelScope.launch {
                        bookRepository.getBooksByStatus(BookStatus.WANT_TO_READ)
                            .catch { e -> 
                                _uiState.update { it.copy(error = "Error loading books: ${e.message}") }
                            }
                            .collectLatest { books ->
                                _uiState.update { it.copy(wantToRead = books) }
                            }
                    }
                    dataLoadingJobs.add(wantToReadJob)

                    val completedBooksJob = viewModelScope.launch {
                        bookRepository.getBooksByStatus(BookStatus.COMPLETED)
                            .catch { e -> 
                                _uiState.update { it.copy(error = "Error loading books: ${e.message}") }
                            }
                            .collectLatest { books ->
                                _uiState.update { 
                                    it.copy(
                                        read = books,
                                        totalBooksRead = books.size
                                    ) 
                                }
                            }
                    }
                    dataLoadingJobs.add(completedBooksJob)

                    // Load user's clubs
                    val clubsJob = viewModelScope.launch {
                        bookClubRepository.getClubsByMember(user.id)
                            .catch { e -> 
                                _uiState.update { it.copy(error = "Error loading clubs: ${e.message}") }
                            }
                            .collectLatest { clubs ->
                                _uiState.update { it.copy(clubs = clubs) }
                            }
                    }
                    dataLoadingJobs.add(clubsJob)

                    // Set reading streak and stop loading
                    val readingStreak = userRepository.getReadingStreak(user.id)
                    _uiState.update { 
                        it.copy(
                            readingStreak = readingStreak,
                            isLoading = false
                        ) 
                    }
                    
                    // Cancel timeout since we've loaded the data
                    timeoutJob?.cancel()
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Failed to load profile: ${e.message}",
                        isLoading = false
                    ) 
                }
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
    
    override fun onCleared() {
        super.onCleared()
        // Cancel all jobs when ViewModel is cleared
        dataLoadingJobs.forEach { it.cancel() }
        timeoutJob?.cancel()
    }
}