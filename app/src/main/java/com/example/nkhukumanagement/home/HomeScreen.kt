package com.example.nkhukumanagement.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nkhukumanagement.AppViewModelProviders
import com.example.nkhukumanagement.FlockManagementTopAppBar
import com.example.nkhukumanagement.R
import com.example.nkhukumanagement.data.Flock
import com.example.nkhukumanagement.ui.theme.NkhukuManagementTheme
import com.example.nkhukumanagement.userinterface.navigation.NavigationBarScreens
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navigateToAddFlock: () -> Unit,
    navigateToFlockDetails: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProviders.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    Scaffold(
        topBar = {
                 FlockManagementTopAppBar(
                     stringResource(NavigationBarScreens.Home.resourceId),
                     canNavigateBack = false
                 )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToAddFlock,
                modifier = Modifier.navigationBarsPadding(),
                shape = ShapeDefaults.Small,
                containerColor = MaterialTheme.colorScheme.secondary,
                elevation = FloatingActionButtonDefaults.elevation(),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add flock",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
    ) {innerPadding ->
        FlockBody(
            flockList = homeUiState.flockList,
            onItemClick = navigateToFlockDetails,
            modifier = modifier.padding(innerPadding)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FlockBody(
    flockList: List<Flock>,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (flockList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {  Text(
            modifier = modifier.align(Alignment.Center),
            text = stringResource(R.string.no_flocks_description),
            style = MaterialTheme.typography.bodyMedium
        ) }
    } else {
        FlockList(flockList = flockList, onItemClick = {onItemClick(it.id)})
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FlockList(
    flockList: List<Flock>,
    modifier: Modifier = Modifier, onItemClick: (Flock) -> Unit) {
    LazyColumn {
        items(flockList) {flock ->
            FlockCard(flock, onItemClick = onItemClick)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FlockCard(
    flock: Flock,
    onItemClick: (Flock) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier.padding(8.dp)
            .clickable { onItemClick(flock) },
        elevation = CardDefaults.cardElevation(),) {
        Row (
            modifier = Modifier.padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
                ) {
            Column (
                modifier = Modifier.weight(1f).fillMaxHeight()
            ){
                Text(
                    text = "#${flock.id}",
                    style = MaterialTheme.typography.displaySmall,
                    fontStyle = FontStyle.Italic,
                    color = Color.Cyan,
                    modifier = modifier
                        .align(Alignment.Start)
                )
                Image(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    painter = painterResource(flock.imageResourceId),
                    contentDescription = ("Breed is ${flock.breed}. Batch number ${flock.id}"),
                    contentScale = ContentScale.Crop
                )
            }
            Column (
                modifier = Modifier.weight(3f)
            ) {
                Row (verticalAlignment = Alignment.Top) {
                    Text("Breed: ",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                            .padding(2.dp),
                            textAlign = TextAlign.Justify
                    )
                    Text(text = flock.breed,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(2.dp),
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }
}
@Composable
private fun FlockDetails(label: String, entry: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text(
            text = label,
            modifier = Modifier
                .padding(2.dp),
            textAlign = TextAlign.Justify
        )
        Text(
            text = entry,
            modifier = Modifier
                .padding(2.dp),
            textAlign = TextAlign.Start
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun ShowPreview() {
    NkhukuManagementTheme {
        FlockBody(
            listOf(
                Flock(id= 1, breed = "Hybrid", datePlaced = LocalDate.now(), numberOfChicksPlaced = 250,
                    donorFlock = 5, mortality = 0, R.drawable.chicken, culls = 0)
            ),
            onItemClick = {}
        )
    }
}