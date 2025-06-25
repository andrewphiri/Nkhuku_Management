package and.drew.nkhukumanagement.userinterface.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Contextual


data class NavigationBarRoutes<T: Any>(
    val name: String,
    val route: T,
    @StringRes val resourceId: Int,
    @Contextual val icon: ImageVector
)


