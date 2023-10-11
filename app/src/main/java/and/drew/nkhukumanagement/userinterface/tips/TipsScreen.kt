package and.drew.nkhukumanagement.userinterface.tips

import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.userinterface.navigation.NavigationBarScreens
import and.drew.nkhukumanagement.utils.BaseCard
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TipsScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = false,
    navigateToArticlesListScreen: (String, Int) -> Unit
) {
    Scaffold(
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(NavigationBarScreens.Tips.resourceId),
                canNavigateBack = canNavigateBack
            )
        }
    ) { innerPadding ->

        CategoriesList(
            modifier = Modifier.padding(innerPadding),
            onCardClick = navigateToArticlesListScreen
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CategoriesList(
    modifier: Modifier = Modifier,
    onCardClick: (String, Int) -> Unit
) {
    val context = LocalContext.current

    val tipsCategories = listOf(
        TipsCategories.Placement,
        TipsCategories.Brooding,
        TipsCategories.RearingAndFeeding,
        TipsCategories.Hygiene,
        TipsCategories.Equipment,
        TipsCategories.BlogArticles
    )
    LazyVerticalGrid(
        modifier = modifier.padding(16.dp),
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(tipsCategories) { index, tipCategory ->
            BaseCard(
                onCardClick = {
                    onCardClick(
                        context.getString(tipCategory.resourceId),
                        tipCategory.id
                    )
                },
                contentDescription = tipCategory.contentDescription,
                imageVector = tipCategory.icon,
                label = stringResource(tipCategory.resourceId)
            )
        }
    }
}