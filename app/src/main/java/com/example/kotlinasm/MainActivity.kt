package com.example.kotlinasm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.assigmentkotlin.api.ApiClient
import com.example.kotlinasm.app.CartScreen
import com.example.kotlinasm.app.CategoryScreen
import com.example.kotlinasm.app.CheckoutScreen
import com.example.kotlinasm.app.DetailsProduct
import com.example.kotlinasm.app.EnterOTPScreen
import com.example.kotlinasm.app.ForgotPassScreen
import com.example.kotlinasm.app.HomeScreen
import com.example.kotlinasm.app.LoginScren
import com.example.kotlinasm.app.OrderDetailsScreen
import com.example.kotlinasm.app.OrdersScreen
import com.example.kotlinasm.app.ProductsScreen
import com.example.kotlinasm.app.ProfileScreen
import com.example.kotlinasm.app.RegisterScreen
import com.example.kotlinasm.app.SettingsScreen
import com.example.kotlinasm.app.SuccessScreen
import com.example.kotlinasm.model.HoaDon
import com.example.kotlinasm.model.User
import com.example.kotlinasm.ui.theme.KotlinASMTheme
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val apiService = ApiClient.apiService

            NavHost(navController = navController, startDestination = "LoginScren") {
                composable("LoginScren") { LoginScren(navController) }
                composable("RegisterScreen") { RegisterScreen(navController) }
                composable("HomeScreen") { HomeScreen(navController) }
                composable("CartScreen") { CartScreen(navController) }
                composable("SuccessScreen") { SuccessScreen(navController) }
                composable("ProfileScreen") { ProfileScreen(navController) }
                composable("SettingsScreen") { SettingsScreen(navController) }
                composable("OrdersScreen") { OrdersScreen(navController) }
                composable(
                    route = "CheckoutScreen/{totalAmount}",
                    arguments = listOf(navArgument("totalAmount") { type = NavType.FloatType })
                ) { backStackEntry ->
                    val totalAmount = backStackEntry.arguments?.getFloat("totalAmount") ?: 0f
                    CheckoutScreen(navController, totalAmount)
                }

                composable("ForgotPassScreen") { ForgotPassScreen(navController, apiService) }
                composable("EnterOTPScreen/{email}") { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email")
                    EnterOTPScreen(navController, email, apiService)
                }

                composable("details/{productId}") { backStackEntry ->
                    DetailsProduct(navController,productId = backStackEntry.arguments?.getString("productId") ?: "")
                }

                composable(
                    route = "order_details/{hoaDonJson}",
                    arguments = listOf(navArgument("hoaDonJson") { type = NavType.StringType })
                ) { backStackEntry ->
                    val hoaDonJson = backStackEntry.arguments?.getString("hoaDonJson")
                    val hoaDon = Gson().fromJson(hoaDonJson, HoaDon::class.java)
                    OrderDetailsScreen(navController = navController, hoaDon = hoaDon)
                }


            }

        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KotlinASMTheme {

    }
}



