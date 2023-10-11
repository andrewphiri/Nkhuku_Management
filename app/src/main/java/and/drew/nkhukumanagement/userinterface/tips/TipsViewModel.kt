package and.drew.nkhukumanagement.userinterface.tips

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
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
    savedStateHandle: SavedStateHandle,
    val firestoreDatabase: FirebaseFirestore
) : ViewModel() {

    //private val firestoreDatabase = Firebase.firestore

    private val _articlesList = MutableStateFlow(mutableListOf<Article>())
    val articlesList = _articlesList.asStateFlow()


    val articleIdCategory = savedStateHandle[TipsArticlesListDestination.articleCategoryIdArg] ?: 0
    val title = savedStateHandle[TipsArticlesListDestination.categoryId] ?: "Tips"

//    fun getArticlesList(): SnapshotStateList<Article> {
//        return articlesList
//    }

    fun setArticlesList(article: SnapshotStateList<Article>) {
        _articlesList.update {
            article
        }
    }

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

    private fun getCollection(): Task<QuerySnapshot> {
        return firestoreDatabase.collection("articles")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("ARTICLE", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("ERROR_RETRIEVING", "Error getting documents: ", exception)
            }
    }

    suspend fun retrieveCategories(): Map<String, String>? {
        val categories: MutableMap<String, String>? = mutableMapOf()
        return try {
            val tipsCategories = getCollection().await().documents
            for (category in tipsCategories) {
                categories?.set(category.id, category.get("category").toString())
                Log.d("CATEGORY", category.get("category").toString())
            }
            categories
        } catch (e: Exception) {
            Log.d("ERROR_RETRIEVING", "Error getting documents: ", e)
            null
        }
    }

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
                        body = article["body"].toString()
                    )
                )
            }
            Log.d("articles", articles.toString())
            articles
        } catch (e: Exception) {
            Log.d("ERROR_RETRIEVING", "Error getting documents: ", e)
            null
        }
    }

    suspend fun getSubCollection(category: String, subCollection: String): QuerySnapshot {
        return firestoreDatabase
            .collection("articles")
            .document(category)
            .collection(subCollection)
            .get()
            .addOnSuccessListener { result ->
//                for (document in result) {
//                    Log.d("ARTICLE", "${document.id} => ${document.data}")
//                }
            }
            .addOnFailureListener { exception ->
                Log.d("ERROR_RETRIEVING", "Error getting documents: ", exception)
            }
            .await()
    }

}