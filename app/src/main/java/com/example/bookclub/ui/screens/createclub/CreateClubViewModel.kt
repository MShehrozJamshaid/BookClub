package com.example.bookclub.ui.screens.createclub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookclub.data.model.BookClub
import com.example.bookclub.data.repository.BookClubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class CreateClubUiState(
    val name: String = "",
    val description: String = "",
    val coverImageUrl: String? = null,
    val isPublic: Boolean = true,
    val nextMeetingDate: Date? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isClubCreated: Boolean = false,
    val clubId: Long? = null
)

@HiltViewModel
class CreateClubViewModel @Inject constructor(
    private val bookClubRepository: BookClubRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateClubUiState())
    val uiState: StateFlow<CreateClubUiState> = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun updateCoverImageUrl(url: String?) {
        _uiState.update { it.copy(coverImageUrl = url) }
    }

    fun updateIsPublic(isPublic: Boolean) {
        _uiState.update { it.copy(isPublic = isPublic) }
    }

    fun updateNextMeetingDate(date: Date?) {
        _uiState.update { it.copy(nextMeetingDate = date) }
    }

    fun createClub() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val club = BookClub(
                    name = uiState.value.name,
                    description = uiState.value.description,
                    coverImageUrl = uiState.value.coverImageUrl,
                    isPublic = uiState.value.isPublic,
                    createdBy = 1, // Using default user ID of 1
                    nextMeetingDate = uiState.value.nextMeetingDate,
                    memberCount = 1 // Creator is the first member
                )
                val newClubId = bookClubRepository.insertClub(club)
                _uiState.update { it.copy(isClubCreated = true, clubId = newClubId) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun resetState() {
        _uiState.update { CreateClubUiState() }
    }
}