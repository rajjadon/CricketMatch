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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raj.cricketmatch.data.Team

// Main Match class with complete implementation
class Match(team1: Team, team2: Team) {
    val team1 = team1
    val team2 = team2

    // Match state
    var currentInnings by mutableStateOf(1)
    var isMatchFinished by mutableStateOf(false)
    var lastOutcome by mutableStateOf("")

    // Team roles
    var battingTeam by mutableStateOf(team1)
    var bowlingTeam by mutableStateOf(team2)

    // Score tracking
    var team1Score by mutableStateOf(0)
    var team2Score by mutableStateOf(0)
    var team1Wickets by mutableStateOf(0)
    var team2Wickets by mutableStateOf(0)

    // Ball tracking
    private var ballsBowled by mutableStateOf(0)
    val overs: String get() = "${ballsBowled / 6}.${ballsBowled % 6}"

    // Match constants
    private val maxOvers = 2
    private val maxWickets = 3
    private val maxBalls = maxOvers * 6

    fun playNextBall(): String {
        if (isMatchFinished) return "Match Over"

        val outcome = generateBallOutcome()
        lastOutcome = outcome

        when (outcome) {
            "Wide", "No Ball" -> handleExtra(outcome)
            "Out" -> handleWicket()
            else -> handleRuns(outcome.toInt())
        }

        checkMatchStatus()
        return outcome
    }

    private fun generateBallOutcome(): String {
        val outcomes = listOf("0", "1", "2", "3", "4", "6", "Out", "Wide", "No Ball")
        val weights = listOf(35, 30, 15, 5, 8, 2, 5, 3, 3)
        return outcomes.zip(weights)
            .flatMap { (outcome, weight) -> List(weight) { outcome } }
            .random()
    }

    private fun handleExtra(type: String) {
        addRuns(1)
        if (type == "No Ball") {
            lastOutcome += " (Free Hit)"
        }
    }

    private fun handleRuns(runs: Int) {
        addRuns(runs)
        incrementBalls()
    }

    private fun handleWicket() {
        if (currentInnings == 1) {
            team1Wickets++
            if (team1Wickets >= maxWickets) endInnings()
        } else {
            team2Wickets++
            if (team2Wickets >= maxWickets) endInnings()
        }
        incrementBalls()
    }

    private fun addRuns(runs: Int) {
        if (currentInnings == 1) team1Score += runs else team2Score += runs
    }

    private fun incrementBalls() {
        ballsBowled++
    }

    private fun checkMatchStatus() {
        when {
            // Team 2 chased successfully
            currentInnings == 2 && team2Score > team1Score -> endMatch()

            // All overs completed
            ballsBowled >= maxBalls -> if (currentInnings == 1) switchInnings() else endMatch()

            // All wickets fallen
            (currentInnings == 1 && team1Wickets >= maxWickets) -> switchInnings()
            (currentInnings == 2 && team2Wickets >= maxWickets) -> endMatch()
        }
    }

    private fun switchInnings() {
        currentInnings = 2
        ballsBowled = 0
        // Swap batting and bowling teams
        battingTeam = team2.also { bowlingTeam = team1 }
    }

    private fun endInnings() {
        if (currentInnings == 1) switchInnings() else endMatch()
    }

    private fun endMatch() {
        isMatchFinished = true
    }

    fun getResult(): String {
        return when {
            team1Score > team2Score -> "${team1.name} wins by ${team1Score - team2Score} runs!"
            team2Score > team1Score -> "${team2.name} wins by ${maxWickets - team2Wickets} wickets!"
            else -> "Match tied!"
        }
    }
}

// Composable UI for Match Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchScreen(team1: Team, team2: Team) {
    val match = remember { Match(team1, team2) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Match Center") }) },
        bottomBar = {
            Button(
                onClick = { match.playNextBall() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = !match.isMatchFinished,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text(
                    if (match.isMatchFinished) "Match Over" else "Play Next Ball",
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
            // Team 1 Display
            TeamDisplay(
                team = match.team1,
                score = match.team1Score,
                wickets = match.team1Wickets,
                overs = if (match.currentInnings == 1) match.overs else "2.0",
                isBatting = match.battingTeam == match.team1,
                hasBatted = match.currentInnings > 1
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Team 2 Display
            TeamDisplay(
                team = match.team2,
                score = match.team2Score,
                wickets = match.team2Wickets,
                overs = if (match.currentInnings == 2) match.overs else if (match.currentInnings > 1) "2.0" else null,
                isBatting = match.battingTeam == match.team2,
                hasBatted = match.currentInnings > 1
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Outcome Display
            OutcomeCard(match.lastOutcome)

            Spacer(modifier = Modifier.height(24.dp))

            // Match Result
            if (match.isMatchFinished) {
                MatchResult(match.getResult())
            }
        }
    }
}

@Composable
fun TeamDisplay(
    team: Team,
    score: Int,
    wickets: Int,
    overs: String?,
    isBatting: Boolean,
    hasBatted: Boolean
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = team.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = when {
                        isBatting -> "Batting"
                        hasBatted -> "Bowled"
                        else -> "Bowling"
                    },
                    color = when {
                        isBatting -> Color(0xFF388E3C)
                        hasBatted -> Color.Gray
                        else -> Color(0xFFD32F2F)
                    },
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (hasBatted || score > 0) "$score/$wickets" else "Yet to bat",
                    fontSize = 16.sp
                )
                Text(
                    text = overs ?: "Yet to bowl",
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun OutcomeCard(outcome: String) {
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
            Text(
                text = outcome.ifEmpty { "Waiting..." },
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MatchResult(result: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
    ) {
        Text(
            text = result,
            modifier = Modifier.padding(16.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1976D2),
            textAlign = TextAlign.Center
        )
    }
}