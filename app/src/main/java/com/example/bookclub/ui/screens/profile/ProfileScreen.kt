@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.bookclub.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.bookclub.ui.components.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

@Composable
fun ProfileScreen(
    onNavigateToBookDetail: (Long) -> Unit,
    onNavigateToClubDetail: (Long) -> Unit,
    onNavigateToEditProfile: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    
    // Filter for currently reading books section
    var selectedReadingFilter by remember { mutableStateOf("All") }
    val readingFilters = listOf("All", "Fiction", "Non-Fiction", "Recent")
    
    // Refresh profile data when returning to this screen
    LaunchedEffect(Unit) {
        viewModel.refresh()
    }
    
    // Random profile image
    val randomProfileImages = remember {
        listOf(
            "https://randomuser.me/api/portraits/women/${Random.nextInt(1, 70)}.jpg",
            "https://randomuser.me/api/portraits/men/${Random.nextInt(1, 70)}.jpg"
        ).random()
    }
    
    // User name
    val userName = remember { uiState.user?.name ?: "Avid Reader" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
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
        GradientBackground(
            modifier = Modifier.fillMaxSize()
        ) {
            if (uiState.isLoading) {
                LoadingIndicator()
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(scrollState)
                ) {
                    // Profile Header with Background
                    GlassySurface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        alpha = 0.5f
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Profile Image with glassy effect
                            GlassyProfileImage(
                                imageUrl = uiState.user?.profileImageUrl ?: randomProfileImages,
                                size = 120.dp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Name
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )

                            // Member since
                            Text(
                                text = "Book Lover since ${SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Profile Bio Section using GlassyCard
                    GlassyCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        cornerRadius = 16.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = "About me",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "About Me",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Text(
                                text = uiState.user?.bio ?: "Book enthusiast with a passion for literature and thoughtful discussions.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            // Favorite Genres
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Favorite Genres",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium
                            )
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val favoriteGenres = uiState.user?.favoriteGenres ?: listOf("Fiction", "Mystery", "Science Fiction")
                                favoriteGenres.forEach { genre ->
                                    GenreChip(genre = genre)
                                }
                            }
                        }
                    }

                    // Reading Statistics with GlassyCard
                    GlassyCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        cornerRadius = 16.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Reading Statistics",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                StatItem(
                                    title = "Reading Streak",
                                    value = uiState.readingStreak.toString(),
                                    unit = "days",
                                    icon = Icons.Outlined.Timelapse
                                )
                                StatItem(
                                    title = "Books Read",
                                    value = uiState.totalBooksRead.toString(),
                                    unit = "books",
                                    icon = Icons.Outlined.MenuBook
                                )
                                StatItem(
                                    title = "Pages Read",
                                    value = uiState.user?.totalPagesRead?.toString() ?: "0",
                                    unit = "pages",
                                    icon = Icons.Outlined.Article
                                )
                            }
                            
                            LinearProgressIndicator(
                                progress = { 0.65f }, // Yearly reading goal progress
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.primaryContainer
                            )
                            
                            Text(
                                text = "65% of your yearly reading goal",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Achievements section with glassy row
                    SectionHeader(
                        title = "Achievements",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = getAchievements(),
                            key = { achievement -> achievement.title }
                        ) { achievement ->
                            GlassySurface(
                                modifier = Modifier.width(120.dp),
                                alpha = 0.6f,
                                cornerRadius = 16.dp
                            ) {
                                AchievementItem(achievement = achievement)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    // Currently Reading with glassy cards
                    if (uiState.currentlyReading.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Currently Reading",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        // Filter chips with glassy style
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(readingFilters) { filter ->
                                FilterChip(
                                    selected = selectedReadingFilter == filter,
                                    onClick = { selectedReadingFilter = filter },
                                    label = { Text(filter) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                                    )
                                )
                            }
                        }
                        
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(uiState.currentlyReading) { book ->
                                GlassyCard(
                                    modifier = Modifier.width(160.dp),
                                    alpha = 0.8f
                                ) {
                                    BookCard(
                                        book = book,
                                        onClick = { onNavigateToBookDetail(book.id) },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }

                    // Want to Read with glassy cards
                    if (uiState.wantToRead.isNotEmpty()) {
                        SectionHeader(
                            title = "Want to Read",
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(uiState.wantToRead) { book ->
                                GlassyCard(
                                    modifier = Modifier.width(160.dp),
                                    alpha = 0.8f
                                ) {
                                    BookCard(
                                        book = book,
                                        onClick = { onNavigateToBookDetail(book.id) },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }

                    // Read - Recently completed books with glassy cards
                    if (uiState.read.isNotEmpty()) {
                        SectionHeader(
                            title = "Recently Completed",
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(uiState.read) { book ->
                                GlassyCard(
                                    modifier = Modifier.width(160.dp),
                                    alpha = 0.8f
                                ) {
                                    BookCard(
                                        book = book,
                                        onClick = { onNavigateToBookDetail(book.id) },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }

                    // Clubs with glassy cards and cover images
                    if (uiState.clubs.isNotEmpty()) {
                        SectionHeader(
                            title = "My Clubs",
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            uiState.clubs.forEach { club ->
                                GlassyCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    alpha = 0.8f
                                ) {
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
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun GenreChip(genre: String) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = genre,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun StatItem(title: String, value: String, unit: String, icon: ImageVector) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = unit,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

data class Achievement(
    val title: String,
    val icon: ImageVector,
    val description: String,
    val isUnlocked: Boolean
)

fun getAchievements(): List<Achievement> {
    return listOf(
        Achievement(
            title = "Bookworm",
            icon = Icons.Outlined.AutoStories,
            description = "Read 10 books",
            isUnlocked = true
        ),
        Achievement(
            title = "Social Reader",
            icon = Icons.Outlined.Groups,
            description = "Join 3 book clubs",
            isUnlocked = true
        ),
        Achievement(
            title = "Genre Explorer",
            icon = Icons.Outlined.Explore,
            description = "Read books from 5 different genres",
            isUnlocked = true
        ),
        Achievement(
            title = "Dedicated Reader",
            icon = Icons.Outlined.Favorite,
            description = "Maintain a 30-day reading streak",
            isUnlocked = false
        ),
        Achievement(
            title = "Completionist",
            icon = Icons.Outlined.CheckCircle,
            description = "Complete a 500+ page book",
            isUnlocked = false
        )
    )
}

@Composable
fun AchievementItem(achievement: Achievement) {
    Box(
        modifier = Modifier.padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = achievement.icon,
                contentDescription = achievement.title,
                tint = if (achievement.isUnlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                modifier = Modifier.size(32.dp)
            )
            
            Text(
                text = achievement.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (achievement.isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                textAlign = TextAlign.Center
            )
            
            Text(
                text = achievement.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}