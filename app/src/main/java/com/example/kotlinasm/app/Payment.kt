package com.example.kotlinasm.app

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.assigmentkotlin.api.ApiClient
import com.example.kotlinasm.model.ChiTietGioHang
import com.example.kotlinasm.model.ChiTietHoaDon
import com.example.kotlinasm.model.HoaDon
import com.example.kotlinasm.model.Products
import com.example.kotlinasm.ui.theme.KotlinASMTheme
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Random


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(navController: NavController, totalAmount: Float) {
    val context = LocalContext.current
    val sharedPreferences =
        remember { context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE) }
    val userId = sharedPreferences.getString("user_id", null)
    val apiKey = "42334ca8-99e0-4935-91d7-ee568d5b3f6a"

    var address by remember { mutableStateOf("") }
    var chiTietGioHangList by remember { mutableStateOf<List<ChiTietGioHang>>(emptyList()) }

    LaunchedEffect(userId) {
        if (userId != null) {
            fetchGioHang(userId, apiKey) { gioHangList ->
                if (gioHangList?.isNotEmpty() == true) {
                    val idGioHang = gioHangList[0]._id
                    fetchChiTietGioHang(idGioHang, apiKey) { chiTietGioHangListResponse ->
                        if (chiTietGioHangListResponse != null) {
                            chiTietGioHangList = chiTietGioHangListResponse
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Checkout") })
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                ShippingAddressSection(address, onAddressChange = { address = it })
                PaymentSection()
                DeliveryMethodSection()
                OrderSummarySection(totalAmount)
                Spacer(modifier = Modifier.weight(1f))
                SubmitOrderButton(address = address, userId = userId, chiTietGioHangList = chiTietGioHangList,navController)
            }
        }
    )
}



@Composable
fun ShippingAddressSection(address: String, onAddressChange: (String) -> Unit) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Shipping Address", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = address,
                onValueChange = onAddressChange,
                label = { Text("Address Line") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun PaymentSection() {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Payment Method", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Cash on Delivery")
        }
    }
}

@Composable
fun DeliveryMethodSection() {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Delivery Method", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("DHL - Express (2-3 days)", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun OrderSummarySection(totalAmount: Float) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Order Summary", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            OrderSummaryItem(label = "Order:", amount = "$${"%.2f".format(totalAmount )}")
            OrderSummaryItem(label = "Shipping:", amount = "$5.00")
            Divider(modifier = Modifier.padding(vertical = 4.dp))
            OrderSummaryItem(
                label = "Total:",
                amount = "$${"%.2f".format(totalAmount + 5)}",
                isTotal = true
            )
        }
    }
}

@Composable
fun OrderSummaryItem(label: String, amount: String, isTotal: Boolean = false) {
    val textStyle =
        if (isTotal) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = textStyle)
        Text(amount, style = textStyle)
    }
}

@Composable
fun SubmitOrderButton(
    address: String,
    userId: String?,
    chiTietGioHangList: List<ChiTietGioHang>,
    navController: NavController
) {
    val context = LocalContext.current


    Button(
        onClick = {
            if (address.isBlank()) {
                Toast.makeText(context, "Please enter a shipping address", Toast.LENGTH_SHORT).show()
            } else if (userId != null) {
                addHoaDon(userId, address, chiTietGioHangList)
                navController.navigate("SuccessScreen")
                Toast.makeText(context, "Purchase successful!", Toast.LENGTH_SHORT).show()
            }
        },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text("Submit Order")
    }
}

fun addHoaDon(userId: String, address: String, chiTietGioHangList: List<ChiTietGioHang>) {
    val random = Random()
    val randomInt = random.nextInt()
    val idHoaDon = randomInt.toString()
    val hoaDon = HoaDon("", idHoaDon, userId, "Processing", address, "")

    ApiClient.apiService.creatHoaDonGmail(hoaDon).enqueue(object : Callback<HoaDon> {
        override fun onResponse(call: Call<HoaDon>, response: Response<HoaDon>) {
            if (response.isSuccessful) {
                Log.d("HUY", "Order created successfully: ${response.body()}")
                addCTHD(idHoaDon, chiTietGioHangList)
            } else {
                Log.d("HUY", "Failed to create order: ${response.message()}")
            }
        }

        override fun onFailure(call: Call<HoaDon>, t: Throwable) {
            Log.d("HUY", "Error creating order", t)
        }
    })
}

fun addCTHD(idHoaDon: String, chiTietGioHangList: List<ChiTietGioHang>) {
    for (chiTietGioHang in chiTietGioHangList) {
        val chiTietHoaDon = ChiTietHoaDon(
            "",
            idHoaDon,
            chiTietGioHang.chiTietSanPhamID,
            chiTietGioHang.soLuong,
            chiTietGioHang.donGia,
            chiTietGioHang.tongTien
        )

        ApiClient.apiService.creatCTHD(chiTietHoaDon).enqueue(object : Callback<ChiTietHoaDon> {
            override fun onResponse(call: Call<ChiTietHoaDon>, response: Response<ChiTietHoaDon>) {
                if (response.isSuccessful) {
                    Log.d("HUY", "Chi tiết hóa đơn được tạo thành công: ${response.body()}")
                    removeCTGH(chiTietGioHang.gioHangID)
                } else {
                    Log.d("HUY", "Không thể tạo chi tiết hóa đơn: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ChiTietHoaDon>, t: Throwable) {
                Log.d("HUY", "Lỗi khi tạo chi tiết hóa đơn", t)
            }
        })
    }
}

fun removeCTGH(idGH: String) {
    ApiClient.apiService.deleteCTGHID(idGH).enqueue(object : Callback<ChiTietGioHang> {
        override fun onResponse(call: Call<ChiTietGioHang>, response: Response<ChiTietGioHang>) {

        }

        override fun onFailure(call: Call<ChiTietGioHang>, t: Throwable) {
        }
    })
}




