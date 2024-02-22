package and.drew.nkhukumanagement.userinterface.navigation


import and.drew.nkhukumanagement.R
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.ui.graphics.vector.ImageVector

sealed class TabScreens(
    @StringRes val title: Int,
    @StringRes val resourceId: Int,
    val icon: ImageVector
) {
    @RequiresApi(Build.VERSION_CODES.O)
    object Income : TabScreens(
        title = R.string.income,
        resourceId = R.string.income,
        icon = Icons.Default.Money
    )

    @RequiresApi(Build.VERSION_CODES.O)
    object Expense : TabScreens(
        title = R.string.expenses,
        resourceId = R.string.expense,
        icon = Icons.Default.MoneyOff
    )

    object SignIn : TabScreens(
        title = R.string.signin,
        resourceId = R.string.signin,
        icon = Icons.Default.Person
    )

    object SignUp : TabScreens(
        title = R.string.signup,
        resourceId = R.string.signup,
        icon = Icons.Default.PersonAdd
    )
}
