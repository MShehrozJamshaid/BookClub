package com.example.bookclub.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bookclub.ui.screens.home.HomeScreen
import com.example.bookclub.ui.screens.bookshelf.BookshelfScreen
import com.example.bookclub.ui.screens.bookdetail.BookDetailScreen
import com.example.bookclub.ui.screens.bookclubs.BookClubsScreen
import com.example.bookclub.ui.screens.clubdetail.ClubDetailScreen
import com.example.bookclub.ui.screens.createclub.CreateClubScreen
import com.example.bookclub.ui.screens.profile.ProfileScreen
import com.example.bookclub.ui.screens.addbook.AddBookScreen
import com.example.bookclub.ui.screens.editprofile.EditProfileScreen
import com.example.bookclub.ui.screens.forums.ForumsScreen
import com.example.bookclub.ui.screens.selectbook.SelectBookScreen
import com.example.bookclub.ui.screen.OnlineBookSearchScreen

@Composable
fun BookClubNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Home.route,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController
            )
        }
        
        composable(Screen.Bookshelf.route) {
            BookshelfScreen(
                onAddBookClick = { navController.navigate(Screen.AddBook.route) },
                onBookClick = { bookId -> 
                    navController.navigate(Screen.BookDetail.createRoute(bookId)) 
                }
            )
        }
        
        composable(
            route = Screen.BookDetail.route,
            arguments = listOf(navArgument("bookId") { type = NavType.LongType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getLong("bookId")
            BookDetailScreen(
                bookId = bookId ?: 0L,
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(Screen.BookClubs.route) {
            BookClubsScreen(
                onNavigateToClubDetail = { clubId ->
                    navController.navigate(Screen.ClubDetail.createRoute(clubId))
                },
                onNavigateToCreateClub = { 
                    navController.navigate(Screen.CreateClub.route) 
                }
            )
        }
        
        composable(
            route = Screen.ClubDetail.route,
            arguments = listOf(navArgument("clubId") { type = NavType.LongType })
        ) { backStackEntry ->
            val clubId = backStackEntry.arguments?.getLong("clubId")
            ClubDetailScreen(
                clubId = clubId ?: 0L,
                onBackClick = { navController.popBackStack() },
                onNavigateToBookDetail = { bookId ->
                    navController.navigate(Screen.BookDetail.createRoute(bookId))
                },
                onNavigateToSelectBook = { 
                    navController.navigate(Screen.SelectBook.route) 
                }
            )
        }
        
        composable(Screen.CreateClub.route) {
            CreateClubScreen(
                onBackClick = { navController.popBackStack() },
                onClubCreated = { clubId -> 
                    navController.navigate(Screen.ClubDetail.createRoute(clubId)) {
                        popUpTo(Screen.BookClubs.route)
                    }
                }
            )
        }
        
        composable(Screen.Forums.route) {
            ForumsScreen()
        }
        
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToEditProfile = { 
                    navController.navigate(Screen.EditProfile.route) 
                },
                onNavigateToClubDetail = { clubId -> 
                    navController.navigate(Screen.ClubDetail.createRoute(clubId))
                },
                onNavigateToBookDetail = { bookId ->
                    navController.navigate(Screen.BookDetail.createRoute(bookId))
                }
            )
        }
        
        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.AddBook.route) {
            AddBookScreen(
                onBackClick = { navController.popBackStack() },
                onSearchOnline = { navController.navigate(Screen.Search.route) }
            )
        }
        
        composable(Screen.Search.route) {
            OnlineBookSearchScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.SelectBook.route) {
            SelectBookScreen(
                onNavigateBack = { navController.popBackStack() },
                onBookSelected = { bookId ->
                    // When a book is selected, go back and update the club's current book
                    navController.popBackStack() 
                }
            )
        }
    }
}