package and.drew.nkhukumanagement.userinterface.tips

import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.auth.GoogleAuthUiClient
import and.drew.nkhukumanagement.auth.SignInViewModel
import and.drew.nkhukumanagement.userinterface.navigation.NavigationBarScreens
import and.drew.nkhukumanagement.utils.BaseCard
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TipsScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = false,
    navigateToArticlesListScreen: (String, Int) -> Unit,
    onClickSettings: () -> Unit,
    signInViewModel: SignInViewModel = hiltViewModel(),
    googleAuthUiClient: GoogleAuthUiClient
) {
    val userSignedIn by signInViewModel.userLoggedIn.collectAsState(initial = false)
    LaunchedEffect(
        key1 = signInViewModel.userLoggedIn
    ) {
        signInViewModel.setUserLoggedIn(loggedIn = googleAuthUiClient.getSignedInUser() != null)

    }
    MainTipsScreen(
        modifier = modifier,
        canNavigateBack = canNavigateBack,
        navigateToArticlesListScreen = navigateToArticlesListScreen,
        onClickSettings = onClickSettings,
        isUserSignedIn = userSignedIn
    )
//    Scaffold(
//        modifier = modifier,
//        topBar = {
//            FlockManagementTopAppBar(
//                title = stringResource(NavigationBarScreens.Tips.resourceId),
//                canNavigateBack = canNavigateBack,
//                onClickSettings = onClickSettings
//            )
//        }
//    ) { innerPadding ->
//
//        CategoriesList(
//            modifier = Modifier.padding(innerPadding),
//            onCardClick = navigateToArticlesListScreen
//        )
//    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainTipsScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = false,
    navigateToArticlesListScreen: (String, Int) -> Unit,
    onClickSettings: () -> Unit,
    isUserSignedIn: Boolean
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(NavigationBarScreens.Tips.resourceId),
                canNavigateBack = canNavigateBack,
                onClickSettings = onClickSettings
            )
        }
    ) { innerPadding ->
        if (isUserSignedIn) {
            CategoriesList(
                modifier = Modifier.padding(innerPadding),
                onCardClick = navigateToArticlesListScreen
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Please Sign in to receive tips.",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
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