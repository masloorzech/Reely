package com.example.reely.presentation.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.icons.lucide.Bookmark
import com.composables.icons.lucide.ChartBar
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.Users

interface BottomBarDestination {
    val route: Any
    val routeName: String?
    val label: String
    val icon: ImageVector
}

object HomeScreenDestination : BottomBarDestination{
    override val route = HomeScreenRoute
    override val routeName = HomeScreenRoute::class.qualifiedName
    override val label = "Home"
    override val icon = Lucide.House
}

object SearchScreenDestination : BottomBarDestination{
    override val route = SearchScreenRoute
    override val routeName = SearchScreenRoute::class.qualifiedName
    override val label = "Szukaj"
    override val icon = Lucide.Search
}

object ListScreenDestination : BottomBarDestination{
    override val route = ListScreenRoute
    override val routeName = ListScreenRoute::class.qualifiedName
    override val label = "Listy"
    override val icon = Lucide.Bookmark
}

object StatisticsScreenDestination : BottomBarDestination{
    override val route = StatisticsScreenRoute
    override val routeName = StatisticsScreenRoute::class.qualifiedName
    override val label = "Statystyki"
    override val icon = Lucide.ChartBar
}

object ProfileScreenDestination : BottomBarDestination{
    override val route = ProfileScreenRoute
    override val routeName = ProfileScreenRoute::class.qualifiedName
    override val label = "Profil"
    override val icon = Lucide.Users
}