package com.raj.cricketmatch.presentation.teamSelectScreen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.gson.Gson
import com.raj.cricketmatch.data.Team
import com.raj.cricketmatch.presentation.teamSelectScreen.viewmodel.TeamSelectViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamSelectScreen(viewModel: TeamSelectViewModel, onCLick: (Team, Team) -> Unit) {
    val teams by viewModel.teams.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState(initial = true)
    val errorMessage by viewModel.errorMessage.collectAsState(initial = "")
    val selectedTeams = remember { mutableStateListOf<Team>() }
    val startMatchEnabled = remember { mutableStateOf(false) }

    // Update start match button state
    LaunchedEffect(selectedTeams.size) {
        startMatchEnabled.value = selectedTeams.size == 2
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Select Teams") }) },
        bottomBar = {
            Button(
                onClick = {
                    if (selectedTeams.size == 2) {
                        onCLick.invoke(selectedTeams[0], selectedTeams[1])
                    }
                },
                enabled = startMatchEnabled.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Start Match")
            }
        }
    ) { paddingValues ->

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Loading...")
            }
        } else if (errorMessage?.isNotEmpty() == true) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: $errorMessage")
            }
        } else {
            LazyColumn(contentPadding = paddingValues) {
                items(teams) { team ->
                    TeamRow(
                        team = team,
                        isSelected = selectedTeams.contains(team),
                        onTeamSelected = { isSelected ->
                            if (isSelected) {
                                if (selectedTeams.size < 2) {
                                    selectedTeams.add(team)
                                }
                            } else {
                                selectedTeams.remove(team)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TeamRow(
    team: Team,
    isSelected: Boolean,
    onTeamSelected: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTeamSelected(!isSelected) }
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(if (isSelected) Color(0xFFE0E0E0) else Color.Transparent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val painter = rememberAsyncImagePainter(
            model = team.flag,
            contentScale = ContentScale.Crop
        )
        Image(
            painter = painter,
            contentDescription = "Team Flag",
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = team.name, fontSize = 16.sp)
        Spacer(modifier = Modifier.weight(1f))
        Checkbox(
            checked = isSelected,
            onCheckedChange = { isChecked ->
                onTeamSelected(isChecked)
            }
        )
    }
}