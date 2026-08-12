package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Vocabulary : Screen("vocabulary", "Vocabulary", Icons.AutoMirrored.Filled.MenuBook)
    object Categories : Screen("categories", "Categories", Icons.Default.Category)
    object Favorites : Screen("favorites", "Favorites", Icons.Default.Star)
    object Test : Screen("test", "Quiz Test", Icons.Default.Quiz)
    object WrongWords : Screen("wrong_words", "Wrong Words", Icons.Default.Warning)
    object Statistics : Screen("statistics", "Statistics", Icons.Default.BarChart)
    object GoogleLogin : Screen("google_login", "Google Account", Icons.Default.Dashboard)
}

val mainNavigationItems = listOf(
    Screen.Dashboard,
    Screen.Vocabulary,
    Screen.Categories,
    Screen.Favorites,
    Screen.Test,
    Screen.WrongWords,
    Screen.Statistics
)
