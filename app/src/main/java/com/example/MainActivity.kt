package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.MainViewModel
import com.example.ui.components.GoogleLoginDialog
import com.example.ui.navigation.Screen
import com.example.ui.navigation.mainNavigationItems
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.StatisticsScreen
import com.example.ui.screens.TestScreen
import com.example.ui.screens.VocabularyScreen
import com.example.ui.screens.WrongWordsScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

  private val viewModel: MainViewModel by viewModels()

  @OptIn(ExperimentalMaterial3Api::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      MyApplicationTheme {
        val navController = rememberNavController()
        val userProfile by viewModel.userProfile.collectAsState()
        var showGoogleLoginDialog by remember { mutableStateOf(false) }

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Dashboard.route

        val activeScreen = mainNavigationItems.find { it.route == currentRoute } ?: Screen.Dashboard

        if (showGoogleLoginDialog) {
          GoogleLoginDialog(
            userProfile = userProfile,
            onDismiss = { showGoogleLoginDialog = false },
            onSignIn = { email, name ->
              viewModel.googleSignIn(email, name)
              showGoogleLoginDialog = false
            },
            onSignOut = {
              viewModel.googleSignOut()
              showGoogleLoginDialog = false
            }
          )
        }

        Scaffold(
          topBar = {
            TopAppBar(
              title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = activeScreen.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                      fontWeight = FontWeight.Bold,
                      fontSize = 20.sp
                    )
                  )
                }
              },
              actions = {
                // Google Account Header Icon with Cloud Sync Indicator
                Surface(
                  shape = CircleShape,
                  color = MaterialTheme.colorScheme.primaryContainer,
                  modifier = Modifier
                    .padding(end = 12.dp)
                    .clickable { showGoogleLoginDialog = true }
                    .testTag("top_bar_profile_button")
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Box(contentAlignment = Alignment.TopEnd) {
                      Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Google Profile",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                      )
                      if (userProfile.isLoggedIn) {
                        Box(
                          modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                        )
                      }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                      text = if (userProfile.isLoggedIn) "Synced" else "Login",
                      style = MaterialTheme.typography.labelMedium,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                  }
                }
              },
              colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
              )
            )
          },
          bottomBar = {
            NavigationBar(
              containerColor = MaterialTheme.colorScheme.surface,
              tonalElevation = 8.dp
            ) {
              mainNavigationItems.forEach { screen ->
                val isSelected = currentRoute == screen.route
                NavigationBarItem(
                  selected = isSelected,
                  onClick = {
                    if (currentRoute != screen.route) {
                      navController.navigate(screen.route) {
                        popUpTo(Screen.Dashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                      }
                    }
                  },
                  icon = {
                    Icon(
                      imageVector = screen.icon,
                      contentDescription = screen.title
                    )
                  },
                  label = {
                    Text(
                      text = screen.title,
                      fontSize = 11.sp,
                      maxLines = 1
                    )
                  },
                  modifier = Modifier.testTag("nav_item_${screen.route}")
                )
              }
            }
          },
          modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
          NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
          ) {
            composable(Screen.Dashboard.route) {
              DashboardScreen(
                viewModel = viewModel,
                onNavigateToVocab = { navController.navigate(Screen.Vocabulary.route) },
                onNavigateToQuiz = { navController.navigate(Screen.Test.route) },
                onNavigateToWrongWords = { navController.navigate(Screen.WrongWords.route) },
                onOpenGoogleLogin = { showGoogleLoginDialog = true }
              )
            }
            composable(Screen.Vocabulary.route) {
              VocabularyScreen(viewModel = viewModel)
            }
            composable(Screen.Categories.route) {
              CategoriesScreen(viewModel = viewModel)
            }
            composable(Screen.Favorites.route) {
              FavoritesScreen(viewModel = viewModel)
            }
            composable(Screen.Test.route) {
              TestScreen(
                viewModel = viewModel,
                onNavigateToWrongWords = { navController.navigate(Screen.WrongWords.route) }
              )
            }
            composable(Screen.WrongWords.route) {
              WrongWordsScreen(
                viewModel = viewModel,
                onStartWrongWordsQuiz = { navController.navigate(Screen.Test.route) }
              )
            }
            composable(Screen.Statistics.route) {
              StatisticsScreen(viewModel = viewModel)
            }
          }
        }
      }
    }
  }
}
