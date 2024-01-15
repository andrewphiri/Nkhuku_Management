package and.drew.nkhukumanagement.userinterface.overview


import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.prefs.UserPrefsViewModel
import and.drew.nkhukumanagement.userinterface.navigation.NavigationBarScreens
import and.drew.nkhukumanagement.utils.BaseCard
import and.drew.nkhukumanagement.utils.ContentType
import and.drew.nkhukumanagement.utils.OverViewDetailsCurrentScreen
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OverviewScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = false,
    navigateToAccountOverviewScreen: () -> Unit,
    navigateToFlockOverviewScreen: () -> Unit,
    userPrefsViewModel: UserPrefsViewModel,
    onClickSettings: () -> Unit,
    contentType: ContentType
) {
    if (contentType == ContentType.LIST_ONLY) {
        MainOverViewScreen(
            modifier = modifier,
            canNavigateBack = canNavigateBack,
            navigateToAccountOverviewScreen = navigateToAccountOverviewScreen,
            navigateToFlockOverviewScreen = navigateToFlockOverviewScreen,
            onClickSettings = onClickSettings
        )
    } else {
        OverviewAndDetailsScreen(
            modifier = modifier,
            onClickSettings = {},
            userPrefsViewModel = userPrefsViewModel
        )
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OverviewAndDetailsScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = false,
    onClickSettings: () -> Unit,
    userPrefsViewModel: UserPrefsViewModel,
) {
    var showDetailsPane by rememberSaveable { mutableStateOf(false) }
    var currentScreen by rememberSaveable { mutableStateOf(OverViewDetailsCurrentScreen.ACCOUNTS_OVERVIEW_SCREEN) }
    Column(modifier = modifier) {
        Row {
            Column(modifier = Modifier.weight(1f)) {
                MainOverViewScreen(
                    canNavigateBack = canNavigateBack,
                    navigateToAccountOverviewScreen = {
                        showDetailsPane = true
                        currentScreen = OverViewDetailsCurrentScreen.ACCOUNTS_OVERVIEW_SCREEN
                    },
                    navigateToFlockOverviewScreen = {
                        showDetailsPane = true
                        currentScreen = OverViewDetailsCurrentScreen.FLOCK_OVERVIEW_SCREEN
                    },
                    onClickSettings = onClickSettings
                )
            }

            Spacer(
                modifier = Modifier
                    .weight(0.001f)
                    .fillMaxHeight()
                    .width(Dp.Hairline)
                    .padding(top = 16.dp, bottom = 16.dp)
                    .background(color = MaterialTheme.colorScheme.tertiary),

                )
            Column(modifier = Modifier.weight(1f)) {
                if (showDetailsPane) {
                    when (currentScreen) {
                        OverViewDetailsCurrentScreen.ACCOUNTS_OVERVIEW_SCREEN -> {
                            AccountOverviewScreen(
                                canNavigateBack = false,
                                onNavigateUp = {},
                                userPrefsViewModel = userPrefsViewModel
                            )
                        }

                        OverViewDetailsCurrentScreen.FLOCK_OVERVIEW_SCREEN -> {
                            FlockOverviewScreen(
                                canNavigateBack = false,
                                onNavigateUp = {}
                            )
                        }
                    }
                }
            }
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainOverViewScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = false,
    navigateToAccountOverviewScreen: () -> Unit,
    navigateToFlockOverviewScreen: () -> Unit,
    onClickSettings: () -> Unit
) {
    Scaffold(
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(NavigationBarScreens.Overview.resourceId),
                canNavigateBack = canNavigateBack,
                onClickSettings = onClickSettings
            )
        }
    ) { innerPadding ->
        Column(modifier = modifier.padding(innerPadding)) {
            OverviewScreenCardList(
                onAccountOverviewCardClick = navigateToAccountOverviewScreen,
                onFlockOverviewCardClick = navigateToFlockOverviewScreen
            )
        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OverviewScreenCardList(
    modifier: Modifier = Modifier,
    onAccountOverviewCardClick: () -> Unit,
    onFlockOverviewCardClick: () -> Unit,
) {
    LazyVerticalGrid(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        columns = GridCells.Fixed(2)
    ) {
        item {
            BaseCard(
                onCardClick = onAccountOverviewCardClick,
                contentDescription = "Account Overview",
                label = "Accounts",
                imageVector = Icons.Default.AttachMoney,
            )
        }
        item {
            BaseCard(
                onCardClick = onFlockOverviewCardClick,
                contentDescription = "Flock Overview",
                label = "Flock",
                imageVector = Icons.Default.Inventory,
            )

        }
    }
}


