@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.bookclub.ui.screens.clubdetail

import androidx.compose.foundation.layout.*
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
import com.example.bookclub.data.model.Book
import com.example.bookclub.ui.components.BookCard
import com.example.bookclub.ui.components.LoadingIndicator
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ClubDetailScreen(
    clubId: Long,
    onBackClick: () -> Unit,
    onNavigateToBookDetail: (Long) -> Unit,
    onNavigateToSelectBook: () -> Unit,
    viewModel: ClubDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(clubId) {
        viewModel.loadClubDetails(clubId)
    }

    // Show date picker dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            onDateSelected = { date ->
                viewModel.scheduleNextMeeting(date.time)
                showDatePicker = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.club?.name ?: "Club Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                // Cover Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    AsyncImage(
                        model = uiState.club?.coverImageUrl ?: "https://via.placeholder.com/150",
                        contentDescription = "Club cover",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Club Info
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Description
                    Text(
                        text = uiState.club?.description ?: "",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    // Stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Members
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = "Members",
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${uiState.club?.memberCount ?: 0} members",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        // Next Meeting
                        uiState.club?.nextMeetingDate?.let { date ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Event,
                                    contentDescription = "Next meeting",
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        // Privacy
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (uiState.club?.isPublic == true) Icons.Default.Public else Icons.Default.Lock,
                                contentDescription = if (uiState.club?.isPublic == true) "Public" else "Private",
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (uiState.club?.isPublic == true) "Public" else "Private",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Current Book Section
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Current Book",
                                style = MaterialTheme.typography.titleMedium
                            )
                            if (uiState.isMember) {
                                TextButton(onClick = onNavigateToSelectBook) {
                                    Text("Change Book")
                                }
                            }
                        }

                        uiState.currentBook?.let { book ->
                            BookCard(
                                book = book,
                                onClick = { onNavigateToBookDetail(book.id) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        } ?: run {
                            if (uiState.isMember) {
                                Button(
                                    onClick = onNavigateToSelectBook,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Select Current Book")
                                }
                            } else {
                                Text(
                                    text = "No book selected",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Join/Leave Button
                    if (uiState.club != null) {
                        Button(
                            onClick = {
                                if (uiState.isMember) {
                                    viewModel.leaveClub()
                                } else {
                                    viewModel.joinClub()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (uiState.isMember) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(if (uiState.isMember) "Leave Club" else "Join Club")
                        }
                    }

                    // Schedule Next Meeting Button
                    if (uiState.isMember) {
                        OutlinedButton(
                            onClick = { showDatePicker = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Schedule Next Meeting")
                        }
                    }

                    // Error message
                    uiState.error?.let { error ->
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

@Composable
private fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (Date) -> Unit
) {
    var selectedDate by remember { mutableStateOf(Date()) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Select Next Meeting Date") },
        text = {
            DatePicker(
                state = rememberDatePickerState(
                    initialSelectedDateMillis = selectedDate.time
                ),
                showModeToggle = false
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(selectedDate)
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
} 