package and.drew.nkhukumanagement.userinterface.tips

import and.drew.nkhukumanagement.R
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Egg
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.ui.graphics.vector.ImageVector

sealed class TipsCategories(
    val id: Int,
    @StringRes val resourceId: Int,
    val contentDescription: String,
    val icon: ImageVector
) {
    object Brooding : TipsCategories(
        id = 1,
        resourceId = R.string.brooding,
        contentDescription = "brooding",
        icon = Icons.Outlined.Egg
    )

    object Equipment : TipsCategories(
        id = 2,
        resourceId = R.string.equipment,
        contentDescription = "Equipment",
        icon = Icons.Outlined.Inventory
    )

    object RearingAndFeeding : TipsCategories(
        id = 3,
        resourceId = R.string.feeding,
        contentDescription = "Feeding",
        icon = Icons.Outlined.Inventory2
    )

    object Hygiene : TipsCategories(
        id = 4,
        resourceId = R.string.hygiene,
        contentDescription = "Sanitation and hygiene",
        icon = Icons.Outlined.HealthAndSafety
    )

    object Placement : TipsCategories(
        id = 5,
        resourceId = R.string.placement,
        contentDescription = "Chick placement procedure",
        icon = Icons.Outlined.Checklist
    )


    object BlogArticles : TipsCategories(
        id = 6,
        resourceId = R.string.blog_articles,
        contentDescription = "Poultry blog articles",
        icon = Icons.Outlined.Article
    )
}
