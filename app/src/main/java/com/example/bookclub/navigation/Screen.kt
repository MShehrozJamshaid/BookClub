package com.example.bookclub.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Bookshelf : Screen("bookshelf")
    object BookDetail : Screen("book_detail/{bookId}") {
        fun createRoute(bookId: Long) = "book_detail/$bookId"
    }
    object BookClubs : Screen("book_clubs")
    object ClubDetail : Screen("club_detail/{clubId}") {
        fun createRoute(clubId: Long) = "club_detail/$clubId"
    }
    object CreateClub : Screen("create_club")
    object Forums : Screen("forums")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object Search : Screen("search")
    object AddBook : Screen("add_book")
    object EditProfile : Screen("edit_profile")
    object SelectBook : Screen("select_book")
    object ReadingGoals : Screen("reading_goals")
    object Achievements : Screen("achievements")
}