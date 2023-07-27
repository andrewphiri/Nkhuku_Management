package com.example.nkhukumanagement.userinterface.flock

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.nkhukumanagement.FlockManagementTopAppBar
import com.example.nkhukumanagement.R
import com.example.nkhukumanagement.userinterface.navigation.NkhukuDestinations

object WeightScreenDestination: NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.Scale
    override val route: String
        get() = "Weight"
    override val resourceId: Int
        get() = R.string.weight
    const val flockIdArg = "id"
    val routeWithArgs = "$route/{$flockIdArg}"
    val arguments = listOf(navArgument(flockIdArg)  {
        defaultValue = 1
        type = NavType.IntType
    })
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeightScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit = {},
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    weightViewModel: WeightViewModel = hiltViewModel(),
    flockEntryViewModel: FlockEntryViewModel
) {
    weightViewModel.setWeightList(flockEntryViewModel.flockUiState)
    Scaffold (
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(WeightScreenDestination.resourceId),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
            )
        }
            ) { innerPadding ->

        WeightInputList(
            modifier = modifier.padding(innerPadding),
            weightViewModel = weightViewModel,
            onItemChange = weightViewModel::updateWeightState
        )
    }
}

@Composable
fun WeightInputList(
    modifier: Modifier = Modifier,
    onItemChange: (Int, WeightUiState) -> Unit,
    weightViewModel: WeightViewModel,
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.End
        ) {
            Text(modifier = Modifier.weight(1f),
            text = "")
            Text(modifier = Modifier.weight(1.5f, fill = true),
                text = "ACTUAL",
            textAlign = TextAlign.Center)
            Text( modifier = Modifier.weight(1.5f, fill = true),
                text = "STANDARD",
            textAlign = TextAlign.Center)
        }
        LazyColumn {
            itemsIndexed(weightViewModel.getWeightList()) { index, weightItem ->
                WeightCard(weightUiState = weightItem,
                    onValueChanged = { weight ->
                        onItemChange(index, weight)
                    }
                )
            }
        }
    }

}

@Composable
fun WeightCard(modifier: Modifier = Modifier, weightUiState: WeightUiState, onValueChanged: (WeightUiState) -> Unit){
    Row(modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center) {
        Text(
            modifier = Modifier.weight(1f),
            text = weightUiState.week
        )

        TextField(
            modifier = Modifier.weight(1.5f),
            value = weightUiState.actualWeight,
            onValueChange = {
                onValueChanged(weightUiState.copy(actualWeight = it))
            },
            suffix = { Text( text = "grams") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Divider(
            modifier = Modifier.weight(0.01f).fillMaxHeight(),
            thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
        )

        TextField(
            modifier = Modifier.weight(1.5f),
            value = weightUiState.standard,
            onValueChange = {
                onValueChanged(weightUiState.copy(standard = it))
            },
            suffix = { Text("grams") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}