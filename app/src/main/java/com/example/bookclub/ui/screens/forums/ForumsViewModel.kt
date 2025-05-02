package com.example.bookclub.ui.screens.forums

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class ForumDiscussion(
    val id: Long,
    val title: String,
    val preview: String,
    val authorName: String,
    val createdDate: Date,
    val commentCount: Int,
    val likeCount: Int
)

data class ForumsUiState(
    val discussions: List<ForumDiscussion> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ForumsViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow(ForumsUiState(isLoading = true))
    val uiState: StateFlow<ForumsUiState> = _uiState.asStateFlow()
    
    init {
        loadSampleDiscussions()
    }
    
    fun refresh() {
        loadSampleDiscussions()
    }
    
    private fun loadSampleDiscussions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // Simulate network delay
                delay(800)
                
                // Load sample discussions
                val sampleDiscussions = listOf(
                    ForumDiscussion(
                        id = 1,
                        title = "Thoughts on 'The Great Gatsby'",
                        preview = "I just finished reading The Great Gatsby and I have some thoughts about the symbolism used throughout the novel...",
                        authorName = "LiteraryFan42",
                        createdDate = Date(System.currentTimeMillis() - 86400000), // 1 day ago
                        commentCount = 24,
                        likeCount = 17
                    ),
                    ForumDiscussion(
                        id = 2,
                        title = "Recommendations for fantasy books like Lord of the Rings?",
                        preview = "I'm looking for epic fantasy series with detailed world-building and complex characters, similar to Tolkien's work...",
                        authorName = "BookDragon",
                        createdDate = Date(System.currentTimeMillis() - 172800000), // 2 days ago
                        commentCount = 31,
                        likeCount = 22
                    ),
                    ForumDiscussion(
                        id = 3,
                        title = "How do you organize your personal library?",
                        preview = "I have over 200 books and I'm struggling with the best way to organize them. Do you sort by genre, author, or something else?",
                        authorName = "OrganizedReader",
                        createdDate = Date(System.currentTimeMillis() - 259200000), // 3 days ago
                        commentCount = 19,
                        likeCount = 14
                    ),
                    ForumDiscussion(
                        id = 4,
                        title = "Book vs Movie: 'Dune' discussion",
                        preview = "I'd like to discuss how the recent Dune movie adaptation compares to the original novel by Frank Herbert...",
                        authorName = "SciFiBuff",
                        createdDate = Date(System.currentTimeMillis() - 345600000), // 4 days ago
                        commentCount = 42,
                        likeCount = 28
                    ),
                    ForumDiscussion(
                        id = 5,
                        title = "Best books of 2024 so far",
                        preview = "We're halfway through the year, and I'd love to hear what everyone thinks are the best new releases of 2024 so far...",
                        authorName = "TrendyReader",
                        createdDate = Date(System.currentTimeMillis() - 432000000), // 5 days ago
                        commentCount = 16,
                        likeCount = 11
                    )
                )
                
                _uiState.update { it.copy(discussions = sampleDiscussions, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to load discussions: ${e.message}", isLoading = false) }
            }
        }
    }
}