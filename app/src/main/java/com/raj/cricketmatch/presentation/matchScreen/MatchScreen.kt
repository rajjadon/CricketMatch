package com.raj.cricketmatch.presentation.matchScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raj.cricketmatch.data.Team

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchScreen(team1Name: Team, team2Name: Team) {
    val match = remember { Match(team1Name, team2Name) }
    var outcome by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Match Center") }) },
        bottomBar = {
            Button(
                onClick = {
                    outcome = match.playBall()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = !match.isMatchOver(),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Text(
                    if (match.isMatchOver()) "Match Over" else "Play Next Ball",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TeamDisplay(
                teamName = match.team1.name,
                score = match.team1Score,
                wickets = match.team1Wickets,
                overs = if (match.battingTeam == match.team1.name) match.oversCompleted else null,
                isBatting = match.battingTeam == match.team1.name
            )
            Spacer(modifier = Modifier.height(8.dp))
            TeamDisplay(
                teamName = match.team2.name,
                score = match.team2Score,
                wickets = match.team2Wickets,
                overs = if (match.bowlingTeam == match.team2.name) match.oversCompleted else null,
                isBatting = match.battingTeam == match.team2.name,
                hasBatted = match.innings > 1 || match.battingTeam == match.team2.name
            )
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFE0E0E0))
                ) {
                    Text(text = outcome, fontSize = 48.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            if (match.isMatchOver()) {
                Text(
                    text = match.getMatchResult(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
fun TeamDisplay(
    teamName: String,
    score: Int,
    wickets: Int,
    overs: Double? = null,
    isBatting: Boolean = false,
    hasBatted: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = teamName + (if (isBatting) " (Batting)" else " (Bowling)"),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (hasBatted || score > 0 || wickets > 0) "Score: $score/$wickets" else "yet to bat",
                fontSize = 16.sp
            )
            if (overs != null || hasBatted) {
                Text(
                    text = if (hasBatted) "Overs: ${"%.1f".format(overs ?: 0.0)}" else "yet to score",
                    fontSize = 16.sp
                )
            } else {
                Text(text = "yet to score", fontSize = 16.sp)
            }
        }
    }
}

class Match(team1: Team, team2: Team) {
    val team1 = team1
    val team2 = team2
    var battingTeam by mutableStateOf(team1.name)
        private set
    var bowlingTeam by mutableStateOf(team2.name)
        private set
    var team1Score by mutableStateOf(0)
        private set
    var team2Score by mutableStateOf(0)
        private set
    var team1Wickets by mutableStateOf(0)
        private set
    var team2Wickets by mutableStateOf(0)
        private set
    var oversCompleted by mutableStateOf(0.0)
        private set
    var innings by mutableStateOf(1)
        private set

    fun playBall(): String {
        val outcome = generateBallOutcome()

        when (outcome) {
            "0", "1", "2", "3", "4", "6" -> {
                val runs = outcome.toInt()
                if (battingTeam == team1.name) {
                    team1Score += runs
                } else {
                    team2Score += runs
                }
                incrementOvers()
                return outcome
            }

            "Out" -> {
                if (battingTeam == team1.name) {
                    team1Wickets++
                } else {
                    team2Wickets++
                }
                incrementOvers()
                return outcome
            }

            "Wide", "No Ball" -> {
                if (battingTeam == team1.name) {
                    team1Score += 1
                } else {
                    team2Score += 1
                }
                return outcome
            }

            else -> return "Invalid Outcome"
        }
    }

    private fun generateBallOutcome(): String {
        val outcomes = listOf("0", "1", "2", "3", "4", "6", "Out", "Wide", "No Ball")
        val weights = listOf(30, 30, 10, 5, 10, 5, 10, 5, 5)

        val weightedOutcomes = mutableListOf<String>()
        for (i in outcomes.indices) {
            repeat(weights[i]) {
                weightedOutcomes.add(outcomes[i])
            }
        }

        return weightedOutcomes.random()
    }

    private fun incrementOvers() {
        oversCompleted += 0.1
        if (oversCompleted >= 2.0) { // Keep it low for preview
            oversCompleted = 0.0
            changeInnings()
        }
    }

    private fun changeInnings() {
        innings = 2
        val tempTeam = battingTeam
        battingTeam = bowlingTeam
        bowlingTeam = tempTeam
    }

    fun isMatchOver(): Boolean {
        if (innings > 2) {
            return true
        }

        if (battingTeam == team2.name && team2Score > team1Score) {
            return true
        }

        return (innings > 2) ||
                (team1Wickets >= 3 && innings == 1) ||
                (team2Wickets >= 3 && innings == 2)
    }

    fun getMatchResult(): String {
        if (!isMatchOver()) {
            return "Match in progress"
        }

        return if (team1Score > team2Score) {
            "${team1.name} Wins!"
        } else if (team2Score > team1Score) {
            "${team2.name} Wins!"
        } else {
            "Match Tied"
        }
    }
}