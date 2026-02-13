package com.example.cauds.data.model

import com.google.firebase.Timestamp

data class LogData(
    val userId: String = "",            
    val timestamp: Timestamp? = null,  
    val date: String = "",           
    val updatedAt: Long = 0L,

    val drinkType: String = "",
    val drinkAmount: Long = 0,
    val drinkCount: Long = 0,
    val drinkCost: Long = 0,
    val alcoholByVolume: Long = 0,
)

data class LogItem(
    val id: String,
    val data: LogData
)
