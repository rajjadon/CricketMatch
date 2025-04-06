package com.raj.cricketmatch

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.raj.cricketmatch.data.Team
import com.raj.cricketmatch.presentation.matchScreen.MatchScreen
import com.raj.cricketmatch.presentation.navigation.NavigationScreens
import com.raj.cricketmatch.presentation.teamSelectScreen.TeamSelectScreen
import com.raj.cricketmatch.ui.theme.CricketMatchTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CricketMatchTheme() {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    NavigationGraph()
                }
            }
        }
    }

    @Composable
    private fun NavigationGraph() {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = NavigationScreens.TeamSelectScreen.route) {
            composable(NavigationScreens.TeamSelectScreen.route) {
                // Use viewModel() to get or create the ViewModel associated with this composable
                TeamSelectScreen(
                    onCLick = { team1, team2 ->
                        navController.navigate(
                            NavigationScreens.MatchScreen.withArgs(
                                team1.name,
                                team2.name
                            )
                        )
                    }
                )
            }
            composable(route = NavigationScreens.MatchScreen.route,
                arguments = listOf(
                    androidx.navigation.navArgument("team1Name") {},
                    androidx.navigation.navArgument("team2Name") {}
                )) { entry ->
                val team1Name = entry.arguments?.getString("team1Name")
                val team2Name = entry.arguments?.getString("team2Name")
                if (team1Name != null && team2Name != null) {
                    MatchScreen(team1 = Team(name = team1Name), team2 = Team(name = team2Name))
                }
                else{
                    navController.navigateUp()
                }
                    }
            }
        }
    }


