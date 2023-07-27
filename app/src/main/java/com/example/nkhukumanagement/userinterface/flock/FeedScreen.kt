package com.example.nkhukumanagement.userinterface.flock

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.nkhukumanagement.R
import com.example.nkhukumanagement.userinterface.navigation.NkhukuDestinations

object FeedScreenDestination: NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.Inventory
    override val route: String
        get() = "Weight"
    override val resourceId: Int
        get() = R.string.feed
    const val flockIdArg = "id"
    val routeWithArgs = "$route/{$flockIdArg}"
    val arguments = listOf(navArgument(flockIdArg)  {
        defaultValue = 1
        type = NavType.IntType
    })
}