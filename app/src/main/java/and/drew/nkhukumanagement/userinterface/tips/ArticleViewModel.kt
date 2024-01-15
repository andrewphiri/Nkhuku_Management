package and.drew.nkhukumanagement.userinterface.tips

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Viewmodel to retrieve single article and display it
 */
@HiltViewModel
class ArticleViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    val firestoreDatabase: FirebaseFirestore
) : ViewModel() {
    // Mutable and immutable article
    private val _article = MutableStateFlow(Article())
    val article = _article.asStateFlow()

    val categoryId = savedStateHandle
        .getStateFlow(key = ReadArticleDestination.categoryIdArg, initialValue = 0)
    val articleId = savedStateHandle
        .getStateFlow(key = ReadArticleDestination.articleIdArg, initialValue = "")

    fun setArticle(article: Article) {
        _article.update {
            article
        }
    }

    fun setCategoryID(id: Int) {
        savedStateHandle[ReadArticleDestination.categoryIdArg] = id
    }

    fun setArticleID(articleID: String) {
        savedStateHandle[ReadArticleDestination.articleIdArg] = articleID
    }

    /**
     * Function to get an article from firestore and return the result
     */
    fun getSingleArticle(
        category: String,
        subCollection: String,
        documentId: String
    ): Task<DocumentSnapshot> {
        return firestoreDatabase
            .collection("articles")
            .document(category)
            .collection(subCollection)
            .document(documentId)
            .get()
            .addOnSuccessListener { result ->
            }
            .addOnFailureListener { exception ->
                Log.d("ERROR_RETRIEVING", "Error getting documents: ", exception)
            }
    }

    /**
     * Retrieve the article returned from [getSingleArticle]
     */
    suspend fun retrieveArticle(
        category: String,
        subCollection: String,
        documentId: String
    ): Article? {
        return try {
            val article = getSingleArticle(category, subCollection, documentId)
                .await()
                .data

            article?.run { ->
                Article(
                    id = article["id"].toString(),
                    title = article["title"].toString(),
                    author = article["author"].toString(),
                    body = article["body"].toString(),
                    imageUrl = article["imageUrl"].toString()
                )
            }
        } catch (e: Exception) {
            Log.d("ERROR_RETRIEVING", "Error getting documents: ", e)
            null
        }
    }

    /**
     * Get the article retrieved from [retrieveArticle] and set it to the mutable property [_article]
     */
    suspend fun generateSingleArticle(id: Int, documentId: String) {
        when (id) {
            1 -> {
                retrieveArticle("Brooding", "brooding", documentId)?.let { setArticle(it) }
            }

            2 -> {
                retrieveArticle("Equipment", "equipment", documentId)?.let { setArticle(it) }
            }

            3 -> {
                retrieveArticle("Feeding", "feeding", documentId)?.let { setArticle(it) }
            }

            4 -> {
                retrieveArticle("Hygiene", "hygiene", documentId)?.let { setArticle(it) }
            }

            5 -> {
                retrieveArticle("Placement", "placement", documentId)?.let { setArticle(it) }
            }
        }
    }
}