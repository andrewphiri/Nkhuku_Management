package and.drew.nkhukumanagement.userinterface.tips

import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import and.drew.nkhukumanagement.utils.ContentType
import and.drew.nkhukumanagement.utils.HtmlImageGetter
import android.graphics.Typeface
import android.os.Build
import android.text.Editable
import android.text.Html
import android.text.Layout
import android.text.Spannable
import android.text.Spanned
import android.text.style.AlignmentSpan
import android.text.style.StyleSpan
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.text.method.LinkMovementMethodCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import org.xml.sax.XMLReader

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
    val categoryId by articleViewModel.categoryId.collectAsState()
    val articleId by articleViewModel.articleId.collectAsState()
    var isCircularIndicatorShowing by rememberSaveable { mutableStateOf(true) }

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
            }.invokeOnCompletion { isCircularIndicatorShowing = false }
        },
        categoryId = categoryId,
        articleId = articleId,
        contentType = contentType,
        isCircularIndicatorShowing = isCircularIndicatorShowing
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
    contentType: ContentType,
    isCircularIndicatorShowing: Boolean
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
        contentWindowInsets = WindowInsets(0.dp),
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
                article = article,
                isCircularIndicatorShowing = isCircularIndicatorShowing
            )
        }
    }
}

@Composable
fun ReadArticleCard(
    modifier: Modifier = Modifier,
    article: Article,
    isCircularIndicatorShowing: Boolean
) {
    val scrollState = rememberScrollState()
    if (isCircularIndicatorShowing) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    } else {
        Column(
            modifier = modifier.verticalScroll(state = scrollState, enabled = true),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
//            AsyncImage(
//                modifier = Modifier
//                    .fillMaxWidth().defaultMinSize(minHeight = 300.dp),
//                model = article.imageUrl,
//                contentDescription = article.title
//            )
//            val html = "<!DOCTYPE html>" +
//                    "<html lang=\"en\">" +
//                    "<head>" +
//                    "    <meta charset=\"UTF-8\">" +
//                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
//                    "</head>" +
//                    "<body>" +
//                    "    <h1>HTML Text Test</h1>" +
//                    "    <p>This is a paragraph of text.</p> <br>" +
//                    "    <p>This is another paragraph of text.</p>" +
//                    "    <br><br>" +
//                    "    <h1>This is a bold title.</h1><br>" +
//                    "    <p>This is another paragraph of text with <em>italic</em> words.</p>" +
//                    "    <img src=\"https://th.bing.com/th/id/OIG4.z9DTZ1VWNtS99mjQ_1qB?pid=ImgGn\" alt=\"Example Image\">" +
//                    "    <p>This is a paragraph of text.</p> <br>" +
//                    "    <p>This is another paragraph of text.</p>" +
//                    "    <img src=\"https://th.bing.com/th/id/OIG4.._49STZ4hT364wtP9B1c?pid=ImgGn\" alt=\"Example Image\">" +
//                    "</body>" +
//                    "</html>"
            //val myArticle = ArticleDescription(html)
            val coroutineScope = rememberCoroutineScope()
            val context = LocalContext.current

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
//                Text(
//                    modifier = Modifier.fillMaxWidth(),
//                    text = article.title,
//                    style = MaterialTheme.typography.headlineMedium
//                )
                val tagHandler = object :  Html.TagHandler {
                    private var inTableRow = false

                    override fun handleTag(opening: Boolean, tag: String?, output: Editable?, xmlReader: XMLReader?) {
                        if (tag.isNullOrEmpty() || output == null) return

                        if (tag.equals("tr", ignoreCase = true)) {
                            if (opening) {
                                inTableRow = true
                            } else {
                                inTableRow = false
                                output.append("\n") // Add new line after the table row
                            }
                        } else if (tag.equals("td", ignoreCase = true) && inTableRow) {
                            if (!opening) {
                                output.append(" ") // Add a space after each cell
                            }
                        }
                    }
                }
                AndroidView(
                    factory = { context ->
                        TextView(context).apply {
                            movementMethod = LinkMovementMethodCompat.getInstance()
                        }
                    },
                    update = {
                        val imageGetter = HtmlImageGetter(
                            scope = coroutineScope,
                            res = context.resources,
                            coil = ImageRequest.Builder(context),
                            textView = it
                        )
                        val htmlText = HtmlCompat.fromHtml(article.body, HtmlCompat.FROM_HTML_MODE_COMPACT, imageGetter, null)
                        it.text = htmlText
                        it.setTextColor(context.resources.getColor(R.color.textColor, null))
                        it.setTextAppearance(R.style.AppTextAppearance_Body1)
                    }
                )


//                AndroidView(
//                    modifier = Modifier.fillMaxWidth(),
//                    factory = { ctx ->
//                        WebView(ctx).apply {
//
//                            settings.javaScriptEnabled = true
//                            webViewClient = WebViewClient()
//                            settings.useWideViewPort = true
//                            settings.loadWithOverviewMode = true
//                            settings.loadsImagesAutomatically = true
//                            settings.allowContentAccess = true
//
//                        }
//                    },
//                    update = {webView ->
//                        webView.loadDataWithBaseURL(null, article.body, "text/html", "UTF-8", null)
//                    }
//                )

//                Text(
//                    modifier = Modifier.fillMaxWidth(),
//                    text = article.body.replace("\\n", "\n"),
//                    style = MaterialTheme.typography.bodyLarge
//                )
            }
        }
    }
}

//@Composable
//fun ArticleDescription(html: String) : Spanned {
//    val coroutineScope = rememberCoroutineScope()
//    val context = LocalContext.current
//    val htmlImageGetter = HtmlImageGetter(
//        res = context.resources,
//        scope = coroutineScope,
//        coil = ImageRequest.Builder(context = context ),
//
//    )
//    return remember(html) {
//        HtmlCompat.fromHtml(
//            html,
//            HtmlCompat.FROM_HTML_MODE_COMPACT,
//            htmlImageGetter,
//            //Handle Html tags
//            object : Html.TagHandler {
//                override fun handleTag(
//                    opening: Boolean,
//                    tag: String,
//                    output: Editable?,
//                    xmlReader: XMLReader?
//                ) {
//                    when {
//                        tag.equals("h1", ignoreCase = true) && opening -> {
//                            // Apply heading 1 style
//                            output?.setSpan(
//                                StyleSpan(Typeface.BOLD),
//                                output.length,
//                                output.length,
//                                Spannable.SPAN_MARK_MARK
//                            )
//                        }
//
//                        tag.equals("h1", ignoreCase = true) && !opening -> {
//                            // End of heading 1
//                            val mark = output?.getSpanStart(Spannable.SPAN_MARK_MARK)
//                            if (mark != null) {
//                                output?.removeSpan(mark)
//                            }
//                        }
//
//                        tag.equals("b", ignoreCase = true) && opening -> {
//                            // Apply bold style
//                            output?.setSpan(
//                                StyleSpan(Typeface.BOLD),
//                                output.length,
//                                output.length,
//                                Spannable.SPAN_MARK_MARK
//                            )
//                        }
//
//                        tag.equals("b", ignoreCase = true) && !opening -> {
//                            // End of bold
//                            val mark = output?.getSpanStart(Spannable.SPAN_MARK_MARK)
//                            if (mark != null) {
//                                val boldSpans =
//                                    output.getSpans(mark, output.length, StyleSpan::class.java)
//                                if (boldSpans.isNotEmpty()) {
//                                    val lastBoldSpan = boldSpans.last()
//                                    val boldStart = output.getSpanStart(lastBoldSpan)
//                                    output.removeSpan(lastBoldSpan)
//                                    if (boldStart != null && boldStart != output.length) {
//                                        output.setSpan(
//                                            StyleSpan(Typeface.BOLD),
//                                            boldStart,
//                                            output.length,
//                                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                                        )
//                                    }
//                                }
//                                output?.removeSpan(mark)
//                            }
//                        }
//
//                        tag.equals("p", ignoreCase = true) && opening -> {
//                            // Apply paragraph style
//                            output?.append("\n\n") // Add space before paragraph
//                        }
//                    }
//                }
//            }
//        )
//    }
//}

