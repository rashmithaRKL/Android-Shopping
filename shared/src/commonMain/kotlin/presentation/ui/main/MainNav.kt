package presentation.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import common.Context
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
    
    val homeState by homeViewModel.state.collectAsState()
    val cartState by cartViewModel.state.collectAsState()
    val profileState by profileViewModel.state.collectAsState()

    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.screen_route
    ) {
        composable(BottomNavItem.Home.screen_route) {
            HomeScreen(
                state = homeState,
                events = homeViewModel::onTriggerEvent,
                errors = homeViewModel.errors,
                navigateToDetail = { navController.navigate("detail/$it") }
            )
        }
        composable(BottomNavItem.Cart.screen_route) {
            CartScreen(
                state = cartState,
                events = cartViewModel::onTriggerEvent,
                errors = cartViewModel.errors,
                navigateToDetail = { navController.navigate("detail/$it") },
                navigateToCheckout = { navController.navigate("checkout") }
            )
        }
        composable(BottomNavItem.Profile.screen_route) {
            ProfileScreen(
                state = profileState,
                events = profileViewModel::onTriggerEvent,
                errors = profileViewModel.errors,
                navigateToAddress = { navController.navigate("address") },
                navigateToEditProfile = { navController.navigate("edit_profile") },
                navigateToPaymentMethod = { navController.navigate("payment_method") },
                navigateToMyOrders = { navController.navigate("my_orders") },
                navigateToMyCoupons = { navController.navigate("my_coupons") },
                navigateToMyWallet = { navController.navigate("my_wallet") },
                navigateToSettings = { navController.navigate("settings") }
            )
        }
        composable(BottomNavItem.Map.screen_route) {
            MapScreen(context = navController.context)
        }
        composable(BottomNavItem.Admin.screen_route) {
            AdminScreen(context = navController.context)
        }
    }
}
