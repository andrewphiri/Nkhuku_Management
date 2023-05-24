//package com.example.nkhukumanagement.navigation
//
//import android.os.Build
//import androidx.annotation.RequiresApi
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import com.example.nkhukumanagement.home.HomeScreen
//import com.example.nkhukumanagement.flock.AddFlockDestination
//import com.example.nkhukumanagement.flock.FlockDetailsDestination
//
//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun RootNavigationGraph(
//    navHostController: NavHostController,
//    modifier: Modifier = Modifier) {
//    NavHost(
//        navController = navHostController,
//        route = GraphRoutes.ROOT,
//        startDestination = GraphRoutes.HOME
//    ) {
//   NkhukuNavGraph(navHostController)
//}
//}