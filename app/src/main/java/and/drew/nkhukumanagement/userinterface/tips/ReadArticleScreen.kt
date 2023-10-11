package and.drew.nkhukumanagement.userinterface.tips

import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import android.os.Build
import android.text.SpannableStringBuilder
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument

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
    articleViewModel: ArticleViewModel = hiltViewModel()
) {
    val article by articleViewModel.article.collectAsState(
        initial = Article()
    )
    LaunchedEffect(articleViewModel.article) {
        articleViewModel.generateSingleArticle(
            id = articleViewModel.categoryId,
            documentId = articleViewModel.articleId
        )
    }
    Scaffold(
        topBar = {
            FlockManagementTopAppBar(
                title = article.title,
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
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
    //val context = LocalContext.current
    val spannableString = SpannableStringBuilder(article.body).toString()
    val spanned = HtmlCompat
        .fromHtml(spannableString, HtmlCompat.FROM_HTML_MODE_COMPACT)
    Column(
        modifier = modifier.padding(16.dp).verticalScroll(state = scrollState, enabled = true),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            modifier = Modifier.padding().fillMaxWidth(),
            text = article.title,
            style = MaterialTheme.typography.titleMedium
        )

        AndroidView(
            factory = { context -> TextView(context) },
            update = {
                it.text = HtmlCompat.fromHtml(article.body, HtmlCompat.FROM_HTML_MODE_LEGACY)
            }
        )
        Text(
            modifier = Modifier.padding().fillMaxWidth(),
            text = AnnotatedString(
                HtmlCompat
                    .fromHtml(
                        SpannableStringBuilder(article.body).toString(),
                        HtmlCompat.FROM_HTML_MODE_COMPACT
                    ).toString()
            ),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}