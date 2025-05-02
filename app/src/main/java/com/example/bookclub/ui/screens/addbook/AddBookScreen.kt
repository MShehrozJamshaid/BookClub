@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.bookclub.ui.screens.addbook

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.bookclub.ui.components.LoadingIndicator
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddBookScreen(
    onBackClick: () -> Unit,
    viewModel: AddBookViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var showDatePicker by remember { mutableStateOf(false) }

    // Show success dialog when book is added
    if (uiState.isBookAdded) {
        AlertDialog(
            onDismissRequest = { /* Dialog cannot be dismissed */ },
            title = { Text("Success") },
            text = { Text("Book added successfully!") },
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
                viewModel.updatePublishedDate(date)
                showDatePicker = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Book") },
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
                            contentDescription = "Book cover",
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

                // Title
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = { viewModel.updateTitle(it) },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Author
                OutlinedTextField(
                    value = uiState.author,
                    onValueChange = { viewModel.updateAuthor(it) },
                    label = { Text("Author") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Genre
                OutlinedTextField(
                    value = uiState.genre,
                    onValueChange = { viewModel.updateGenre(it) },
                    label = { Text("Genre") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // ISBN
                OutlinedTextField(
                    value = uiState.isbn,
                    onValueChange = { viewModel.updateIsbn(it) },
                    label = { Text("ISBN (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Total Pages
                OutlinedTextField(
                    value = uiState.totalPages,
                    onValueChange = { viewModel.updateTotalPages(it) },
                    label = { Text("Total Pages") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Published Date
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = uiState.publishedDate?.let {
                            SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(it)
                        } ?: "Select Publication Date"
                    )
                }

                // Description
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = { viewModel.updateDescription(it) },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                // Error message
                uiState.error?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Add Button
                Button(
                    onClick = { viewModel.addBook() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.title.isNotBlank() && uiState.author.isNotBlank()
                ) {
                    Text("Add Book")
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
        title = { Text("Select Publication Date") },
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