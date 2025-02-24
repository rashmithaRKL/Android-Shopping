package presentation.ui.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.koin.compose.koinInject
import presentation.navigation.BottomNavItem
import presentation.ui.main.admin.AdminScreen
import presentation.ui.main.cart.CartScreen
import presentation.ui.main.home.HomeScreen
import presentation.ui.main.map.MapScreen
import presentation.ui.main.profile.ProfileScreen
import presentation.ui.main.home.view_model.HomeViewModel
import presentation.ui.main.cart.view_model.CartViewModel
import presentation.ui.main.profile.view_model.ProfileViewModel

@Composable
fun MainNav(navController: NavHostController) {
    val homeViewModel: HomeViewModel = koinInject()
    val cartViewModel: CartViewModel = koinInject()
    val profileViewModel: ProfileViewModel = koinInject()

    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.screen_route
    ) {
        composable(BottomNavItem.Home.screen_route) {
            HomeScreen(
                navigateToDetail = { navController.navigate("detail/$it") }
            )
        }
        composable(BottomNavItem.Cart.screen_route) {
            CartScreen(
                navigateToDetail = { navController.navigate("detail/$it") },
                navigateToCheckout = { navController.navigate("checkout") }
            )
        }
        composable(BottomNavItem.Profile.screen_route) {
            ProfileScreen(
                navigateToAddress = { navController.navigate("address") },
                navigateToEditProfile = { navController.navigate("edit_profile") },
                navigateToPaymentMethod = { navController.navigate("payment_method") },
                navigateToMyOrders = { navController.navigate("my_orders") },
                navigateToMyCoupons = { navController.navigate("my_coupons") },
                navigateToSettings = { navController.navigate("settings") }
            )
        }
        composable(BottomNavItem.Map.screen_route) {
            MapScreen()
        }
        composable(BottomNavItem.Admin.screen_route) {
            AdminScreen()
        }
    }
}
