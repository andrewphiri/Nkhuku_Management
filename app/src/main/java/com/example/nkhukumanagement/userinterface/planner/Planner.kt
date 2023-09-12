package com.example.nkhukumanagement.userinterface.planner

data class Planner(
    val quantityToOrder: Int,
    val areFeedersAvailable: Boolean,
    val areDrinkersAvailable: Boolean,
    val starterNeeded: Double,
    val growerNeeded: Double,
    val finisherNeeded: Double,
    val totalStarterBags: Int,
    val totalGrowerBags: Int,
    val totalFinisherBags: Int,
    val totalFeed: Double,
    val totalBags: Int,
    val smallDrinkersNeeded: Int,
    val bigDrinkersNeeded: Int,
    val smallFeedersNeeded: Int,
    val bigFeedersNeeded: Int,
    val automaticDrinkers: Int,
    val nippleDrinkers: Int,
    val chicksTray: Int
)
