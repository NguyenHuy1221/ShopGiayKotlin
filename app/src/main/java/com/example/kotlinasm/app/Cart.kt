package com.example.kotlinasm.app

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.assigmentkotlin.api.ApiClient
import com.example.kotlinasm.model.ChiTietGioHang
import com.example.kotlinasm.model.ChiTietSanPham
import com.example.kotlinasm.model.GioHang
import com.example.kotlinasm.model.Products
import com.example.kotlinasm.ui.theme.KotlinASMTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Composable
fun CartScreen(navController: NavController) {
    val apiKey = "42334ca8-99e0-4935-91d7-ee568d5b3f6a"
    var promoCode by remember { mutableStateOf("") }
    val context = LocalContext.current
    val sharedPreferences =
        remember { context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE) }
    val userId = sharedPreferences.getString("user_id", null)

    // Biến trạng thái để lưu trữ danh sách các sản phẩm trong giỏ hàng.
    var cartItems by remember { mutableStateOf<List<Pair<Products, ChiTietGioHang>>>(emptyList()) }

    // Bộ nhớ đệm cho sản phẩm để tránh gọi lại API nhiều lần.
    val productCache = mutableMapOf<String, Products>()

    // Biến trạng thái cho tổng số tiền
    var totalAmount by remember { mutableStateOf(0.0) }

    LaunchedEffect(userId) {
        if (userId != null) {
            fetchGioHang(userId, apiKey) { gioHangList ->
                if (gioHangList?.isNotEmpty() == true) {
                    val idGioHang = gioHangList[0]._id
                    Log.d("HUY", idGioHang)

                    fetchChiTietGioHang(idGioHang, apiKey) { chiTietGioHangList ->
                        if (chiTietGioHangList?.isNotEmpty() == true) {
                            val cartItemsList = mutableListOf<Pair<Products, ChiTietGioHang>>()
                            chiTietGioHangList.forEach { chiTietGioHang ->
                                fetchChiTietSanPham(
                                    chiTietGioHang.chiTietSanPhamID,
                                    apiKey
                                ) { chiTietSanPham ->
                                    if (chiTietSanPham != null) {
                                        val productId = chiTietSanPham.products
                                        if (productCache.containsKey(productId)) {
                                            val product = productCache[productId]!!
                                            cartItemsList.add(product to chiTietGioHang)
                                            if (cartItemsList.size == chiTietGioHangList.size) {
                                                cartItems = cartItemsList
                                                // Cập nhật tổng số tiền
                                                totalAmount = cartItemsList.sumOf { it.second.soLuong * it.first.donGiaBan }
                                            }
                                        } else {
                                            fetchProduct(productId, apiKey) { product ->
                                                if (product != null) {
                                                    productCache[productId] = product
                                                    cartItemsList.add(product to chiTietGioHang)
                                                    if (cartItemsList.size == chiTietGioHangList.size) {
                                                        cartItems = cartItemsList
                                                        // Cập nhật tổng số tiền
                                                        totalAmount = cartItemsList.sumOf { it.second.soLuong * it.first.donGiaBan }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "My cart",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(cartItems) { (product, chiTietGioHang) ->
                CartItemView(
                    products = product,
                    chiTietGioHang = chiTietGioHang,
                    onQuantityChange = { newQuantity ->
                        // Handle quantity change here
                        val updatedCartItems = cartItems.toMutableList()
                        val index =
                            updatedCartItems.indexOfFirst { it.second._id == chiTietGioHang._id }
                        if (index != -1) {
                            updatedCartItems[index] =
                                product to chiTietGioHang.copy(soLuong = newQuantity)
                            cartItems = updatedCartItems
                            // Cập nhật tổng số tiền
                            totalAmount = updatedCartItems.sumOf { it.second.soLuong * it.first.donGiaBan }
                        }
                    },
                    onRemoveItem = { cartItemId ->
                        deleteCart(cartItemId) { success ->
                            if (success) {
                                val updatedCartItems = cartItems.filterNot { it.second._id == cartItemId }
                                cartItems = updatedCartItems
                                // Cập nhật tổng số tiền
                                totalAmount = updatedCartItems.sumOf { it.second.soLuong * it.first.donGiaBan }
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = promoCode,
                onValueChange = { promoCode = it },
                placeholder = { Text(text = "Enter your promo code") },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {  },
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Apply Promo Code"
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Total: $${"%.2f".format(totalAmount)}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {navController.navigate("CheckoutScreen/${totalAmount.toFloat()}") },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Check out", fontSize = 18.sp)
        }
    }
}

fun fetchGioHang(userId: String, apiKey: String, onResult: (List<GioHang>?) -> Unit) {
    ApiClient.apiService.showGioHangUser(userId, apiKey).enqueue(object : Callback<List<GioHang>> {
        override fun onResponse(call: Call<List<GioHang>>, response: Response<List<GioHang>>) {
            if (response.isSuccessful) {
                onResult(response.body())
            } else {
                onResult(null)
            }
        }

        override fun onFailure(call: Call<List<GioHang>>, t: Throwable) {
            Log.d("HUY", "Call Api Cart Error")
            onResult(null)
        }
    })
}

fun fetchChiTietGioHang(
    idGioHang: String,
    apiKey: String,
    onResult: (List<ChiTietGioHang>?) -> Unit
) {
    ApiClient.apiService.showCtghIDCart(idGioHang, apiKey)
        .enqueue(object : Callback<List<ChiTietGioHang>> {
            override fun onResponse(
                call: Call<List<ChiTietGioHang>>,
                response: Response<List<ChiTietGioHang>>
            ) {
                if (response.isSuccessful) {
                    onResult(response.body())
                } else {
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<List<ChiTietGioHang>>, t: Throwable) {
                Log.d("HUY", "Call Api CT Cart Error")
                onResult(null)
            }
        })
}

fun fetchChiTietSanPham(
    chiTietSanPhamId: String,
    apiKey: String,
    onResult: (ChiTietSanPham?) -> Unit
) {
    ApiClient.apiService.chitietsanphamID(chiTietSanPhamId, apiKey)
        .enqueue(object : Callback<ChiTietSanPham> {
            override fun onResponse(
                call: Call<ChiTietSanPham>,
                response: Response<ChiTietSanPham>
            ) {
                if (response.isSuccessful) {
                    onResult(response.body())
                } else {
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<ChiTietSanPham>, t: Throwable) {
                Log.d("HUY", "Call Api ChiTietSanPham Error")
                onResult(null)
            }
        })
}

fun fetchProduct(productId: String, apiKey: String, onResult: (Products?) -> Unit) {
    ApiClient.apiService.showProductsID(productId, apiKey).enqueue(object : Callback<Products> {
        override fun onResponse(call: Call<Products>, response: Response<Products>) {
            if (response.isSuccessful) {
                onResult(response.body())
            } else {
                onResult(null)
            }
        }

        override fun onFailure(call: Call<Products>, t: Throwable) {
            Log.d("HUY", "Call Api Product Details Error")
            onResult(null)
        }
    })
}

fun updateCart(
    CTGHID: String,
    chiTietGioHang: ChiTietGioHang?,
    onResult: (ChiTietGioHang?) -> Unit
) {
    ApiClient.apiService.updateChiTietGioHang(CTGHID, chiTietGioHang)
        .enqueue(object : Callback<ChiTietGioHang> {
            override fun onResponse(
                call: Call<ChiTietGioHang>,
                response: Response<ChiTietGioHang>
            ) {
                if (response.isSuccessful) {
                    onResult(response.body())
                } else {
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<ChiTietGioHang>, t: Throwable) {
                Log.d("HUY", "Call Api Update Cart Item Quantity Error")
                onResult(null)
            }
        })
}

fun deleteCart(ctghID: String, onResult: (Boolean) -> Unit) {
    ApiClient.apiService.deleteCTGH(ctghID).enqueue(object : Callback<ChiTietGioHang> {
        override fun onResponse(call: Call<ChiTietGioHang>, response: Response<ChiTietGioHang>) {
            if (response.isSuccessful) {
                onResult(true)
            } else {
                onResult(false)
            }
        }

        override fun onFailure(call: Call<ChiTietGioHang>, t: Throwable) {
            onResult(false)
        }
    })
}



@Composable
fun CartItemView(
    products: Products,
    chiTietGioHang: ChiTietGioHang,
    onQuantityChange: (Int) -> Unit,
    onRemoveItem: (String)  -> Unit
) {
    var quantity by remember { mutableStateOf(chiTietGioHang.soLuong) }
    val totalPrice = quantity * products.donGiaBan
    var showDialog by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = rememberImagePainter(data = products.hinh),
            contentDescription = products.ten,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = products.ten,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$${"%.2f".format(totalPrice)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Green
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    // Giảm số lượng sản phẩm trong giỏ hàng
                    if (quantity > 1) {
                        val newQuantity = quantity - 1
                        // Gọi hàm cập nhật số lượng sản phẩm trong giỏ hàng thông qua API
                        val updatedCartItem = chiTietGioHang.copy(soLuong = newQuantity)
                        updateCart(chiTietGioHang._id, updatedCartItem) {
                            if (it != null) {
                                quantity = newQuantity
                                onQuantityChange(newQuantity)
                            }
                        }
                    }
                }) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease")
                }
                Text(
                    text = chiTietGioHang.soLuong.toString().padStart(2, '0'),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = {
                    // Tăng số lượng sản phẩm trong giỏ hàng
                    val newQuantity = quantity + 1
                    // Gọi hàm cập nhật số lượng sản phẩm trong giỏ hàng thông qua API
                    val updatedCartItem = chiTietGioHang.copy(soLuong = newQuantity)
                    updateCart(chiTietGioHang._id, updatedCartItem) {
                        if (it != null) {
                            // Chỉ cập nhật UI nếu API cập nhật thành công
                            quantity = newQuantity
                            onQuantityChange(newQuantity)
                        }
                    }
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Increase")
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = {
            // Hiển thị hộp thoại xác nhận khi người dùng ấn vào nút xóa
            showDialog = true
        }) {
            Icon(Icons.Default.Close, contentDescription = "Remove")
        }

        // Hộp thoại xác nhận khi showDialog là true
        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    // Ẩn hộp thoại khi người dùng bấm ra ngoài
                    showDialog = false
                },
                title = {
                    Text(text = "Confirmation")
                },
                text = {
                    Text(text = "Are you sure you want to remove this item from your cart?")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            // Ẩn hộp thoại và thực hiện xóa mục
                            showDialog = false
                            onRemoveItem(chiTietGioHang._id)
                        }
                    ) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            // Ẩn hộp thoại khi người dùng ấn nút "No"
                            showDialog = false
                        }
                    ) {
                        Text("No")
                    }
                }
            )
        }
    }
}


