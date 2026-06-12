package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.PnrEntity
import com.example.data.PnrRepository
import com.example.data.api.GeminiClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

enum class ActiveScreen {
    Splash,
    Login,
    Tabs
}

enum class Tab {
    Home,
    CheckPnr,
    Alternatives,
    AIAssistant,
    BookTicket,
    Profile
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PnrRepository

    // Navigation and states
    val currentScreen = MutableStateFlow(ActiveScreen.Splash)
    val currentTab = MutableStateFlow(Tab.Home)
    val isDarkTheme = MutableStateFlow(true)

    // Logged-in user details
    val loggedInName = MutableStateFlow("Anjali Sharma")
    val loggedInEmail = MutableStateFlow("anjali18102005@gmail.com")

    // PNR state
    val searchHistory: StateFlow<List<PnrEntity>>
    val selectedPnr = MutableStateFlow<PnrEntity?>(null)
    val isPnrLoading = MutableStateFlow(false)

    // AI Assistant state
    val chatMessages = MutableStateFlow<List<ChatMessage>>(listOf(
        ChatMessage("init", "Welcome to ResQRail AI Assistant! I am actively monitoring railway tracks and seats. How can I help you navigate your waitlisted journey today?", false)
    ))
    val isAiTyping = MutableStateFlow(false)

    init {
        val database = AppDatabase.getDatabase(application)
        repository = PnrRepository(database.pnrDao())
        
        searchHistory = repository.allSearches.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Splash transition timing
        viewModelScope.launch {
            delay(2800) // 2.8 seconds splash duration
            currentScreen.value = ActiveScreen.Login
        }

        // Ticking countdown updates
        viewModelScope.launch {
            while (true) {
                delay(60000) // clock minute tick
                selectedPnr.value?.let { pnr ->
                    if (pnr.countdownMinutes > 1) {
                        val updated = pnr.copy(countdownMinutes = pnr.countdownMinutes - 1)
                        selectedPnr.value = updated
                        repository.insertPnr(updated)
                    }
                }
            }
        }
    }

    fun selectTab(tab: Tab) {
        currentTab.value = tab
    }

    fun selectPnr(pnr: PnrEntity) {
        selectedPnr.value = pnr
        currentTab.value = Tab.CheckPnr
    }

    fun addConfirmedBooking(pnr: PnrEntity) {
        viewModelScope.launch {
            repository.insertPnr(pnr)
            selectedPnr.value = pnr
        }
    }

    fun toggleDarkTheme() {
        isDarkTheme.value = !isDarkTheme.value
    }

    fun checkPnr(pnrNumber: String) {
        if (pnrNumber.trim().length < 5) return

        viewModelScope.launch {
            isPnrLoading.value = true
            delay(1500) // realistic mock visual delay

            val digits = pnrNumber.filter { it.isDigit() }
            val lastDigit = if (digits.isNotEmpty()) digits.last().digitToInt() else 5
            
            val probability = when (lastDigit) {
                0, 1, 2, 3 -> 15 // High Risk - Red
                4, 5, 6 -> 48    // Medium Risk - Amber
                else -> 82       // Safe - Green
            }
            
            val riskLevel = when {
                probability < 30 -> "HIGH"
                probability < 70 -> "MEDIUM"
                else -> "SAFE"
            }

            val trains = listOf(
                Pair("12124", "Deccan Queen Express"),
                Pair("12951", "Mumbai Rajdhani Express"),
                Pair("12002", "NDLS Shatabdi Express"),
                Pair("22691", "Hazrat Nizamuddin Rajdhani"),
                Pair("12115", "Siddheshwar Express"),
                Pair("12223", "LTT Duronto Express")
            )
            val selectedTrain = trains[lastDigit % trains.size]

            val stations = listOf(
                Pair("PUNE", "CSMT"),
                Pair("NDLS", "BCT"),
                Pair("MAS", "SBC"),
                Pair("HWH", "NDLS"),
                Pair("BSB", "LTT")
            )
            val selectedStations = stations[lastDigit % stations.size]

            val pnrEntity = PnrEntity(
                pnr = pnrNumber,
                trainNumber = selectedTrain.first,
                trainName = selectedTrain.second,
                dateOfJourney = "15th June 2026",
                sourceStation = selectedStations.first,
                destinationStation = selectedStations.second,
                bookingClass = "3A Economy",
                currentStatus = "WL - " + (lastDigit * 4 + 3),
                initialStatus = "WL - " + (lastDigit * 6 + 18),
                confirmationProbability = probability,
                riskLevel = riskLevel,
                refundEstimate = 320 + (lastDigit * 40),
                racProbability = if (probability in 30..70) 65 else if (probability > 70) 92 else 12,
                chartPreparedTime = "06:00 PM",
                countdownMinutes = (180 + lastDigit * 75).toLong()
            )

            repository.insertPnr(pnrEntity)
            selectedPnr.value = pnrEntity
            isPnrLoading.value = false

            // Transition directly to the PNR Checker Details
            currentTab.value = Tab.CheckPnr

            // Alert the AI Travel Agent in Chat
            viewModelScope.launch {
                isAiTyping.value = true
                delay(1000)
                val promptMsg = "Analyse PNR ${pnrEntity.pnr} for train ${pnrEntity.trainName}. Status is ${pnrEntity.currentStatus}, confirmation probability is ${pnrEntity.confirmationProbability}%. Provide a brief premium alert message (max 2-3 sentences) detailing this. Highlight transit alternatives if high risk."
                val response = GeminiClient.getChatResponse(promptMsg, "You are ResQRail's Smart Travel Agent. Keep it highly professional, supportive, and informative.")
                addSystemChatMessage(response)
                isAiTyping.value = false
            }
        }
    }

    fun deletePnr(pnr: PnrEntity) {
        viewModelScope.launch {
            if (selectedPnr.value?.pnr == pnr.pnr) {
                selectedPnr.value = null
            }
            repository.deletePnr(pnr)
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            selectedPnr.value = null
            repository.clearHistory()
        }
    }

    fun sendUserChatMessage(messageText: String) {
        if (messageText.trim().isEmpty()) return
        val userMsg = ChatMessage(text = messageText, isUser = true)
        chatMessages.update { it + userMsg }

        viewModelScope.launch {
            isAiTyping.value = true

            val selected = selectedPnr.value
            val contextPrefix = if (selected != null) {
                "[Monitored Ticket Context: Train ${selected.trainName} (${selected.trainNumber}), Status ${selected.currentStatus}, Risk: ${selected.riskLevel}, Confirmation Probability: ${selected.confirmationProbability}%]\n"
            } else {
                "[Context: No active PNR monitored]\n"
            }

            val fullPrompt = contextPrefix + messageText
            val systemIns = "You are ResQRail AI Assistant. An expert in Indian Railways, PNR indicators, seat allocation, refund logistics, and multimodal travel options (connecting buses and air transfer flights). Keep your response professional, precise, conversational and to-the-point (2 to 3 sentences max). Suggest checked routes and travel alternatives if appropriate."

            val botResponse = GeminiClient.getChatResponse(fullPrompt, systemIns)
            val botMsg = ChatMessage(text = botResponse, isUser = false)
            chatMessages.update { it + botMsg }
            isAiTyping.value = false
        }
    }

    private fun addSystemChatMessage(text: String) {
        chatMessages.update { it + ChatMessage(text = text, isUser = false) }
    }
}
