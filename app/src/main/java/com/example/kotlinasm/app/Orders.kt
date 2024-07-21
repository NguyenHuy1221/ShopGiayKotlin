package com.example.kotlinasm.app

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.assigmentkotlin.api.ApiClient
import com.example.kotlinasm.model.ChiTietHoaDon
import com.example.kotlinasm.model.HoaDon
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(navController: NavController) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE) }
    val userId = sharedPreferences.getString("user_id", null)

    val tabs = listOf("Delivered", "Processing", "Canceled")
    Column {
        TopAppBar(
            title = { Text(text = "My order") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
        )

        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.White,
            contentColor = Color.Black
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> DeliveredOrdersTab(navController,userId)
            1 -> ProcessingOrdersTab(navController,userId)
            2 -> CanceledOrdersTab(navController,userId)
        }
    }
}

@Composable
fun showCTHDidHD(navController: NavController, hoaDon: HoaDon) {
    var chiTietHoaDons by remember { mutableStateOf<List<ChiTietHoaDon>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(hoaDon.hoaDonID) {
        hoaDon.hoaDonID?.let {
            ApiClient.apiService.showCTHDidHD(it, "42334ca8-99e0-4935-91d7-ee568d5b3f6a").enqueue(object : Callback<List<ChiTietHoaDon>> {
                override fun onResponse(call: Call<List<ChiTietHoaDon>>, response: Response<List<ChiTietHoaDon>>) {
                    val chiTietHoaDonList = response.body()
                    if (!chiTietHoaDonList.isNullOrEmpty()) {
                        chiTietHoaDons = chiTietHoaDonList
                        isLoading = false
                    }
                }

                override fun onFailure(call: Call<List<ChiTietHoaDon>>, t: Throwable) {
                    Log.e("HUY", "Failed to fetch order details", t)
                    errorMessage = "Failed to fetch order details"
                    isLoading = false
                }
            })
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Loading...", color = Color.Gray)
        }
    } else if (errorMessage != null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = errorMessage!!, color = Color.Red)
        }
    } else {
        chiTietHoaDons?.let {
            OrderCard(navController, hoaDon, it)
        }
    }
}

@Composable
fun OrderCard(navController: NavController, hoaDon: HoaDon, chiTietHoaDons: List<ChiTietHoaDon>) {
    val totalQuantity = chiTietHoaDons.sumOf { it.soLuong }
    val totalAmount = chiTietHoaDons.sumOf { it.soLuong * it.donGia }

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Order No: ${hoaDon.hoaDonID}", fontWeight = FontWeight.Bold)
                Text(text = hoaDon.ngayTao ?: "")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Quantity: $totalQuantity")
            Text(text = "Total Amount: $$totalAmount")
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        val hoaDonJson = Gson().toJson(hoaDon)
                        navController.navigate("order_details/$hoaDonJson")
                    },
                    modifier = Modifier
                        .background(Color.Black)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Detail")
                }

                Text(
                    text = hoaDon.trangThai ?: "Delivered",
                    color = when (hoaDon.trangThai) {
                        "Delivered" -> Color.Green
                        "Processing" -> Color.Blue
                        "Canceled" -> Color.Red
                        else -> Color.Gray
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}



//@Composable
//fun showCTHDidHD(navController: NavController,hoaDon: HoaDon) {
//    var chiTietHoaDons by remember { mutableStateOf<List<ChiTietHoaDon>?>(null) }
//    var isLoading by remember { mutableStateOf(true) }
//    var errorMessage by remember { mutableStateOf<String?>(null) }
//
//    LaunchedEffect(hoaDon.hoaDonID) {
//        hoaDon.hoaDonID?.let {
//            ApiClient.apiService.showCTHDidHD(it, "42334ca8-99e0-4935-91d7-ee568d5b3f6a").enqueue(object : Callback<List<ChiTietHoaDon>> {
//                override fun onResponse(call: Call<List<ChiTietHoaDon>>, response: Response<List<ChiTietHoaDon>>) {
//                    val chiTietHoaDonList = response.body()
//                    if (!chiTietHoaDonList.isNullOrEmpty()) {
//                        chiTietHoaDons = chiTietHoaDonList
//                        isLoading = false
//                    }
//                }
//
//                override fun onFailure(call: Call<List<ChiTietHoaDon>>, t: Throwable) {
//                    Log.e("HUY", "Failed to fetch order details", t)
//                    errorMessage = "Failed to fetch order details"
//                    isLoading = false
//                }
//            })
//        }
//    }
//
//    if (isLoading) {
//        Box(
//            modifier = Modifier.fillMaxSize(),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(text = "Loading...", color = Color.Gray)
//        }
//    } else if (errorMessage != null) {
//        Box(
//            modifier = Modifier.fillMaxSize(),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(text = errorMessage!!, color = Color.Red)
//        }
//    } else {
//        chiTietHoaDons?.let {
//            OrderCard(navController,hoaDon, it)
//        }
//    }
//}

//@Composable
//fun OrderCard(navController: NavController,hoaDon: HoaDon, chiTietHoaDons: List<ChiTietHoaDon>) {
//    val totalQuantity = chiTietHoaDons.sumOf { it.soLuong }
//    val totalAmount = chiTietHoaDons.sumOf { it.soLuong * it.donGia }
//
//    Card(
//        shape = RoundedCornerShape(8.dp),
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp),
//        elevation = CardDefaults.cardElevation(4.dp)
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Text(text = "Order No: ${hoaDon.hoaDonID}", fontWeight = FontWeight.Bold)
//                Text(text = hoaDon.ngayTao ?: "")
//            }
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(text = "Quantity: $totalQuantity")
//            Text(text = "Total Amount: $$totalAmount")
//            Spacer(modifier = Modifier.height(8.dp))
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Button(
//                    onClick = {
//                        val hoaDonJson = Gson().toJson(hoaDon)
//                        navController.navigate("order_details/$hoaDonJson")
//                    },
//                    modifier = Modifier
//                        .background(Color.Black)
//                        .height(40.dp),
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color.Black,
//                        contentColor = Color.White
//                    )
//                ) {
//                    Text(text = "Detail")
//                }
//
//                Text(
//                    text = hoaDon.trangThai ?: "Delivered",
//                    color = Color.Green,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//        }
//    }
//}

//@Composable
//fun DeliveredOrdersTab(navController: NavController,userId: String?) {
//    var orders by remember { mutableStateOf<List<HoaDon>?>(null) }
//    var isLoading by remember { mutableStateOf(true) }
//    var errorMessage by remember { mutableStateOf<String?>(null) }
//
//    LaunchedEffect(userId) {
//        ApiClient.apiService.showHdIdUser(userId, "42334ca8-99e0-4935-91d7-ee568d5b3f6a").enqueue(object : Callback<List<HoaDon>> {
//            override fun onResponse(call: Call<List<HoaDon>>, response: Response<List<HoaDon>>) {
//                val hoaDonList = response.body()
//                if (!hoaDonList.isNullOrEmpty()) {
//                    Log.d("HUY", "size : " + hoaDonList.size)
//                    orders = hoaDonList
//                    isLoading = false
//                }
//            }
//
//            override fun onFailure(call: Call<List<HoaDon>>, t: Throwable) {
//                Log.e("HUY", "Failed to fetch orders", t)
//                errorMessage = "Failed to fetch orders"
//                isLoading = false
//            }
//        })
//    }
//
//    errorMessage?.let {
//        Box(
//            modifier = Modifier.fillMaxSize(),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(text = it, color = Color.Red)
//        }
//    } ?: run {
//        if (!isLoading) {
//            orders?.let {
//                LazyColumn(
//                    modifier = Modifier.fillMaxSize()
//                ) {
//                    items(it) { hoaDon ->
//                        showCTHDidHD(navController,hoaDon)
//                        Spacer(modifier = Modifier.height(8.dp))
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun ProcessingOrdersTab(userId: String?) {
//    // Similar to DeliveredOrdersTab but for processing orders
//}
//
//@Composable
//fun CanceledOrdersTab(userId: String?) {
//    // Similar to DeliveredOrdersTab but for canceled orders
//}

@Composable
fun DeliveredOrdersTab(navController: NavController, userId: String?) {
    OrderTab(navController, userId, "Delivered")
}

@Composable
fun ProcessingOrdersTab(navController: NavController,userId: String?) {
    OrderTab(navController, userId, "Processing")
}

@Composable
fun CanceledOrdersTab(navController: NavController,userId: String?) {
    OrderTab(navController, userId, "Cancelled")
}

@Composable
fun OrderTab(navController: NavController, userId: String?, status: String) {
    var orders by remember { mutableStateOf<List<HoaDon>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userId) {
        ApiClient.apiService.showHdIdUser(userId, "42334ca8-99e0-4935-91d7-ee568d5b3f6a").enqueue(object : Callback<List<HoaDon>> {
            override fun onResponse(call: Call<List<HoaDon>>, response: Response<List<HoaDon>>) {
                val hoaDonList = response.body()
                if (!hoaDonList.isNullOrEmpty()) {
                    orders = hoaDonList.filter { it.trangThai == status }
                    isLoading = false
                }
            }

            override fun onFailure(call: Call<List<HoaDon>>, t: Throwable) {
                Log.e("HUY", "Failed to fetch orders", t)
                errorMessage = "Failed to fetch orders"
                isLoading = false
            }
        })
    }

    errorMessage?.let {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = it, color = Color.Red)
        }
    } ?: run {
        if (!isLoading) {
            orders?.let {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(it) { hoaDon ->
                        showCTHDidHD(navController, hoaDon)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}






