package and.drew.nkhukumanagement.userinterface.overview


import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.userinterface.navigation.NavigationBarScreens
import and.drew.nkhukumanagement.utils.BaseCard
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OverviewScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = false,
    navigateToAccountOverviewScreen: () -> Unit,
    navigateToFlockOverviewScreen: () -> Unit,
) {

    Scaffold(
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(NavigationBarScreens.Overview.resourceId),
                canNavigateBack = canNavigateBack
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


