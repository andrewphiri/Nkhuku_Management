package com.example.nkhukumanagement.data

import androidx.room.Embedded
import androidx.room.Relation

data class FlockWithFeed(
    @Embedded val flock: Flock?,
    @Relation(
        parentColumn = "uniqueId",
        entityColumn = "flockUniqueId"
    )
    val feedList: List<Feed>?
)
