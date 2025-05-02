package com.example.bookclub.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/**
 * A card with clean, modern appearance
 */
@Composable
fun GlassyCard(
    modifier: Modifier = Modifier,
    alpha: Float = 0.7f,
    blurRadius: Dp = 10.dp,
    cornerRadius: Dp = 16.dp,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        content()
    }
}

/**
 * A surface with a clean appearance
 */
@Composable
fun GlassySurface(
    modifier: Modifier = Modifier,
    alpha: Float = 0.6f,
    cornerRadius: Dp = 24.dp,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 1.dp
    ) {
        content()
    }
}

/**
 * Provides a gradient background that can be used for screen backgrounds
 */
@Composable
fun GradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White,
                        Color.White.copy(alpha = 0.95f)
                    )
                )
            )
    ) {
        content()
    }
}

/**
 * A profile image with a simple border
 */
@Composable
fun GlassyProfileImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    borderWidth: Dp = 2.dp
) {
    Box(
        modifier = modifier
            .size(size + borderWidth * 2)
            .clip(RoundedCornerShape(100))
            .background(MaterialTheme.colorScheme.primary)
            .padding(borderWidth)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Profile image",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(100)),
            contentScale = ContentScale.Crop
        )
    }
}

/**
 * A shimmer effect for placeholder loading states
 */
@Composable
fun GlassyShimmer(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 8.dp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    )
}