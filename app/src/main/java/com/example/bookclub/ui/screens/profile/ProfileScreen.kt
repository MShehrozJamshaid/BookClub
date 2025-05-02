@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.bookclub.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.bookclub.ui.components.BookCard
import com.example.bookclub.ui.components.BookClubCard
import com.example.bookclub.ui.components.LoadingIndicator
import com.example.bookclub.ui.components.SectionHeader

@Composable
fun ProfileScreen(
    onNavigateToBookDetail: (Long) -> Unit,
    onNavigateToClubDetail: (Long) -> Unit,
    onNavigateToEditProfile: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = onNavigateToEditProfile) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            LoadingIndicator()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
            ) {
                // Profile Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Profile Image
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .padding(8.dp)
                    ) {
                        AsyncImage(
                            model = uiState.user?.profileImageUrl ?: "https://via.placeholder.com/150",
                            contentDescription = "Profile image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Name
                    Text(
                        text = uiState.user?.name ?: "",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    // Bio
                    Text(
                        text = uiState.user?.bio ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Reading Streak
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = uiState.readingStreak.toString(),
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "Day Streak",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        // Books Read
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = uiState.totalBooksRead.toString(),
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "Books Read",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        // Clubs
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = uiState.clubs.size.toString(),
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "Clubs",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                // Currently Reading
                if (uiState.currentlyReading.isNotEmpty()) {
                    SectionHeader(
                        title = "Currently Reading",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.currentlyReading) { book ->
                            BookCard(
                                book = book,
                                onClick = { onNavigateToBookDetail(book.id) },
                                modifier = Modifier.width(160.dp)
                            )
                        }
                    }
                }

                // Want to Read
                if (uiState.wantToRead.isNotEmpty()) {
                    SectionHeader(
                        title = "Want to Read",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.wantToRead) { book ->
                            BookCard(
                                book = book,
                                onClick = { onNavigateToBookDetail(book.id) },
                                modifier = Modifier.width(160.dp)
                            )
                        }
                    }
                }

                // Read
                if (uiState.read.isNotEmpty()) {
                    SectionHeader(
                        title = "Read",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.read) { book ->
                            BookCard(
                                book = book,
                                onClick = { onNavigateToBookDetail(book.id) },
                                modifier = Modifier.width(160.dp)
                            )
                        }
                    }
                }

                // Clubs
                if (uiState.clubs.isNotEmpty()) {
                    SectionHeader(
                        title = "My Clubs",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        uiState.clubs.forEach { club ->
                            BookClubCard(
                                bookClub = club,
                                onClick = { onNavigateToClubDetail(club.id) },
                                onJoinClick = { /* Not needed in profile */ },
                                onLeaveClick = { /* Not needed in profile */ },
                                isMember = true
                            )
                        }
                    }
                }

                // Error message
                uiState.error?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}