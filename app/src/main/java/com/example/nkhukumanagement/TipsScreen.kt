package com.example.nkhukumanagement

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.nkhukumanagement.userinterface.navigation.NavigationBarScreens

@Composable
fun TipsScreen(modifier: Modifier = Modifier,
               canNavigateBack: Boolean = false
){
    Scaffold (
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(NavigationBarScreens.Tips.resourceId),
                canNavigateBack = canNavigateBack
            )
        }
    ){ innerPadding ->
        Text(text = "Tips",
            modifier = modifier.padding(innerPadding))
    }

}