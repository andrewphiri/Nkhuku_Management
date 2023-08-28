package com.example.nkhukumanagement.userinterface.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.TipsAndUpdates
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.nkhukumanagement.R

sealed class NavigationBarScreens(val route: String,
                                  @StringRes val resourceId: Int,
                                  val icon: ImageVector,
                                  val iconSelected: ImageVector,
                                  var isIconSelected: Boolean = false) {
    object Home: NavigationBarScreens(route = "Home", resourceId = R.string.home,
        icon = Icons.Outlined.Home, iconSelected = Icons.Default.Home )
    object Accounts: NavigationBarScreens(route = "Accounts", resourceId =R.string.accounts,
       icon = Icons.Outlined.AttachMoney, iconSelected = Icons.Default.AttachMoney )
    object Planner: NavigationBarScreens(route = "Planner",resourceId = R.string.planner,
        icon = Icons.Outlined.Calculate, iconSelected = Icons.Default.Calculate)
    object Tips: NavigationBarScreens(route = "Tips", resourceId =R.string.tips,
        icon = Icons.Outlined.TipsAndUpdates, Icons.Default.TipsAndUpdates)
    object Overview: NavigationBarScreens(route = "Overview",resourceId = R.string.overview,
        icon = Icons.Outlined.PieChart, iconSelected = Icons.Default.PieChart)
}
