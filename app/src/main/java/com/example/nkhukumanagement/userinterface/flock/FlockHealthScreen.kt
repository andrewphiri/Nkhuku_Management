package com.example.nkhukumanagement.userinterface.flock

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.nkhukumanagement.FlockManagementTopAppBar
import com.example.nkhukumanagement.R
import com.example.nkhukumanagement.data.FlockHealth
import com.example.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import com.example.nkhukumanagement.utils.DateUtils


object FlockHealthScreenDestination : NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.MedicalServices
    override val route: String
        get() = "Flock Health"
    override val resourceId: Int
        get() = R.string.flock
    const val flockIdArg = "id"
    val routeWithArgs = "$route/{$flockIdArg}"
    val arguments = listOf(navArgument(flockIdArg) {
        defaultValue = 1
        type = NavType.IntType
    })
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FlockHealthScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    navigateToFlockEditScreen: (Int?, Int) -> Unit,
    editFlockViewModel: EditFlockViewModel = hiltViewModel()
) {
    val flockWithHealth by editFlockViewModel.flockWithHealth.collectAsState()
    val listState = rememberLazyListState()
    Scaffold(
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(FlockHealthScreenDestination.resourceId),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        },
        floatingActionButton = {
            AnimatedVisibility(visible = listState.isScrollingUp(),
                enter = slideIn(tween(200, easing = LinearOutSlowInEasing),
                    initialOffset = {
                        IntOffset(180, 90)
                    }),
                exit = slideOut(tween(200, easing = FastOutSlowInEasing)) {
                    IntOffset(180, 90)
                }
            ) {
                FloatingActionButton(
                    onClick = { navigateToFlockEditScreen(flockWithHealth.flock?.id, 0) },
                    modifier = Modifier.navigationBarsPadding(),
                    shape = ShapeDefaults.Small,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    elevation = FloatingActionButtonDefaults.elevation()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add mortality and/or cull",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = modifier.padding(innerPadding)) {
            flockWithHealth.health?.let {
                FlockHealthList(
                    onItemClick = { healthId ->
                        navigateToFlockEditScreen(flockWithHealth.flock?.id, healthId)
                    },
                    flockHealthList = it
                )
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FlockHealthList(
    modifier: Modifier = Modifier,
    onItemClick: (Int) -> Unit,
    flockHealthList: List<FlockHealth>
) {
    if (flockHealthList.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = stringResource(R.string.no_records_add_mort_cull),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    } else {
        Column(
            modifier = modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    modifier = Modifier.weight(1.5f),
                    text = "Date",
                    textAlign = TextAlign.Center
                )
                Text(
                    modifier = Modifier.weight(1f, fill = true),
                    text = "Mortality",
                    textAlign = TextAlign.Center
                )

                Divider(
                    modifier = Modifier.weight(0.01f).fillMaxHeight(),
                    thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
                )

                Text(
                    modifier = Modifier.weight(1f, fill = true),
                    text = "Culls",
                    textAlign = TextAlign.Center
                )
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                itemsIndexed(flockHealthList) { index, flockHealthItem ->
                    FlockHealthCard(
                        flockHealth = flockHealthItem,
                        onItemClick = { onItemClick(flockHealthItem.id) }
                    )
                }
            }
        }
    }

}

/**
 * Composable function to detect scroll position of list
 * This will be used to hide FAB when scrolling down, and to Show FAB when scrolling up
 */
@Composable
private fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FlockHealthCard(
    modifier: Modifier = Modifier,
    flockHealth: FlockHealth,
    onItemClick: () -> Unit,
) {
    Card(modifier = modifier.clickable(onClick = onItemClick)) {
        Row(
            modifier = Modifier.padding(16.dp).height(intrinsicSize = IntrinsicSize.Min)
        ) {
            Text(
                modifier = Modifier.weight(weight = 1.5f, fill = true),
                text = DateUtils().dateToStringShortFormat(flockHealth.date),
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.weight(1f, fill = true),
                text = flockHealth.mortality.toString(),
                textAlign = TextAlign.Center
            )

            Divider(
                modifier = Modifier.weight(0.01f).fillMaxHeight(),
                thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
            )

            Text(
                modifier = Modifier.weight(1f, fill = true),
                text = flockHealth.culls.toString(),
                textAlign = TextAlign.Center
            )
        }
    }

}