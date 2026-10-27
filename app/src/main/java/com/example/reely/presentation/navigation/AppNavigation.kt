package com.example.reely.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reely.presentation.ui.screens.home.HomeScreen
import com.example.reely.presentation.ui.screens.list.ListScreen
import com.example.reely.presentation.ui.screens.movies.MovieDetailsScreen
import com.example.reely.presentation.ui.screens.profile.ProfileScreen
import com.example.reely.presentation.ui.screens.search.SearchScreen
import com.example.reely.presentation.ui.screens.statistics.StatisticsScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier
) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        modifier = modifier.fillMaxSize(),
        startDestination = HomeScreenRoute,
    ) {

        composable<HomeScreenRoute> {
            TabScaffold(navController) {
                HomeScreen(
                    viewModel = hiltViewModel(),
                    onMovieClick = { id ->
                        navController.navigate(MovieDetailsRoute(id))
                    },
                    onSeeMoreClick = {
                        navController.navigate(ListScreenRoute) {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }

                            launchSingleTop = true
                        }
                    }
                )
            }
        }

        composable<SearchScreenRoute> {
            TabScaffold(navController) {
                SearchScreen(
                    viewModel = hiltViewModel(),
                    onMovieClick = { movieId ->
                        navController.navigate(MovieDetailsRoute(movieId))
                    }
                )
            }
        }

        composable<ListScreenRoute> {
            TabScaffold(navController) {
                ListScreen(
                    onMovieClick = { movieId ->
                        navController.navigate(MovieDetailsRoute(movieId))
                    }
                )
            }
        }

        composable<MovieDetailsRoute>(
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(250)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(250)
                )
            }
        ) {
            MovieDetailsScreen(
                viewModel = hiltViewModel(),
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<StatisticsScreenRoute> {
            TabScaffold(navController) {
                StatisticsScreen(
                    viewModel = hiltViewModel(),
                    onMovieClick = { movieId ->
                        navController.navigate(
                            MovieDetailsRoute(movieId),
                        )
                    },
                )
            }
        }

        composable<ProfileScreenRoute> {
            TabScaffold(navController) {
                ProfileScreen(
                    viewModel = hiltViewModel()
                )
            }
        }
    }
}

@Composable
private fun TabScaffold(
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                items = listOf(
                    HomeScreenDestination,
                    StatisticsScreenDestination,
                    SearchScreenDestination,
                    ListScreenDestination,
                    ProfileScreenDestination
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            content()
        }
    }
}