package com.example.trabalho_final_mobile.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val destination: String,
    val type: String, // "Lazer" ou "Negócios"
    val startDate: String, // formato: dd/MM/yyyy
    val endDate: String,   // formato: dd/MM/yyyy
    val budget: Double,
    val totalSpent: Double = 0.0, // Total gasto na viagem (por enquanto sempre zero)
    val userId: Int
)