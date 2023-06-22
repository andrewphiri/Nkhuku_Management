package com.example.nkhukumanagement.data

import java.time.LocalDate

data class Feed(
    val id: Int,
    val uniqueID:String,
    val name: String,
    val type: String,
    val consumed: Double,
    val feedingDate: LocalDate
)
