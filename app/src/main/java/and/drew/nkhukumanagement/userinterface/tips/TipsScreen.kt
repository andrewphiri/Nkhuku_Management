package and.drew.nkhukumanagement.userinterface.tips

import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.auth.GoogleAuthUiClient
import and.drew.nkhukumanagement.auth.SignInViewModel
import and.drew.nkhukumanagement.userinterface.navigation.NavigationBarScreens
import and.drew.nkhukumanagement.utils.BaseCard
import and.drew.nkhukumanagement.utils.ContentType
import and.drew.nkhukumanagement.utils.TipsAndDetailsCurrentScreen
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TipsScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = false,
    navigateToArticlesListScreen: (String, Int) -> Unit,
    navigateToLoginScreen: () -> Unit,
    onClickSettings: () -> Unit,
    signInViewModel: SignInViewModel = hiltViewModel(),
    tipsViewModel: TipsViewModel = hiltViewModel(),
    articleViewModel: ArticleViewModel = hiltViewModel(),
    googleAuthUiClient: GoogleAuthUiClient,
    contentType: ContentType,
    isUserSignedIn: Boolean
) {

    if (contentType == ContentType.LIST_ONLY) {
        MainTipsScreen(
            modifier = modifier,
            canNavigateBack = canNavigateBack,
            navigateToArticlesListScreen = navigateToArticlesListScreen,
            onClickSettings = onClickSettings,
            isUserSignedIn = isUserSignedIn,
            navigateToLoginScreen = navigateToLoginScreen,
            contentType = contentType
        )
    } else {
        TipsAndDetailsScreen(
            modifier = modifier,
            canNavigateBack = canNavigateBack,
            onClickSettings = onClickSettings,
            isUserSignedIn = isUserSignedIn,
            articleViewModel = articleViewModel,
            tipsViewModel = tipsViewModel,
            navigateToLoginScreen = navigateToLoginScreen,
            contentType = contentType
        )
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TipsAndDetailsScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = false,
    tipsViewModel: TipsViewModel,
    articleViewModel: ArticleViewModel,
    onClickSettings: () -> Unit,
    isUserSignedIn: Boolean,
    navigateToLoginScreen: () -> Unit,
    contentType: ContentType,
) {
    var currentScreen by rememberSaveable { mutableStateOf(TipsAndDetailsCurrentScreen.ARTICLES_LIST_SCREEN) }
    var showDetailsPane by rememberSaveable { mutableStateOf(false) }
    val articlesList by tipsViewModel.articlesList.collectAsState(
        initial = mutableListOf()
    )

    Column {
        Row {
            Column(modifier = Modifier.weight(0.75f)) {
                MainTipsScreen(
                    modifier = modifier,
                    canNavigateBack = canNavigateBack,
                    navigateToArticlesListScreen = { title, id ->
                        articlesList.clear()
                        showDetailsPane = true
                        currentScreen = TipsAndDetailsCurrentScreen.ARTICLES_LIST_SCREEN
                        tipsViewModel.setTitle(title)
                        tipsViewModel.setCategoryID(id)
                    },
                    onClickSettings = onClickSettings,
                    isUserSignedIn = isUserSignedIn,
                    navigateToLoginScreen = navigateToLoginScreen,
                    contentType = contentType
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
                        TipsAndDetailsCurrentScreen.ARTICLES_LIST_SCREEN -> {
                            TipsArticlesListScreen(
                                canNavigateBack = false,
                                onNavigateUp = {},
                                navigateToReadArticle = { categoryId, articleId ->
                                    currentScreen =
                                        TipsAndDetailsCurrentScreen.SINGLE_ARTICLE_SCREEN
                                    articleViewModel.setCategoryID(categoryId)
                                    articleViewModel.setArticleID(articleId)
                                },
                                contentType = contentType
                            )
                        }

                        TipsAndDetailsCurrentScreen.SINGLE_ARTICLE_SCREEN -> {
                            ReadArticleScreen(
                                canNavigateBack = true,
                                onNavigateUp = {
                                    currentScreen = TipsAndDetailsCurrentScreen.ARTICLES_LIST_SCREEN
                                },
                                contentType = contentType
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
fun MainTipsScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = false,
    navigateToArticlesListScreen: (String, Int) -> Unit,
    navigateToLoginScreen: () -> Unit,
    onClickSettings: () -> Unit,
    isUserSignedIn: Boolean,
    contentType: ContentType,
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        modifier = modifier,
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(NavigationBarScreens.Tips.resourceId),
                canNavigateBack = canNavigateBack,
                onClickSettings = onClickSettings,
                contentType = contentType
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
                Row {
                    Text(
                        text = stringResource(R.string.please),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        modifier = Modifier.clickable(
                            onClick = navigateToLoginScreen,
                        ),
                        color = Color.Blue,
                        textDecoration = TextDecoration.Underline,
                        text = stringResource(R.string.signin),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = stringResource(R.string.to_receive_tips),
                        style = MaterialTheme.typography.labelMedium
                    )
                }

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
                description = tipCategory.contentDescription,
                imageVector = tipCategory.icon,
                label = stringResource(tipCategory.resourceId)
            )
        }
    }
}