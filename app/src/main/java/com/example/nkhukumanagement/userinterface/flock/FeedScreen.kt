package com.example.nkhukumanagement.userinterface.flock

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.nkhukumanagement.FeedUiState
import com.example.nkhukumanagement.FlockManagementTopAppBar
import com.example.nkhukumanagement.R
import com.example.nkhukumanagement.data.Feed
import com.example.nkhukumanagement.toFeedUiState
import com.example.nkhukumanagement.ui.theme.NkhukuManagementTheme
import com.example.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import java.time.LocalDate

object FeedScreenDestination: NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.Inventory
    override val route: String
        get() = "Feed"
    override val resourceId: Int
        get() = R.string.feed
    const val flockIdArg = "id"
    val routeWithArgs = "$route/{$flockIdArg}"
    val arguments = listOf(navArgument(flockIdArg)  {
        defaultValue = 1
        type = NavType.IntType
    })
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    feedViewModel: FeedViewModel = hiltViewModel(),
    flockEntryViewModel: FlockEntryViewModel
) {
    val flockWithFeed by feedViewModel.flockWithFeed.collectAsState()
    val feedList: List<Feed> = flockWithFeed.feedList ?: listOf()
    val feedUiStateList: MutableList<FeedUiState> = mutableListOf()
    var isFABVisible by remember { mutableStateOf(false) }

    for (feedUiState in feedList) {
        feedUiStateList.add(feedUiState.toFeedUiState())
    }
    feedViewModel.setFeedList(feedUiStateList.toMutableStateList())

Scaffold (
    topBar = {
        FlockManagementTopAppBar(
            title = stringResource(FeedScreenDestination.resourceId),
            canNavigateBack= canNavigateBack,
            navigateUp = onNavigateUp
        )
    },
    floatingActionButton = {
        AnimatedVisibility(visible = isFABVisible,
            enter = slideIn(tween(200, easing = LinearOutSlowInEasing),
                initialOffset = {
                    IntOffset(180, 90)
                }),
            exit = slideOut(tween(200, easing = FastOutSlowInEasing)) {
                IntOffset(180, 90)
            }) {
            FloatingActionButton(onClick = {
                isFABVisible = false
            }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Feed"
                )
            }
        }
    }
){ innerPadding ->
    FeedConsumptionList(
        modifier = modifier.padding(innerPadding),
        feedViewModel = feedViewModel,
        onItemChange = feedViewModel::updateFeedState
    )
}
}

@Composable
fun FeedConsumptionList(modifier: Modifier = Modifier,
                        feedViewModel: FeedViewModel,
                        onItemChange: (Int, FeedUiState) -> Unit) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Text(modifier = Modifier.weight(0.5f),
                text = "")
            Text(modifier = Modifier.weight(1.41f, fill = true).padding(4.dp),
                text = "CONSUMPTION PER BIRD",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleSmall)

            Divider(modifier = Modifier.weight(0.02f).fillMaxHeight(),
                thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary)

            Text(modifier = Modifier.weight(1.51f, fill = true).padding(4.dp),
            text = "TOTAL FEED CONSUMED",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleSmall)
        }

        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Text(modifier = Modifier.weight(0.5f),
                text = "")
            Text(modifier = Modifier.weight(0.70f, fill = true),
                text = "ACTUAL",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center)
            Text(modifier = Modifier.weight(0.71f, fill = true),
                text = "STANDARD",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center)

            Divider(modifier = Modifier.weight(0.02f).fillMaxHeight(),
                thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary)

            Text(modifier = Modifier.weight(0.755f, fill = true),
                text = "ACTUAL",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center)
            Text(modifier = Modifier.weight(0.755f, fill = true),
                text = "STANDARD",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center)
        }

        LazyColumn {
            itemsIndexed(feedViewModel.getFeedList()) { index, feedItem ->
                FeedCardItem(
                    feedUiState = feedItem,
                    onChangedValue = {
                        onItemChange(index, it)
                    }
                )
            }
        }
    }
}

@Composable
fun FeedCardItem(modifier: Modifier = Modifier,
             feedUiState: FeedUiState,
             onChangedValue: (FeedUiState) -> Unit = {}) {

    Row(modifier = modifier.height(IntrinsicSize.Max).padding(4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {

        Text(
            modifier = Modifier.weight(0.5f),
            text = feedUiState.week,
            style = MaterialTheme.typography.bodyMedium)

        TextField(
            modifier = Modifier.weight(0.7f),
            value = feedUiState.actualConsumptionPerBird,
            onValueChange = {
                onChangedValue(feedUiState.copy(actualConsumptionPerBird = it))
            },
            suffix = { Text("Kg",
                color = LocalContentColor.current.copy(1f),
                style = MaterialTheme.typography.labelSmall) },
            enabled = false,
            colors = TextFieldDefaults.colors(
                disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = LocalContentColor.current.alpha),
                disabledTextColor = LocalContentColor.current.copy(LocalContentColor.current.alpha)),
            textStyle = MaterialTheme.typography.labelSmall,
            minLines = 2
        )

        Divider(
            modifier = Modifier.weight(0.01f).fillMaxHeight(),
            thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
        )

        TextField(
            modifier = Modifier.weight(0.7f),
            value = feedUiState.standardConsumptionPerBird,
            onValueChange = {
                onChangedValue(feedUiState.copy(standardConsumptionPerBird = it))
            },
            suffix = { Text("Kg",
                color = LocalContentColor.current.copy(1f),
                style = MaterialTheme.typography.labelSmall) },
            enabled = false,
            colors = TextFieldDefaults.colors(
                disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = LocalContentColor.current.alpha),
                disabledTextColor = LocalContentColor.current.copy(LocalContentColor.current.alpha)),
            textStyle = MaterialTheme.typography.labelSmall
        )

        Divider(
            modifier = Modifier.weight(0.02f).fillMaxHeight(),
            thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
        )

        TextField(
            modifier = Modifier.weight(0.75f),
            value = feedUiState.actualConsumed,
            onValueChange = {
                onChangedValue(feedUiState.copy(actualConsumed = it))
            },
            suffix = { Text("Kg",
                color = LocalContentColor.current.copy(1f),
                style = MaterialTheme.typography.labelSmall) },
            enabled = false,
            colors = TextFieldDefaults.colors(
                disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = LocalContentColor.current.alpha),
                disabledTextColor = LocalContentColor.current.copy(LocalContentColor.current.alpha)),
            textStyle = MaterialTheme.typography.labelSmall
        )

        Divider(
            modifier = Modifier.weight(0.01f).fillMaxHeight(),
            thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
        )

        TextField(
            modifier = Modifier.weight(0.75f),
            value = feedUiState.standardConsumption,
            onValueChange = {
                onChangedValue(feedUiState.copy(standardConsumption = it))
            },
            suffix = { Text("Kg",
                color = LocalContentColor.current.copy(1f),
                style = MaterialTheme.typography.labelSmall) },
            readOnly = true,
            colors = TextFieldDefaults.colors(
                disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = LocalContentColor.current.alpha),
                disabledTextColor = LocalContentColor.current.copy(LocalContentColor.current.alpha)),
            textStyle = MaterialTheme.typography.labelSmall
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NkhukuManagementTheme {
        val feedUiState = FeedUiState(
            name = "Starter",
            week = "Week 1",
            actualConsumptionPerBird = "167",
            standardConsumptionPerBird = "167",
            standardConsumption = "7500",
            actualConsumed = "6500",
            feedingDate = LocalDate.now().toString()
            )
        FeedCardItem(feedUiState = feedUiState)
    }
}