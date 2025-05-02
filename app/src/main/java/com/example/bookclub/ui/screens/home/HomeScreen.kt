@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.bookclub.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bookclub.R
import com.example.bookclub.data.model.Book
import com.example.bookclub.data.model.BookClub
import com.example.bookclub.navigation.Screen
import com.example.bookclub.ui.components.BookCard
import com.example.bookclub.ui.components.BookClubCard
import com.example.bookclub.ui.components.GlassyCard
import com.example.bookclub.ui.components.GlassySurface
import com.example.bookclub.ui.components.GradientBackground
import com.example.bookclub.ui.components.LoadingIndicator
import com.example.bookclub.ui.theme.GlassBlue
import com.example.bookclub.ui.theme.GlassWhite
import com.example.bookclub.ui.theme.LocalGradients
import com.example.bookclub.ui.theme.SoftGray
import com.example.bookclub.ui.theme.SoftShadow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val gradientBrush = LocalGradients.current.backgroundGradient
    
    // For the reading streak adjustment dialog
    var showStreakDialog by remember { mutableStateOf(false) }
    var streakAdjustmentValue by remember(uiState.readingStreak) { mutableStateOf(uiState.readingStreak.toString()) }

    Scaffold(
        topBar = {
            Surface(
                color = Color.Transparent,
                shadowElevation = 0.dp
            ) {
                TopAppBar(
                    title = { 
                        Text(
                            text = "Book Club",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    actions = {
                        // Section name in top right
                        Text(
                            text = "Home",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                        
                        IconButton(onClick = { viewModel.refresh() }) {
                            Icon(
                                Icons.Default.Refresh, 
                                contentDescription = "Refresh",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddBook.route) },
                containerColor = GlassBlue,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Book")
            }
        },
        containerColor = Color(0xFFFFF8F0) // Light cream background
    ) { padding ->
        // Gradient background applied to the entire screen
        GradientBackground {
            if (uiState.isLoading) {
                LoadingIndicator()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    // Reading Streak Section with enhanced interactivity
                    item {
                        GlassyCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            cornerRadius = 20.dp,
                            alpha = 0.15f
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Reading Streak",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    
                                    // Button to open streak adjustment dialog
                                    IconButton(
                                        onClick = { showStreakDialog = true },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Edit streak",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = "${uiState.readingStreak} days",
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Last read date
                                uiState.lastReadDate?.let { lastDate ->
                                    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                    Text(
                                        text = "Last read: ${dateFormat.format(lastDate)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                LinearProgressIndicator(
                                    progress = { uiState.readingStreak.toFloat() / 30 }, // Target 30 days for example
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Streak controls
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    StreakControlButton(
                                        icon = Icons.Default.Add,
                                        text = "Log Reading",
                                        onClick = { viewModel.updateReadingStreak(true) }
                                    )
                                    
                                    StreakControlButton(
                                        icon = Icons.Default.Delete,
                                        text = "Reset Streak",
                                        onClick = { viewModel.resetReadingStreak() }
                                    )
                                }
                            }
                        }
                    }

                    // Section Header style - Apple Books like
                    item {
                        SectionTitle(
                            title = "Currently Reading",
                            actionText = "See All",
                            onActionClick = { navController.navigate(Screen.Bookshelf.route) }
                        )
                    }
                    
                    // Apple Books style horizontal scrolling book covers with glass effect
                    item {
                        if (uiState.currentlyReading.isNotEmpty()) {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(uiState.currentlyReading) { book ->
                                    BookCoverCard(
                                        book = book,
                                        onClick = { 
                                            navController.navigate(Screen.BookDetail.createRoute(book.id)) 
                                        }
                                    )
                                }
                            }
                        } else {
                            EmptyStateMessage(
                                message = "You're not reading any books yet",
                                actionText = "Add a book",
                                onActionClick = { navController.navigate(Screen.AddBook.route) }
                            )
                        }
                    }

                    // Recommended Books Section
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionTitle(
                            title = "Recommendations",
                            actionText = "Browse",
                            onActionClick = { navController.navigate(Screen.Search.route) }
                        )
                    }
                    
                    item {
                        if (uiState.recommendedBooks.isNotEmpty()) {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(uiState.recommendedBooks) { book ->
                                    BookCoverCard(
                                        book = book,
                                        onClick = { 
                                            navController.navigate(Screen.BookDetail.createRoute(book.id)) 
                                        }
                                    )
                                }
                            }
                        } else {
                            EmptyStateMessage(
                                message = "No recommendations yet",
                                actionText = "Explore",
                                onActionClick = { navController.navigate(Screen.Search.route) }
                            )
                        }
                    }

                    // Popular Clubs Section
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionTitle(
                            title = "Reading Clubs",
                            actionText = "All Clubs",
                            onActionClick = { navController.navigate(Screen.BookClubs.route) }
                        )
                    }
                    
                    // Club cards with glassy UI styling
                    items(uiState.popularClubs) { bookClub ->
                        BookClubCard(
                            bookClub = bookClub,
                            onClick = { 
                                navController.navigate(Screen.ClubDetail.createRoute(bookClub.id)) 
                            },
                            onJoinClick = { viewModel.joinClub(bookClub.id) },
                            onLeaveClick = { viewModel.leaveClub(bookClub.id) },
                            isMember = uiState.myClubs.any { it.id == bookClub.id },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    
                    // Bottom spacing to avoid FAB overlap
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
        
        // Streak adjustment dialog
        if (showStreakDialog) {
            AlertDialog(
                onDismissRequest = { showStreakDialog = false },
                title = { Text("Adjust Reading Streak") },
                text = {
                    Column {
                        Text("Enter your current reading streak:")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = streakAdjustmentValue,
                            onValueChange = { 
                                // Only accept numeric inputs
                                if (it.all { char -> char.isDigit() } || it.isEmpty()) {
                                    streakAdjustmentValue = it
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { 
                            val newStreak = streakAdjustmentValue.toIntOrNull() ?: 0
                            viewModel.setReadingStreak(newStreak)
                            showStreakDialog = false
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showStreakDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun StreakControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Text(text)
        }
    }
}

@Composable
fun SectionTitle(
    title: String,
    actionText: String? = null,
    onActionClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        
        if (actionText != null) {
            TextButton(onClick = onActionClick) {
                Text(
                    text = actionText,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun BookCoverCard(
    book: Book,
    onClick: () -> Unit
) {
    // Use glassy card with enhanced shadow effect
    GlassyCard(
        modifier = Modifier
            .width(140.dp)
            .height(220.dp)
            .clickable(onClick = onClick),
        cornerRadius = 16.dp,
        alpha = 0.7f,  // Changed from shadowElevation and containerColor
        blurRadius = 8.dp  // Added blur radius as a substitute for shadow elevation
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Book Cover Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                AsyncImage(
                    model = book.coverImageUrl ?: "https://picsum.photos/seed/${book.id}/300/450",
                    contentDescription = book.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                )
            }
            
            // Book Title
            Text(
                text = book.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun EmptyStateMessage(
    message: String,
    actionText: String? = null,
    onActionClick: () -> Unit = {}
) {
    GlassySurface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 16.dp),
        cornerRadius = 16.dp,
        alpha = 0.6f  // Changed containerColor to alpha
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            
            if (actionText != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onActionClick,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GlassBlue
                    )
                ) {
                    Text(actionText)
                }
            }
        }
    }
}