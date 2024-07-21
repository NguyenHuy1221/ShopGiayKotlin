package com.example.kotlinasm.app

import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.assigmentkotlin.api.ApiClient
import com.example.kotlinasm.model.ChiTietHoaDon
import com.example.kotlinasm.model.ChiTietSanPham
import com.example.kotlinasm.model.HoaDon
import com.example.kotlinasm.model.Products
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(navController: NavController, hoaDon: HoaDon) {
    var chiTietHoaDons by remember { mutableStateOf<List<ChiTietHoaDon>?>(null) }
    var chiTietSanPhams by remember { mutableStateOf<Map<String, ChiTietSanPham>>(emptyMap()) }
    var products by remember { mutableStateOf<Map<String, Products>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(hoaDon.hoaDonID) {
        hoaDon.hoaDonID?.let {
            ApiClient.apiService.showCTHDidHD(it, "42334ca8-99e0-4935-91d7-ee568d5b3f6a").enqueue(object : Callback<List<ChiTietHoaDon>> {
                override fun onResponse(call: Call<List<ChiTietHoaDon>>, response: Response<List<ChiTietHoaDon>>) {
                    val chiTietHoaDonList = response.body()
                    if (!chiTietHoaDonList.isNullOrEmpty()) {
                        chiTietHoaDons = chiTietHoaDonList
                        chiTietHoaDonList.forEach { chiTiet ->
                            chiTiet.chiTietSanPhamID?.let { id ->
                                fetchChiTietSanPham(id) { chiTietSanPham ->
                                    chiTietSanPhams = chiTietSanPhams + (id to chiTietSanPham)
                                    chiTietSanPham.products?.let { productId ->
                                        fetchProduct(productId) { product ->
                                            products = products + (productId to product)
                                        }
                                    }
                                }
                            }
                        }
                        isLoading = false
                    } else {
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

    Column {
        TopAppBar(
            title = { Text(text = "Order Details") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
        )
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> Text(text = "Loading...", color = Color.Gray)
                errorMessage != null -> Text(text = errorMessage!!, color = Color.Red)
                chiTietHoaDons != null -> {
                    LazyColumn {
                        items(chiTietHoaDons!!) { chiTietHoaDon ->
                            val chiTietSanPham = chiTietSanPhams[chiTietHoaDon.chiTietSanPhamID]
                            val product = chiTietSanPham?.products?.let { products[it] }
                            if (chiTietSanPham != null && product != null) {
                                DetailItem(chiTietHoaDon, chiTietSanPham, product)
                            }
                        }
                    }
                }
            }
        }
    }
}



private fun fetchChiTietSanPham(idChiTietSanPham: String, callback: (ChiTietSanPham) -> Unit) {
    ApiClient.apiService.chitietsanphamID(idChiTietSanPham, "42334ca8-99e0-4935-91d7-ee568d5b3f6a").enqueue(object : Callback<ChiTietSanPham> {
        override fun onResponse(call: Call<ChiTietSanPham>, response: Response<ChiTietSanPham>) {
            response.body()?.let(callback)
        }

        override fun onFailure(call: Call<ChiTietSanPham>, t: Throwable) {
            Log.e("HUY", "Failed to fetch ChiTietSanPham details", t)
        }
    })
}

private fun fetchProduct(productId: String, callback: (Products) -> Unit) {
    ApiClient.apiService.showProductsID(productId, "42334ca8-99e0-4935-91d7-ee568d5b3f6a").enqueue(object : Callback<Products> {
        override fun onResponse(call: Call<Products>, response: Response<Products>) {
            response.body()?.let(callback)
        }

        override fun onFailure(call: Call<Products>, t: Throwable) {
            Log.e("HUY", "Failed to fetch product details", t)
        }
    })
}

@Composable
fun DetailItem(chiTietHoaDon: ChiTietHoaDon, chiTietSanPham: ChiTietSanPham, product: Products) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = product.ten,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = rememberImagePainter(data = product.hinh),
                contentDescription = "Product Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            DetailText(label = "Size", value = chiTietSanPham.size)
            DetailText(label = "Color", value = chiTietSanPham.color)
            DetailText(label = "Quantity", value = chiTietHoaDon.soLuong.toString())
            DetailText(label = "Price", value = "${chiTietHoaDon.donGia} VND")
            DetailText(label = "Total", value = "${chiTietHoaDon.soLuong * chiTietHoaDon.donGia} VND")
        }
    }
}

@Composable
fun DetailText(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = "$label: ",
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

