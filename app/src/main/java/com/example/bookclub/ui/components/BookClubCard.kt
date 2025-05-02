package com.example.bookclub.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.bookclub.data.model.BookClub
import com.example.bookclub.ui.theme.GlassBlue
import com.example.bookclub.ui.theme.GlassWhite
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
    // Random pastel color for each club's card
    val randomHue = (bookClub.id.hashCode() % 360).toFloat()
    val pastelColor = Color.hsv(randomHue, 0.3f, 0.95f)
    val coverImageUrl = bookClub.coverImageUrl ?: "https://picsum.photos/seed/${bookClub.id}/300/200"
    
    GlassyCard(
        modifier = modifier.fillMaxWidth(),
        alpha = 0.85f,  // Changed containerColor to alpha
        cornerRadius = 20.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Cover Image with rounded corners
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .weight(0.3f)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    AsyncImage(
                        model = coverImageUrl,
                        contentDescription = "Club cover",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Club Info
                Column(
                    modifier = Modifier.weight(0.7f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
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

            // Stats with glassy effect
            GlassySurface(
                cornerRadius = 12.dp,
                alpha = 0.2f  // Changed contentPadding and containerColor to just alpha
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),  // Added padding here instead
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
            }

            // Join/Leave Button with glassy effect
            Button(
                onClick = if (isMember) onLeaveClick else onJoinClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isMember) 
                        MaterialTheme.colorScheme.error.copy(alpha = 0.9f) 
                    else 
                        GlassBlue
                )
            ) {
                Text(
                    if (isMember) "Leave Club" else "Join Club",
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}