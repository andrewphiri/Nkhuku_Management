package com.example.nkhukumanagement.userinterface.flock

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Details
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.nkhukumanagement.FlockManagementTopAppBar
import com.example.nkhukumanagement.R
import com.example.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import kotlin.String

object FlockDetailsDestination : NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.Details
    override val route: String
        get() = "Details"
    override val resourceId: Int
        get() = R.string.details
    const val flockIdArg = "id"
    val routeWithArgs = "$route/{$flockIdArg}"
    val arguments = listOf(navArgument(flockIdArg) {
        type = NavType.IntType
    })
}

@Composable
fun FlockDetailsScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit
){
    Scaffold (
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(FlockDetailsDestination.resourceId),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
            ){ innerPadding ->
        Text(text = "Flock details screen",
        modifier = modifier.padding(innerPadding))
    }

}