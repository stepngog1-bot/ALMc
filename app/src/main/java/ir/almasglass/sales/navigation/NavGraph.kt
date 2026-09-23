package ir.almasglass.sales.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import ir.almasglass.sales.components.AppTab
import ir.almasglass.sales.screens.*
import ir.almasglass.sales.viewmodel.SalesViewModel

private object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val REGISTER_SUCCESS = "register_success"
    const val CATEGORIES = "categories"
    const val PRODUCTS = "products"
    const val PRODUCT_DETAIL = "product_detail/{productId}"
    const val CART = "cart"
    const val EDIT_CART_ITEM = "edit_cart_item/{cartItemId}"
    const val CHECKOUT_CONFIRM = "checkout_confirm"
    const val MY_ORDERS = "my_orders"
    const val ORDER_DETAIL = "order_detail/{orderId}"
    const val ACCOUNT = "account"
    const val PHONE_CHANGE = "phone_change"
    const val CHANGE_PASSWORD = "change_password"
    const val ORDER_SUCCESS = "order_success"
    const val CASH_DEMO = "cash_demo"
}

private fun tabRoute(tab: AppTab): String = when (tab) {
    AppTab.STORE -> Routes.CATEGORIES
    AppTab.CART -> Routes.CART
    AppTab.ORDERS -> Routes.MY_ORDERS
    AppTab.ACCOUNT -> Routes.ACCOUNT
}

@Composable
fun AlmasNavGraph(vm: SalesViewModel) {
    val navController = rememberNavController()
    val slideIn = { slideInHorizontally(initialOffsetX = { it }) }
    val slideOut = { slideOutHorizontally(targetOffsetX = { it }) }

    fun goTab(tab: AppTab) {
        navController.navigate(tabRoute(tab)) {
            launchSingleTop = true
        }
    }

    val startDestination = Routes.SPLASH

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { slideIn() },
        exitTransition = { slideOut() },
        popEnterTransition = { slideIn() },
        popExitTransition = { slideOut() }
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                vm = vm,
                onLogin = { navController.navigate(Routes.LOGIN) },
                onRegister = { navController.navigate(Routes.REGISTER) }
            )
        }
        composable(Routes.LOGIN) {
            LoginScreen(
                vm = vm,
                onBack = { navController.navigate(Routes.SPLASH) { popUpTo(Routes.SPLASH) { inclusive = true } } },
                onLoggedIn = { navController.navigate(Routes.CATEGORIES) { popUpTo(Routes.SPLASH) { inclusive = true } } },
                onGoRegister = { navController.navigate(Routes.REGISTER) }
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                vm = vm,
                onBack = { navController.navigate(Routes.SPLASH) { popUpTo(Routes.SPLASH) { inclusive = true } } },
                onRegistered = { navController.navigate(Routes.REGISTER_SUCCESS) }
            )
        }
        composable(Routes.REGISTER_SUCCESS) {
            RegisterSuccessScreen(
                vm = vm,
                onHome = { navController.navigate(Routes.SPLASH) { popUpTo(Routes.SPLASH) { inclusive = true } } }
            )
        }
        composable(Routes.CATEGORIES) {
            CategoriesScreen(
                vm = vm,
                onOpenProducts = { navController.navigate(Routes.PRODUCTS) },
                onTab = { goTab(it) }
            )
        }
        composable(Routes.PRODUCTS) {
            ProductsScreen(
                vm = vm,
                onOpenCategories = { navController.navigate(Routes.CATEGORIES) },
                onOpenProduct = { id -> navController.navigate("product_detail/$id") },
                onSaveDraft = { navController.navigate(Routes.CART) },
                onCheckout = { navController.navigate(Routes.CHECKOUT_CONFIRM) },
                onTab = { goTab(it) }
            )
        }
        composable(
            Routes.PRODUCT_DETAIL,
            arguments = listOf(navArgument("productId") { type = NavType.LongType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
            ProductDetailScreen(
                vm = vm,
                productId = productId,
                onBack = { navController.navigate(Routes.PRODUCTS) },
                onAddedGoCart = { navController.navigate(Routes.CART) },
                onSelectAnother = { navController.navigate(Routes.CATEGORIES) },
                onCheckout = { navController.navigate(Routes.CHECKOUT_CONFIRM) },
                onSaveDraft = { navController.navigate(Routes.CART) }
            )
        }
        composable(Routes.CART) {
            CartScreen(
                vm = vm,
                onEditLine = { id -> navController.navigate("edit_cart_item/$id") },
                onSelectAnother = { navController.navigate(Routes.CATEGORIES) },
                onCheckout = { navController.navigate(Routes.CHECKOUT_CONFIRM) },
                onSaveDraft = { navController.navigate(Routes.CART) },
                onTab = { goTab(it) }
            )
        }
        composable(
            Routes.EDIT_CART_ITEM,
            arguments = listOf(navArgument("cartItemId") { type = NavType.LongType })
        ) { backStackEntry ->
            val cartItemId = backStackEntry.arguments?.getLong("cartItemId") ?: 0L
            EditCartItemScreen(
                vm = vm,
                cartItemId = cartItemId,
                onBack = { navController.navigate(Routes.CART) },
                onSaved = { navController.navigate(Routes.CART) },
                onSelectAnother = { navController.navigate(Routes.CATEGORIES) },
                onCheckout = { navController.navigate(Routes.CHECKOUT_CONFIRM) }
            )
        }
        composable(Routes.CHECKOUT_CONFIRM) {
            CheckoutConfirmScreen(
                vm = vm,
                onBack = { navController.navigate(Routes.CART) },
                onCreditPlaced = { navController.navigate(Routes.ORDER_SUCCESS) },
                onCashPlaced = { navController.navigate(Routes.CASH_DEMO) }
            )
        }
        composable(Routes.MY_ORDERS) {
            MyOrdersScreen(
                vm = vm,
                onOpenOrder = { id -> navController.navigate("order_detail/$id") },
                onTab = { goTab(it) }
            )
        }
        composable(
            Routes.ORDER_DETAIL,
            arguments = listOf(navArgument("orderId") { type = NavType.LongType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getLong("orderId") ?: 0L
            OrderDetailScreen(
                vm = vm,
                orderId = orderId,
                onBack = { navController.navigate(Routes.MY_ORDERS) },
                onBackToStore = { navController.navigate(Routes.CATEGORIES) }
            )
        }
        composable(Routes.ACCOUNT) {
            AccountScreen(
                vm = vm,
                onPhoneChangeRequest = { navController.navigate(Routes.PHONE_CHANGE) },
                onChangePassword = { navController.navigate(Routes.CHANGE_PASSWORD) },
                onLoggedOut = { navController.navigate(Routes.SPLASH) { popUpTo(0) { inclusive = true } } },
                onTab = { goTab(it) }
            )
        }
        composable(Routes.PHONE_CHANGE) {
            PhoneChangeRequestScreen(vm = vm, onDone = { navController.navigate(Routes.ACCOUNT) })
        }
        composable(Routes.CHANGE_PASSWORD) {
            ChangePasswordScreen(vm = vm, onDone = { navController.navigate(Routes.ACCOUNT) })
        }
        composable(Routes.ORDER_SUCCESS) {
            OrderSuccessScreen(
                onViewOrders = { navController.navigate(Routes.MY_ORDERS) { popUpTo(Routes.CATEGORIES) } },
                onBackToStore = { navController.navigate(Routes.CATEGORIES) { popUpTo(Routes.CATEGORIES) { inclusive = true } } }
            )
        }
        composable(Routes.CASH_DEMO) {
            CashPaymentDemoScreen(
                onGateway = { /* Demo only: payment gateway not connected yet. */ },
                onViewOrders = { navController.navigate(Routes.MY_ORDERS) { popUpTo(Routes.CATEGORIES) } },
                onBackToStore = { navController.navigate(Routes.CATEGORIES) { popUpTo(Routes.CATEGORIES) { inclusive = true } } }
            )
        }
    }
}
