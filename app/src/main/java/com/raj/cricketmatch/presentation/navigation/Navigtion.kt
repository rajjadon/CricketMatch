package com.raj.cricketmatch.presentation.navigation

sealed class NavigationScreens(val route: String) {
    object TeamSelectScreen : NavigationScreens("team_select")
    object MatchScreen : NavigationScreens("match/{team1Name}/{team2Name}") {
        fun withArgs(team1Name: String, team2Name: String): String {
            return "match/$team1Name/$team1Name"
        }
    }
}