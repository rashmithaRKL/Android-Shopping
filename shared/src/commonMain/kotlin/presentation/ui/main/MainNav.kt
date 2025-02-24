package presentation.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import common.Context
import presentation.navigation.BottomNavItem
import presentation.ui.main.admin.AdminScreen
import presentation.ui.main.cart.CartScreen
import presentation.ui.main.home.HomeScreen
import presentation.ui.main.map.MapScreen
import presentation.ui.main.profile.ProfileScreen

@Composable
fun MainNav(
    navController: NavHostController,
    context: Context
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.screen_route
    ) {
        composable(BottomNavItem.Home.screen_route) {
            HomeScreen(context = context)
        }
        composable(BottomNavItem.Cart.screen_route) {
            CartScreen(context = context)
        }
        composable(BottomNavItem.Profile.screen_route) {
            ProfileScreen(context = context)
        }
        composable(BottomNavItem.Map.screen_route) {
            MapScreen(context = context)
        }
        composable(BottomNavItem.Admin.screen_route) {
            AdminScreen(context = context)
        }
    }
}
