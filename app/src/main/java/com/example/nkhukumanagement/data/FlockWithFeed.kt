package com.example.nkhukumanagement.data

import androidx.room.Embedded
import androidx.room.Relation

/**
 * One to many relationship. Single Flock instance with zero or more instances of Feed
 */
data class FlockWithFeed(
    @Embedded val flock: Flock?,
    @Relation(
        parentColumn = "uniqueId",
        entityColumn = "flockUniqueId"
    )
    val feedList: List<Feed>?
)
