package presentation.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import shoping_by_kmp.shared.generated.resources.*

sealed class BottomNavItem(
    var title: String,
    var icon: @Composable () -> Unit,
    var selectedIcon: @Composable () -> Unit,
    var screen_route: String
) {
    @OptIn(ExperimentalResourceApi::class)
    object Home : BottomNavItem(
        "Home",
        { Icon(painterResource(Res.drawable.home_border), "Home") },
        { Icon(painterResource(Res.drawable.home), "Home") },
        "home"
    )

    @OptIn(ExperimentalResourceApi::class)
    object Cart : BottomNavItem(
        "Cart",
        { Icon(painterResource(Res.drawable.cart_border), "Cart") },
        { Icon(painterResource(Res.drawable.cart), "Cart") },
        "cart"
    )

    @OptIn(ExperimentalResourceApi::class)
    object Profile : BottomNavItem(
        "Profile",
        { Icon(painterResource(Res.drawable.profile_border), "Profile") },
        { Icon(painterResource(Res.drawable.profile), "Profile") },
        "profile"
    )

    @OptIn(ExperimentalResourceApi::class)
    object Map : BottomNavItem(
        "Map",
        { Icon(painterResource(Res.drawable.location), "Map") },
        { Icon(painterResource(Res.drawable.location), "Map") },
        "map"
    )

    @OptIn(ExperimentalResourceApi::class)
    object Admin : BottomNavItem(
        "Admin",
        { Icon(painterResource(Res.drawable.setting), "Admin") },
        { Icon(painterResource(Res.drawable.setting2), "Admin") },
        "admin"
    )
}
