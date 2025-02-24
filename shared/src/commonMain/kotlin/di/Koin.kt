package di

import business.core.AppDataStore
import business.core.AppDataStoreManager
import business.core.KtorHttpClient
import business.datasource.network.main.MainService
import business.datasource.network.main.MainServiceImpl
import business.datasource.network.splash.SplashService
import business.datasource.network.splash.SplashServiceImpl
import business.interactors.main.*
import business.interactors.splash.*
import common.Context
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import presentation.SharedViewModel
import presentation.token_manager.TokenManager
import presentation.ui.main.add_address.view_model.AddAddressViewModel
import presentation.ui.main.address.view_model.AddressViewModel
import presentation.ui.main.admin.view_model.AdminViewModel
import presentation.ui.main.cart.view_model.CartViewModel
import presentation.ui.main.categories.view_model.CategoriesViewModel
import presentation.ui.main.checkout.view_model.CheckoutViewModel
import presentation.ui.main.comment.view_model.CommentViewModel
import presentation.ui.main.detail.view_model.DetailViewModel
import presentation.ui.main.edit_profile.view_model.EditProfileViewModel
import presentation.ui.main.home.view_model.HomeViewModel
import presentation.ui.main.map.view_model.MapViewModel
import presentation.ui.main.my_coupons.view_model.MyCouponsViewModel
import presentation.ui.main.my_orders.view_model.MyOrdersViewModel
import presentation.ui.main.notifications.view_model.NotificationsViewModel
import presentation.ui.main.payment_method.view_model.PaymentMethodViewModel
import presentation.ui.main.profile.view_model.ProfileViewModel
import presentation.ui.main.search.view_model.SearchViewModel
import presentation.ui.main.settings.view_model.SettingsViewModel
import presentation.ui.main.wishlist.view_model.WishlistViewModel
import presentation.ui.splash.view_model.LoginViewModel

fun appModule(context: Context) = module {
    single { Json { isLenient = true; ignoreUnknownKeys = true } }
    single { KtorHttpClient.httpClient(get()) }
    single<SplashService> { SplashServiceImpl(get()) }
    single<MainService> { MainServiceImpl(get()) }
    single<AppDataStore> { AppDataStoreManager(context) }
    
    // ViewModels
    factory { SharedViewModel(get()) }
    factory { LoginViewModel(get(), get(), get()) }
    factory { HomeViewModel(get(), get()) }
    factory { AddressViewModel(get()) }
    factory { AddAddressViewModel(get()) }
    factory { CategoriesViewModel(get()) }
    factory { ProfileViewModel(get()) }
    factory { SettingsViewModel(get()) }
    factory { EditProfileViewModel(get(), get(), get()) }
    factory { PaymentMethodViewModel() }
    factory { NotificationsViewModel(get()) }
    factory { MyCouponsViewModel() }
    factory { MyOrdersViewModel(get()) }
    factory { CheckoutViewModel(get(), get(), get()) }
    factory { WishlistViewModel(get(), get()) }
    factory { CartViewModel(get(), get(), get()) }
    factory { DetailViewModel(get(), get(), get()) }
    factory { SearchViewModel(get(), get()) }
    factory { MapViewModel() }
    factory { AdminViewModel() }
    
    // Use Cases
    single { WishListUseCase(get(), get()) }
    single { BasketListUseCase(get(), get()) }
    single { GetProfileUseCase(get(), get()) }
    single { UpdateProfileUseCase(get(), get()) }
    single { TokenManager(get(), get()) }
    single { LogoutUseCase(get()) }
    single { GetEmailFromCacheUseCase(get()) }
    single { GetSearchFilterUseCase(get(), get()) }
    single { SearchUseCase(get(), get()) }
    single { AddCommentUseCase(get(), get()) }
    single { BuyProductUseCase(get(), get()) }
    single { CommentViewModel(get(), get()) }
    single { GetCommentsUseCase(get(), get()) }
    single { GetAddressesUseCase(get(), get()) }
    single { GetOrdersUseCase(get(), get()) }
    single { GetNotificationsUseCase(get(), get()) }
    single { AddAddressUseCase(get(), get()) }
    single { AddBasketUseCase(get(), get()) }
    single { DeleteBasketUseCase(get(), get()) }
    single { LikeUseCase(get(), get()) }
    single { LoginUseCase(get(), get()) }
    single { RegisterUseCase(get(), get()) }
    single { CheckTokenUseCase(get()) }
    single { HomeUseCase(get(), get()) }
    single { ProductUseCase(get(), get()) }
}
