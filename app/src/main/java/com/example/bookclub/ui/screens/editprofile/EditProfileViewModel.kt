package com.example.bookclub.ui.screens.editprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookclub.data.model.User
import com.example.bookclub.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditProfileUiState(
    val name: String = "",
    val bio: String = "",
    val profileImageUrl: String? = null,
    val isLoading: Boolean = false,
    val isProfileUpdated: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    // Store the original user data
    private var originalUser: User? = null

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Using getUserByIdSync instead of collecting from flow
                val user = userRepository.getUserByIdSync(1) ?: createDefaultUser()
                originalUser = user
                _uiState.update {
                    it.copy(
                        name = user.name,
                        bio = user.bio,
                        profileImageUrl = user.profileImageUrl,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Failed to load profile: ${e.message}",
                        isLoading = false
                    ) 
                }
                // If there's an error, use default values
                setDefaultProfileValues()
            }
        }
    }
    
    private fun setDefaultProfileValues() {
        _uiState.update {
            it.copy(
                name = "Book Lover",
                bio = "I love reading various genres and discussing books with others.",
                profileImageUrl = "https://via.placeholder.com/150",
                isLoading = false
            )
        }
    }
    
    private fun createDefaultUser(): User {
        return User(
            id = 1,
            name = "Book Lover",
            email = "booklover@example.com",
            bio = "I love reading various genres and discussing books with others.",
            profileImageUrl = "https://via.placeholder.com/150"
        )
    }

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun updateBio(bio: String) {
        _uiState.update { it.copy(bio = bio) }
    }

    fun updateProfileImageUrl(url: String?) {
        _uiState.update { it.copy(profileImageUrl = url) }
    }

    fun saveProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Get the original user or create a new default one
                val userToUpdate = originalUser ?: createDefaultUser()
                
                val updatedUser = userToUpdate.copy(
                    name = _uiState.value.name,
                    bio = _uiState.value.bio,
                    profileImageUrl = _uiState.value.profileImageUrl
                )
                
                userRepository.updateUser(updatedUser)
                _uiState.update { it.copy(isProfileUpdated = true, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Failed to save profile: ${e.message}",
                        isLoading = false
                    ) 
                }
            }
        }
    }

    fun resetState() {
        _uiState.update { EditProfileUiState() }
        loadUserProfile()
    }
}