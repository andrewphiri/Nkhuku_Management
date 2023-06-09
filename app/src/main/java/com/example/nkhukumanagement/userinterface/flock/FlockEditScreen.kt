package com.example.nkhukumanagement.userinterface.flock

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.example.nkhukumanagement.FlockManagementTopAppBar
import com.example.nkhukumanagement.R
import com.example.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import kotlin.String

object EditFlockDestination : NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.Edit
    override val route: String
        get() = "edit flock"
    override val resourceId: Int
        get() = R.string.edit_flock
}

@Composable
fun FlockEditScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit
    ){
        Scaffold (
            topBar = {
                FlockManagementTopAppBar(
                    title = stringResource(EditFlockDestination.resourceId),
                    canNavigateBack = canNavigateBack,
                    navigateUp = onNavigateUp
                )
            }
        ){ innerPadding ->
            Text(text = "Edit flock",
                modifier = modifier.padding(innerPadding))
        }

    }