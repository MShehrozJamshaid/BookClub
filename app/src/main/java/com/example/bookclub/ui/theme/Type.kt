package com.example.bookclub.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Create a serif font family for book titles and headings for an elegant look
val AppleSerif = FontFamily.SansSerif

// Set of Material typography styles to start with
val Typography = Typography(
    // Large section titles (like Apple Books main headers)
    headlineLarge = TextStyle(
        fontFamily = AppleSerif,
        fontWeight = FontWeight.Bold,  // Increased from SemiBold
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.5).sp // Negative letter spacing like Apple uses
    ),
    
    // Section headers
    headlineMedium = TextStyle(
        fontFamily = AppleSerif,
        fontWeight = FontWeight.Bold,  // Increased from SemiBold
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.5).sp
    ),
    
    // Large titles (like book titles in detail view)
    titleLarge = TextStyle(
        fontFamily = AppleSerif,
        fontWeight = FontWeight.Bold,  // Increased from Medium
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.25).sp
    ),
    
    // Medium titles (like book titles in cards)
    titleMedium = TextStyle(
        fontFamily = AppleSerif,
        fontWeight = FontWeight.Bold,  // Increased from Medium
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = (-0.25).sp
    ),
    
    // Small titles (like author names)
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,  // Increased from Medium
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    ),
    
    // Body text (like book descriptions)
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,  // Increased from Normal
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    
    // Secondary body text
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,  // Increased from Normal
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    
    // Small captions and metadata
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,  // Increased from Normal
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
        color = MediumGray
    ),
    
    // Labels for buttons and UI elements
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,  // Increased from Medium
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    
    // Small UI labels
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,  // Increased from Medium
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)