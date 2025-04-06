package com.raj.cricketmatch.presentation.teamSelectScreen.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.raj.cricketmatch.data.Team
import com.raj.cricketmatch.extension.fromJson
import com.raj.cricketmatch.extension.getJsonFromAssets
import com.raj.cricketmatch.extension.toStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class TeamSelectViewModel @Inject constructor(@ApplicationContext private val context: Context) : ViewModel() {

    private val _teams = MutableStateFlow<List<Team>>(emptyList())
    val teams = _teams.toStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading= _isLoading.toStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage= _errorMessage.toStateFlow()

    init {
        loadTeams() // Load the teams when the ViewModel is created
    }

    fun loadTeams() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true // Indicate loading is in progress
            try {
                val jsonString = getJsonFromAssets(context, "team.json")
                if (jsonString != null) {
                    val teamList = Gson().fromJson<List<Team>>(jsonString)
                    _teams.value = teamList
                    _isLoading.value = false // Loading complete
                } else {
                    _errorMessage.value = "Failed to load teams data."
                    _isLoading.value = false
                }
            } catch (e: IOException) {
                _errorMessage.value = "Error reading teams file."
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error parsing teams data."
                _isLoading.value = false
            }
        }
    }
}