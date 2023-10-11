package and.drew.nkhukumanagement.userinterface.tips

import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.ui.theme.Shapes
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument

object TipsArticlesListDestination : NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.Inventory
    override val route: String
        get() = "tips_articles"
    override val resourceId: Int
        get() = TipsCategories.Brooding.resourceId
    const val articleCategoryIdArg = "id"
    const val categoryId = "categoryId"
    val routeWithArgs = "$route/{$categoryId}/{$articleCategoryIdArg}"
    val arguments = listOf(navArgument(categoryId) {
        defaultValue = ""
        type = NavType.StringType
    },
        navArgument(articleCategoryIdArg) {
            defaultValue = 1
            type = NavType.IntType
        })
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TipsArticlesListScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    navigateToReadArticle: (Int, String) -> Unit,
    tipsViewModel: TipsViewModel = hiltViewModel()
) {
    val tipsCategories = listOf(
        TipsCategories.Placement,
        TipsCategories.Brooding,
        TipsCategories.RearingAndFeeding,
        TipsCategories.Hygiene,
        TipsCategories.Equipment,
        TipsCategories.BlogArticles
    )
    var title: Int? by remember { mutableStateOf(0) }
    val articlesList by tipsViewModel.articlesList.collectAsState(
        initial = mutableListOf()
    )
    LaunchedEffect(tipsViewModel.articlesList) {
        tipsViewModel.generateArticles(tipsViewModel.articleIdCategory)
    }
    Log.d("ViewModel List", "$${articlesList.toList()}")
    Scaffold(
        topBar = {
            FlockManagementTopAppBar(
                title = tipsViewModel.title,
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        Column(modifier = modifier.padding(innerPadding)) {
            ArticlesList(
                articles = articlesList,
                onArticleClick = navigateToReadArticle,
                categoryId = tipsViewModel.articleIdCategory
            )
        }
    }
}

@Composable
fun ArticlesList(
    modifier: Modifier = Modifier,
    articles: List<Article>,
    onArticleClick: (Int, String) -> Unit,
    categoryId: Int
) {
    LazyColumn(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(articles) { article ->
            ArticleCard(
                article = article,
                onArticleClick = { onArticleClick(categoryId, article.id) }
            )
        }
    }
}

@Composable
fun ArticleCard(
    modifier: Modifier = Modifier,
    article: Article,
    onArticleClick: () -> Unit
) {
    ElevatedCard(
        modifier = modifier.clickable(onClick = onArticleClick),
        shape = Shapes.medium
    ) {
        Text(
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            text = article.title,
            style = MaterialTheme.typography.titleSmall
        )
    }
}

// create a list of article objects with dummy data
val articles = listOf(
    Article(
        "1",
        "How to use Kotlin data classes",
        "In this article, we will learn about the benefits and features of data classes in Kotlin."
    ),
    Article(
        "2",
        "Kotlin vs Java: Which one is better?",
        "Kotlin and Java are two popular programming languages that run on the JVM. In this article, we will compare and contrast their syntax, features, and performance."
    ),
    Article(
        "3",
        "Top 10 Kotlin tips and tricks",
        "Kotlin is a concise and expressive language that offers many powerful features. In this article, we will share some of the best tips and tricks to write better Kotlin code."
    ),
    Article(
        "4",
        "Introduction to coroutines in Kotlin",
        "Coroutines are a way of writing asynchronous and non-blocking code in Kotlin. In this article, we will explore the basics of coroutines and how to use them effectively."
    ),
    Article(
        "5",
        "Building a simple Android app with Kotlin",
        "Kotlin is the official language for Android development. In this article, we will show you how to create a simple Android app with Kotlin using Android Studio."
    )
)


