@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.bookclub.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bookclub.navigation.Screen
import com.example.bookclub.ui.components.BookCard
import com.example.bookclub.ui.components.BookClubCard
import com.example.bookclub.ui.components.LoadingIndicator
import com.example.bookclub.ui.components.SectionHeader

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Club") },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddBook.route) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Book")
            }
        }
    ) { padding ->
        if (uiState.isLoading) {
            LoadingIndicator()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Reading Streak Section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Reading Streak",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "${uiState.readingStreak} days",
                                style = MaterialTheme.typography.headlineLarge
                            )
                        }
                    }
                }

                // Currently Reading Section
                item {
                    SectionHeader(
                        title = "Currently Reading",
                        actionText = "View All",
                        onActionClick = { navController.navigate(Screen.Bookshelf.route) }
                    )
                }
                items(uiState.currentlyReading) { book ->
                    BookCard(
                        book = book,
                        onClick = { navController.navigate(Screen.BookDetail.createRoute(book.id)) }
                    )
                }

                // Recommended Books Section
                item {
                    SectionHeader(
                        title = "Recommended for You",
                        actionText = "View All",
                        onActionClick = { navController.navigate(Screen.Search.route) }
                    )
                }
                items(uiState.recommendedBooks) { book ->
                    BookCard(
                        book = book,
                        onClick = { navController.navigate(Screen.BookDetail.createRoute(book.id)) }
                    )
                }

                // Popular Clubs Section
                item {
                    SectionHeader(
                        title = "Popular Clubs",
                        actionText = "View All",
                        onActionClick = { navController.navigate(Screen.BookClubs.route) }
                    )
                }
                items(uiState.popularClubs) { bookClub ->
                    BookClubCard(
                        bookClub = bookClub,
                        onClick = { navController.navigate(Screen.ClubDetail.createRoute(bookClub.id)) },
                        onJoinClick = { viewModel.joinClub(bookClub.id) },
                        onLeaveClick = { viewModel.leaveClub(bookClub.id) },
                        isMember = uiState.myClubs.any { it.id == bookClub.id }
                    )
                }
            }
        }
    }
}