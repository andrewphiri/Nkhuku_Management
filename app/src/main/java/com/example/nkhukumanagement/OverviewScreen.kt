package com.example.nkhukumanagement


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.nkhukumanagement.userinterface.navigation.NavigationBarScreens
import java.lang.reflect.Modifier

@Composable
fun OverviewScreen(
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    canNavigateBack: Boolean = false
) {
    Scaffold(
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(NavigationBarScreens.Overview.resourceId),
                canNavigateBack = canNavigateBack
            )
        }
    ) { innerPadding ->
        Text(
            text = "Overview",
            modifier = modifier.padding(innerPadding)
        )
    }

}