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

@HiltViewModel
class ArticleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val firestoreDatabase: FirebaseFirestore
) : ViewModel() {

    private val _article = MutableStateFlow(Article())
    val article = _article.asStateFlow()

    val categoryId = savedStateHandle[ReadArticleDestination.categoryIdArg] ?: 0
    val articleId = savedStateHandle[ReadArticleDestination.articleIdArg] ?: ""

    fun setArticle(article: Article) {
        _article.update {
            article
        }
    }

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

    suspend fun retrieveArticle(
        category: String,
        subCollection: String,
        documentId: String
    ): Article? {
        return try {
            val article = getSingleArticle(category, subCollection, documentId)
                .await()
                .data

            Log.d("articles", articles.toString())
            article?.run { ->
                Article(
                    id = article["id"].toString(),
                    title = article["title"].toString(),
                    author = article["author"].toString(),
                    body = article["body"].toString()
                )
            }


        } catch (e: Exception) {
            Log.d("ERROR_RETRIEVING", "Error getting documents: ", e)
            null
        }
    }

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