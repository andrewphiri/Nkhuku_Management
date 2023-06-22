package com.example.nkhukumanagement.data

import java.time.LocalDate

data class Weight(
    val id: Int,
    val uniqueID: String,
    val week: String,
    val weight: Double,
    val measuredDate: LocalDate
)
