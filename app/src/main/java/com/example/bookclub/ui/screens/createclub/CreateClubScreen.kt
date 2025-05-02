@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.bookclub.ui.screens.createclub

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.bookclub.ui.components.LoadingIndicator
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CreateClubScreen(
    onBackClick: () -> Unit,
    onClubCreated: (Long) -> Unit,
    viewModel: CreateClubViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var showDatePicker by remember { mutableStateOf(false) }

    // Show success dialog when club is created
    LaunchedEffect(uiState.isClubCreated, uiState.clubId) {
        if (uiState.isClubCreated && uiState.clubId != null) {
            onClubCreated(uiState.clubId!!)
        }
    }

    // Show success dialog when club is created
    if (uiState.isClubCreated) {
        AlertDialog(
            onDismissRequest = { /* Dialog cannot be dismissed */ },
            title = { Text("Success") },
            text = { Text("Club created successfully!") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetState()
                        onBackClick()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    // Show date picker dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            onDateSelected = { date ->
                viewModel.updateNextMeetingDate(date)
                showDatePicker = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Club") },
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Cover Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.coverImageUrl != null) {
                        AsyncImage(
                            model = uiState.coverImageUrl,
                            contentDescription = "Club cover",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Add cover image",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    OutlinedButton(
                        onClick = { /* TODO: Implement image picker */ },
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        Text("Add Cover")
                    }
                }

                // Name
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = { viewModel.updateName(it) },
                    label = { Text("Club Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Description
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = { viewModel.updateDescription(it) },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                // Privacy
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Public Club")
                    Switch(
                        checked = uiState.isPublic,
                        onCheckedChange = { viewModel.updateIsPublic(it) }
                    )
                }

                // Next Meeting Date
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = uiState.nextMeetingDate?.let {
                            SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(it)
                        } ?: "Select Next Meeting Date"
                    )
                }

                // Error message
                uiState.error?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Create Button
                Button(
                    onClick = { viewModel.createClub() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.name.isNotBlank() && uiState.description.isNotBlank()
                ) {
                    Text("Create Club")
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