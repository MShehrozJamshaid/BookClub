@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.bookclub.ui.screens.bookclubs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bookclub.ui.components.BookClubCard
import com.example.bookclub.ui.components.LoadingIndicator

@Composable
fun BookClubsScreen(
    onNavigateToClubDetail: (Long) -> Unit,
    onNavigateToCreateClub: () -> Unit,
    viewModel: BookClubsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Clubs") },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateClub,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Club")
            }
        }
    ) { padding ->
        if (uiState.isLoading) {
            LoadingIndicator()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Tabs
                TabRow(selectedTabIndex = uiState.selectedTab.ordinal) {
                    ClubTab.values().forEach { tab ->
                        Tab(
                            selected = uiState.selectedTab == tab,
                            onClick = { viewModel.updateSelectedTab(tab) },
                            text = { Text(tab.name.replace("_", " ")) }
                        )
                    }
                }

                // Content
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val clubs = when (uiState.selectedTab) {
                            ClubTab.PUBLIC -> uiState.publicClubs
                            ClubTab.MY_CLUBS -> uiState.myClubs
                        }

                        items(clubs) { club ->
                            BookClubCard(
                                bookClub = club,
                                onClick = { onNavigateToClubDetail(club.id) },
                                onJoinClick = { viewModel.joinClub(club.id) },
                                onLeaveClick = { viewModel.leaveClub(club.id) },
                                isMember = uiState.myClubs.any { it.id == club.id }
                            )
                        }
                    }

                    // Error message
                    uiState.error?.let { error ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
} 