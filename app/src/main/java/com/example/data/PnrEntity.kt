package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pnr_searches")
data class PnrEntity(
    @PrimaryKey
    val pnr: String,
    val trainNumber: String,
    val trainName: String,
    val dateOfJourney: String,
    val sourceStation: String,
    val destinationStation: String,
    val bookingClass: String,
    val currentStatus: String,
    val initialStatus: String,
    val confirmationProbability: Int, // e.g. 15 for 15%
    val riskLevel: String, // "HIGH", "MEDIUM", "SAFE"
    val refundEstimate: Int,
    val racProbability: Int,
    val chartPreparedTime: String,
    val countdownMinutes: Long, // minutes until chart preparation
    val timestamp: Long = System.currentTimeMillis()
)
