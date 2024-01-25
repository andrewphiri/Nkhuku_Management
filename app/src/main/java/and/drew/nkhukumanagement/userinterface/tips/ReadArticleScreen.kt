package and.drew.nkhukumanagement.userinterface.tips

import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import and.drew.nkhukumanagement.utils.ContentType
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

object ReadArticleDestination : NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.Article
    override val route: String
        get() = "read_article"
    override val resourceId: Int
        get() = R.string.read_article
    const val categoryIdArg = "categoryId"
    const val articleIdArg = "id"
    val routeWithArgs = "$route/{$categoryIdArg}/{$articleIdArg}"
    val arguments = listOf(navArgument(categoryIdArg) {
        defaultValue = 0
        type = NavType.IntType
    },
        navArgument(articleIdArg) {
            defaultValue = ""
            type = NavType.StringType
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReadArticleScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    articleViewModel: ArticleViewModel = hiltViewModel(),
    contentType: ContentType
) {
    val coroutineScope = rememberCoroutineScope()
    val article by articleViewModel.article.collectAsState(
        initial = Article()
    )

    MainReadArticleScreen(
        modifier = modifier,
        canNavigateBack = canNavigateBack,
        onNavigateUp = onNavigateUp,
        article = article,
        generateSingleArticle = { categoryId, documentId ->
            coroutineScope.launch {
                articleViewModel.generateSingleArticle(
                    id = categoryId,
                    documentId = documentId
                )
            }
        },
        categoryId = articleViewModel.categoryId.value,
        articleId = articleViewModel.articleId.value,
        contentType = contentType
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainReadArticleScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    article: Article,
    generateSingleArticle: (Int, String) -> Unit,
    categoryId: Int,
    articleId: String,
    contentType: ContentType
) {

    BackHandler {
        onNavigateUp()
    }
    LaunchedEffect(article) {
        generateSingleArticle(
            categoryId,
            articleId
        )
    }
    Scaffold(
        topBar = {
            FlockManagementTopAppBar(
                title = article.title,
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
                contentType = contentType
            )
        }
    ) { innerPadding ->
        Column(modifier = modifier.padding(innerPadding)) {
            ReadArticleCard(
                article = article
            )
        }
    }
}

@Composable
fun ReadArticleCard(modifier: Modifier = Modifier, article: Article) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.verticalScroll(state = scrollState, enabled = true),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth().defaultMinSize(minHeight = 300.dp),
            model = article.imageUrl,
            contentDescription = article.title
        )
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = article.title,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = article.body.replace("\\n", "\n"),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }

}