package com.example.nkhukumanagement.userinterface.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.nkhukumanagement.R

interface NkhukuDestinations {
    val icon: ImageVector
    val route: String
    @get:StringRes
    val resourceId: Int
}





