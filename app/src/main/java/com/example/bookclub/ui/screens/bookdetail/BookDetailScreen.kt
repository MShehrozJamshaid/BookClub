@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.bookclub.ui.screens.bookdetail

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
import com.example.bookclub.data.model.BookStatus
import com.example.bookclub.ui.components.LoadingIndicator
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BookDetailScreen(
    bookId: Long,
    onBackClick: () -> Unit,
    viewModel: BookDetailViewModel = hiltViewModel()
) {
    // The BookDetailViewModel automatically loads the book using the bookId from SavedStateHandle
    
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    var showProgressDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }
    var showRatingDialog by remember { mutableStateOf(false) }

    // Show dialogs
    uiState.book?.let { book ->
        if (showProgressDialog) {
            UpdateProgressDialog(
                currentPage = book.currentPage,
                totalPages = book.totalPages,
                onDismiss = { showProgressDialog = false },
                onConfirm = { page ->
                    viewModel.updateReadingProgress(page)
                    showProgressDialog = false
                }
            )
        }

        if (showStatusDialog) {
            StatusSelectionDialog(
                currentStatus = book.status,
                onDismiss = { showStatusDialog = false },
                onStatusSelected = { status ->
                    viewModel.updateBookStatus(status)
                    showStatusDialog = false
                }
            )
        }

        if (showRatingDialog) {
            RatingDialog(
                currentRating = book.rating,
                onDismiss = { showRatingDialog = false },
                onRatingSelected = { rating ->
                    viewModel.updateRating(rating)
                    showRatingDialog = false
                }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            LoadingIndicator()
        } else {
            uiState.book?.let { book ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(scrollState)
                ) {
                    // Book Cover and Basic Info
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        AsyncImage(
                            model = book.coverImageUrl ?: "https://via.placeholder.com/150x200",
                            contentDescription = "${book.title} cover",
                            modifier = Modifier
                                .width(150.dp)
                                .height(200.dp),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = book.title,
                                style = MaterialTheme.typography.headlineSmall,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = book.author,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = book.genre,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (book.rating > 0) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Rating",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = String.format("%.1f", book.rating),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }

                    // Reading Progress
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Reading Progress",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = if (book.totalPages > 0) {
                                    book.currentPage.toFloat() / book.totalPages
                                } else 0f,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Page ${book.currentPage} of ${book.totalPages}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "${((book.currentPage.toFloat() / book.totalPages) * 100).toInt()}%",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = { showProgressDialog = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Update Progress")
                            }
                        }
                    }

                    // Book Description
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Description",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = book.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Book Details
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Details",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            DetailRow("ISBN", book.isbn ?: "Not available")
                            DetailRow("Published", book.publishedDate?.let {
                                SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(it)
                            } ?: "Not available")
                            DetailRow("Added", SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(book.addedDate))
                            book.lastReadDate?.let {
                                DetailRow("Last Read", SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(it))
                            }
                        }
                    }

                    // Status and Rating
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showStatusDialog = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(book.status.name)
                        }
                        OutlinedButton(
                            onClick = { showRatingDialog = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Rate Book")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}