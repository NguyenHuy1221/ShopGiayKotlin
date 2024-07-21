package com.example.kotlinasm.app

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.assigmentkotlin.api.ApiClient
import com.example.kotlinasm.model.ChiTietGioHang
import com.example.kotlinasm.model.ChiTietSanPham
import com.example.kotlinasm.model.GioHang
import com.example.kotlinasm.model.Products
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Random

@Composable
fun DetailsProduct(navController: NavController, productId: String) {
    val apiKey = "42334ca8-99e0-4935-91d7-ee568d5b3f6a"
    var product by remember { mutableStateOf<Products?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    var showDialog by remember { mutableStateOf(false) }


    LaunchedEffect(productId) {
        ApiClient.apiService.showProductsID(productId, apiKey).enqueue(object : Callback<Products> {
            override fun onResponse(call: Call<Products>, response: Response<Products>) {
                if (response.isSuccessful) {
                    product = response.body()
                    isLoading = false
                } else {
                    errorMessage = "Response error: ${response.code()} - ${response.message()}"
                    isLoading = false
                }
            }

            override fun onFailure(call: Call<Products>, t: Throwable) {
                errorMessage = "API call failed: ${t.message}"
                isLoading = false
            }
        })
    }

    var quantity by remember { mutableStateOf(1) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 60.dp)
        ) {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }

            if (isLoading) {
                Text(text = "Loading...")
            } else if (errorMessage != null) {
                Text(text = errorMessage!!, color = Color.Red)
            } else if (product != null) {
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                ) {
                    Image(
                        painter = rememberImagePainter(data = product!!.hinh),
                        contentDescription = product!!.ten,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product!!.ten,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { if (quantity > 1) quantity-- }) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease")
                        }
                        Text(
                            text = quantity.toString(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { quantity++ }) {
                            Icon(Icons.Default.Add, contentDescription = "Increase")
                        }
                    }
                }

                Text(
                    text = "$ ${product!!.donGiaBan}",
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = product!!.moTa,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Justify
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(color = Color.Black),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(text = "Add to cart", fontSize = 18.sp)
            }

            if (showDialog) {
                product?.let {
                    showSizeColorDialog(it) {
                        showDialog = false
                    }
                }
            }
        }
    }
}


@Composable
fun showSizeColorDialog(products: Products, onDismiss: () -> Unit) {
    val api = "42334ca8-99e0-4935-91d7-ee568d5b3f6a"
    var chiTietSanPhamList by remember { mutableStateOf<List<ChiTietSanPham>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedQuantity by remember { mutableStateOf(1) }
    var selectedColor by remember { mutableStateOf<String?>(null) }
    var selectedSize by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val sharedPreferences =
        remember { context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE) }
    val userId = sharedPreferences.getString("user_id", null)

    LaunchedEffect(products._id) {
        ApiClient.apiService.chiTietSanPhamListApi(api)
            .enqueue(object : Callback<List<ChiTietSanPham>> {
                override fun onResponse(
                    call: Call<List<ChiTietSanPham>>,
                    response: Response<List<ChiTietSanPham>>
                ) {
                    if (response.isSuccessful) {
                        chiTietSanPhamList = response.body()
                        isLoading = false
                    } else {
                        errorMessage = "Response error: ${response.code()} - ${response.message()}"
                        isLoading = false
                    }
                }

                override fun onFailure(call: Call<List<ChiTietSanPham>>, t: Throwable) {
                    errorMessage = "API call failed: ${t.message}"
                    isLoading = false
                }
            })
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(8.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                    AlertDialog(
                        onDismissRequest = onDismiss,
                        confirmButton = {
                            TextButton(onClick = onDismiss) {
                                Text("Close")
                            }
                        },
                        title = { Text(text = "Select Size and Color") },
                        text = {
                            if (isLoading) {
                                Text(text = "Loading...")
                            } else if (errorMessage != null) {
                                Text(text = errorMessage!!, color = Color.Red)
                            } else if (chiTietSanPhamList != null) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp)
                                ) {
                                    LazyColumn {
                                        items(chiTietSanPhamList!!.filter { it.products == products._id }) { chiTietSanPham ->
                                            itemSizeColor(
                                                chiTietSanPham = chiTietSanPham,
                                                onColorSelected = {
                                                    selectedColor = chiTietSanPham.color
                                                },
                                                onSizeSelected = {
                                                    selectedSize = chiTietSanPham.size
                                                },
                                                isSelected = selectedColor == chiTietSanPham.color
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        IconButton(onClick = { if (selectedQuantity > 1) selectedQuantity-- }) {
                                            Icon(
                                                Icons.Default.Remove,
                                                contentDescription = "Decrease"
                                            )
                                        }
                                        Text(
                                            text = selectedQuantity.toString(),
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        IconButton(onClick = { selectedQuantity++ }) {
                                            Icon(Icons.Default.Add, contentDescription = "Increase")
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = {
                                            if (selectedColor != null && selectedSize != null) {
                                                val selectedChiTietSanPham =
                                                    chiTietSanPhamList?.find { it.color == selectedColor && it.size == selectedSize }
                                                selectedChiTietSanPham?.let { chiTietSanPham ->
                                                    if (userId != null) {
                                                        addProducToCart(
                                                            giaBan = products.donGiaBan,
                                                            tongTien = products.donGiaBan * selectedQuantity,
                                                            idChiTietSanPham = chiTietSanPham._id,
                                                            soLuong = selectedQuantity,
                                                            userId = userId
                                                        )
                                                    }
                                                }
                                                Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show()
                                                onDismiss()
                                            } else {
                                                Toast.makeText(context, "Please select color and size", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.Black,
                                            contentColor = Color.White )
                                    ) {
                                        Text("Add to Cart")
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    )
}

fun addProducToCart(
    giaBan: Double,
    tongTien: Double,
    idChiTietSanPham: String,
    soLuong: Int,
    userId: String
) {
    ApiClient.apiService.getCartByUserId(userId).enqueue(object : Callback<GioHang> {
        override fun onResponse(call: Call<GioHang>, response: Response<GioHang>) {
            if (response.isSuccessful && response.body() != null) {
                val existingCart = response.body()
                existingCart?.let {
                    addChiTietGioHang(it._id, giaBan, tongTien, idChiTietSanPham, soLuong)
                }
            } else {
                createNewCart(giaBan, tongTien, idChiTietSanPham, soLuong, userId)
            }
        }

        override fun onFailure(call: Call<GioHang>, t: Throwable) {
            Log.d("HUY", "Failed to fetch cart: ${t.message}")
        }
    })
}

fun createNewCart(
    giaBan: Double,
    tongTien: Double,
    idChiTietSanPham: String,
    soLuong: Int,
    userId: String
) {
    val random = Random()
    val randomInt = random.nextInt()
    val idGioHang = randomInt.toString()
    var gioHang = GioHang("", idGioHang, userId, tongTien)
    ApiClient.apiService.creatCart(gioHang).enqueue(object : Callback<GioHang> {
        override fun onResponse(call: Call<GioHang>, response: Response<GioHang>) {
            if (response.isSuccessful) {
                gioHang = response.body()!!
                Log.d("HUY", "Cart added successfully")
                addChiTietGioHang(idGioHang, giaBan, tongTien, idChiTietSanPham, soLuong)
            } else {
                Log.d("HUY", "Failed to add cart: ${response.message()}")
            }
        }

        override fun onFailure(call: Call<GioHang>, t: Throwable) {
            Log.d("HUY", "Error: ${t.message}")
        }
    })
}

fun addChiTietGioHang(
    idGioHang: String,
    donGia: Double,
    tongTien: Double,
    idChiTietSanPham: String,
    soLuong: Int
) {
    val chiTietGioHang = ChiTietGioHang("", idGioHang, idChiTietSanPham, soLuong, donGia, tongTien)
    ApiClient.apiService.creatChiTietGioHangList(chiTietGioHang)
        .enqueue(object : Callback<ChiTietGioHang> {
            override fun onResponse(
                call: Call<ChiTietGioHang>,
                response: Response<ChiTietGioHang>
            ) {
                if (response.isSuccessful) {
                    Log.d("HUY", "Successfully added cart detail")
                } else {
                    Log.d("HUY", "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ChiTietGioHang>, t: Throwable) {
                Log.d("HUY", "Failed to add cart detail: ${t.message}")
            }
        })
}


@Composable
fun itemSizeColor(
    chiTietSanPham: ChiTietSanPham,
    onSizeSelected: () -> Unit,
    onColorSelected: () -> Unit,
    isSelected: Boolean
) {
    val backgroundColor = if (isSelected) Color(0xFFe0f7fa) else Color.White
    val borderColor = if (isSelected) Color.Red else Color.Gray

    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                onSizeSelected()
                onColorSelected()
            },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Box(
            modifier = Modifier
                .background(color = backgroundColor)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Size: ${chiTietSanPham.size}",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Color: ${chiTietSanPham.color}",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        }
    }
}

