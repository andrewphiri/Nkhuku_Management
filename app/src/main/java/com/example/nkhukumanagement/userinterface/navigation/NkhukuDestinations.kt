package com.example.nkhukumanagement.userinterface.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

interface NkhukuDestinations {
    val icon: ImageVector
    val route: String

    @get:StringRes
    val resourceId: Int
}





