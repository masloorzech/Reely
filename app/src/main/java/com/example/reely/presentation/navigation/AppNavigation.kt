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
import androidx.navigation.toRoute
import com.example.reely.presentation.ui.screens.home.HomeScreen
import com.example.reely.presentation.ui.screens.list.ListScreen
import com.example.reely.presentation.ui.screens.movies.MovieDetailsScreen
import com.example.reely.presentation.ui.screens.search.SearchScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier
){

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route in listOf(
        HomeScreenRoute::class.qualifiedName,
        SearchScreenRoute::class.qualifiedName,
        ListScreenRoute::class.qualifiedName,
        StatisticsScreenRoute::class.qualifiedName,
        ProfileScreenRoute::class.qualifiedName
    )

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (showBottomBar) {
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
        }
    ){ innerPadding ->

        NavHost(
            navController = navController,
            modifier = if (showBottomBar) {
                Modifier.padding(innerPadding)
            } else {
                Modifier
            },
            startDestination = HomeScreenRoute,
            ){

            composable<HomeScreenRoute>{
                HomeScreen(

                )
            }

            composable<SearchScreenRoute>{
                SearchScreen(
                    viewModel = hiltViewModel(),
                    onMovieClick = { movieId ->
                        navController.navigate(MovieDetailsRoute(movieId))
                    }
                )
            }

            composable<ListScreenRoute>{
                ListScreen(
                    onMovieClick = { movieId ->
                        navController.navigate(MovieDetailsRoute(movieId))
                    }
                )
            }

            composable<MovieDetailsRoute> {
                MovieDetailsScreen(
                    viewModel = hiltViewModel(),
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

        }

    }
}