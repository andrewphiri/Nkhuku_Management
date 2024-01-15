package and.drew.nkhukumanagement.userinterface.tips

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class TipsViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    val firestoreDatabase: FirebaseFirestore
) : ViewModel() {

    //private val firestoreDatabase = Firebase.firestore

    private val _articlesList = MutableStateFlow(mutableListOf<Article>())
    val articlesList = _articlesList.asStateFlow()

    //Category ID of the category chosen
    val articleIdCategory = savedStateHandle
        .getStateFlow(TipsArticlesListDestination.articleCategoryIdArg, initialValue = 0)

    //Name of category. Used as title of the app bar
    val title = savedStateHandle
        .getStateFlow(key = TipsArticlesListDestination.categoryId, initialValue = "Tips")


    fun setArticlesList(article: SnapshotStateList<Article>) {
        _articlesList.update {
            article
        }
    }

    /**
     * Retrieve articles from [retrieveCategoryArticles] for each category chosen and set
     * the result to [_articlesList]
     */
    suspend fun generateArticles(id: Int) {
        when (id) {
            1 -> {
                retrieveCategoryArticles(
                    "Brooding",
                    "brooding"
                )?.let { setArticlesList(it.toMutableStateList()) }
                //getArticlesList()
            }

            2 -> {
                retrieveCategoryArticles(
                    "Equipment",
                    "equipment"
                )?.let { setArticlesList(it.toMutableStateList()) }
                // getArticlesList()
            }

            3 -> {
                retrieveCategoryArticles(
                    "Feeding",
                    "feeding"
                )?.let { setArticlesList(it.toMutableStateList()) }
                // getArticlesList()
            }

            4 -> {
                retrieveCategoryArticles(
                    "Hygiene",
                    "hygiene"
                )?.let { setArticlesList(it.toMutableStateList()) }
                // getArticlesList()
            }

            5 -> {
                retrieveCategoryArticles(
                    "Placement",
                    "placement"
                )?.let { setArticlesList(it.toMutableStateList()) }
                // getArticlesList()
            }
        }
    }

    /**
     * Retrieve the articles from [getSubCollection] and return a list of articles
     */
    suspend fun retrieveCategoryArticles(category: String, subCollection: String): List<Article>? {
        val articles: MutableList<Article> = mutableListOf()
        return try {
            val articlesList = getSubCollection(category, subCollection)
                .documents
            for (article in articlesList) {
//                article.toObject<Article>()?.let { articles.add(it) }
                articles.add(
                    Article(
                        id = article.id,
                        title = article["title"].toString(),
                        author = article["author"].toString(),
                        body = article["body"].toString(),
                        imageUrl = article["imageUrl"].toString()
                    )
                )
            }
            articles
        } catch (e: Exception) {
            Log.d("ERROR_RETRIEVING", "Error getting documents: ", e)
            null
        }
    }

    /**
     * Get subCollection of [ARTICLES] collection from firestore
     */
    suspend fun getSubCollection(category: String, subCollection: String): QuerySnapshot {
        return firestoreDatabase
            .collection("articles")
            .document(category)
            .collection(subCollection)
            .get()
            .addOnSuccessListener { result ->
            }
            .addOnFailureListener { exception ->
                Log.d("ERROR_RETRIEVING", "Error getting documents: ", exception)
            }
            .await()
    }

    fun setCategoryID(id: Int) {
        savedStateHandle[TipsArticlesListDestination.articleCategoryIdArg] = id
    }

    fun setTitle(title: String) {
        savedStateHandle[TipsArticlesListDestination.categoryId] = title
    }

}