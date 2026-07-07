package com.example.reely.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.reely.presentation.ui.screens.home.HomeScreen
import com.example.reely.presentation.ui.screens.list.ListScreen
import com.example.reely.presentation.ui.screens.search.SearchScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier
){

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                items = listOf(
                    HomeScreenDestination,
                    SearchScreenDestination,
                    ListScreenDestination,
                    StatisticsScreenDestination,
                    ProfileScreenDestination
                ),
            )
        }
    ){ innerPadding ->

        NavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            startDestination = HomeScreenRoute,
            ){

            composable<HomeScreenRoute>{
                HomeScreen(

                )
            }

            composable<SearchScreenRoute>{
                SearchScreen(
                    viewModel = hiltViewModel()
                )
            }

            composable<ListScreenRoute>{
                ListScreen()
            }

            composable<MovieDetailsRoute>{ movieId ->


            }

        }

    }
}