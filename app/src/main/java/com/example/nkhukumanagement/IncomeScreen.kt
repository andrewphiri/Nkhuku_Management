package com.example.nkhukumanagement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.nkhukumanagement.ui.theme.Shapes
import com.example.nkhukumanagement.userinterface.navigation.NkhukuDestinations

object IncomeScreenDestination: NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.Inventory
    override val route: String
        get() = "income"
    override val resourceId: Int
        get() = R.string.income
    const val flockIdArg = "id"
    val routeWithArgs = "$route/{$flockIdArg}"
    val arguments = listOf(navArgument(flockIdArg) {
        defaultValue = 1
        type = NavType.IntType
    })
}
@Composable
fun IncomeScreen() {
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                shape = Shapes.large,
                onClick = {},
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null)},
                text = { Text("Income")} )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally)
        { Text("Income") }
    }
}