package com.example.bookclub.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.bookclub.data.model.BookClub
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BookClubCard(
    bookClub: BookClub,
    onClick: () -> Unit,
    onJoinClick: () -> Unit,
    onLeaveClick: () -> Unit,
    isMember: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Cover Image
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .weight(0.3f)
                ) {
                    AsyncImage(
                        model = bookClub.coverImageUrl ?: "https://via.placeholder.com/150",
                        contentDescription = "Club cover",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Club Info
                Column(
                    modifier = Modifier.weight(0.7f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = bookClub.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = bookClub.description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

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
                        text = "${bookClub.memberCount} members",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Next Meeting
                bookClub.nextMeetingDate?.let { date ->
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
                            text = SimpleDateFormat("MMM d", Locale.getDefault()).format(date),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // Privacy
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (bookClub.isPublic) Icons.Default.Public else Icons.Default.Lock,
                        contentDescription = if (bookClub.isPublic) "Public" else "Private",
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (bookClub.isPublic) "Public" else "Private",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Join/Leave Button
            Button(
                onClick = if (isMember) onLeaveClick else onJoinClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isMember) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(if (isMember) "Leave Club" else "Join Club")
            }
        }
    }
} 