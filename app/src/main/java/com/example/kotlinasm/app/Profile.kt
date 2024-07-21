package com.example.kotlinasm.app

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.assigmentkotlin.api.ApiClient
import com.example.kotlinasm.R
import com.example.kotlinasm.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {

    val context = LocalContext.current
    val sharedPreferences =
        remember { context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE) }
    val userInfo = remember { mutableStateOf<User?>(null) }
    val userId = sharedPreferences.getString("user_id", null)
    val userImageUrl = remember { mutableStateOf<String?>(null) }

    ApiClient.apiService.showUserId(userId, "").enqueue(object : Callback<User> {
        override fun onResponse(call: Call<User>, response: Response<User>) {
            val user = response.body()
            if (user != null && !user.hinh.isNullOrEmpty()) {
                userImageUrl.value = user.hinh
                userInfo.value = user
            }
        }

        override fun onFailure(call: Call<User>, t: Throwable) {
            // Xử lý lỗi
        }
    })

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Profile") },
                actions = {
                    IconButton(onClick = {  }) {
                        Icon(painter = painterResource(id = R.drawable.icon_sneaker), contentDescription = null)
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = if (!userImageUrl.value.isNullOrEmpty()) {
                        rememberImagePainter(userImageUrl.value!!)
                    } else {
                        painterResource(id = R.drawable.hotgirl)
                    },
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = userInfo.value?.ten ?: "",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = userInfo.value?.gmail ?: "",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                ProfileOptionItem(title = "My orders", subtitle = "Already have 10 orders") { navController.navigate("OrdersScreen")}
                ProfileOptionItem(title = "Shipping Addresses", subtitle = "03 Addresses") { }
                ProfileOptionItem(title = "Payment Method", subtitle = "You have 2 cards") {  }
                ProfileOptionItem(title = "My reviews", subtitle = "Reviews for 5 items") {  }
                ProfileOptionItem(title = "Setting", subtitle = "Notification, Password, FAQ, Contact") { navController.navigate("SettingsScreen") }
                ProfileOptionItem(title = "Log out", subtitle = "") {

                    with(sharedPreferences.edit()) {
                        remove("user_id")
                        apply()
                    }
                    navController.navigate("LoginScren") {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                    }
                }


            }
        }
    )
}

@Composable
fun ProfileOptionItem(title: String, subtitle: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(text = subtitle, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    Divider()
}

