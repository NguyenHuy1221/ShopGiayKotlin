package com.example.kotlinasm.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.assigmentkotlin.api.ApiClient
import com.example.kotlinasm.R
import com.example.kotlinasm.model.CategoryItem
import com.example.kotlinasm.model.Products
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    var selectedItemIndex by rememberSaveable { mutableStateOf(1) }
    var showSearchField by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val items = listOf(
        BottomNavigationItem(
            title = "Cart",
            selectedIcon = Icons.Filled.ShoppingCart,
            unselectedIcon = Icons.Outlined.ShoppingCart,
            hasNews = false,
        ),
        BottomNavigationItem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            hasNews = false,
        ),
        BottomNavigationItem(
            title = "Settings",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            hasNews = false,
        ),
    )

    Column(
        modifier = Modifier
            .padding(top = 40.dp)
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showSearchField) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(text = "Search products...") }
                )
                IconButton(
                    onClick = {
                        showSearchField = false
                        searchQuery = ""
                    },
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            } else {
                IconButton(
                    onClick = { showSearchField = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = Color.Gray)
                }
                Column {
                    Text(
                        text = "Make home",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Light,
                        color = Color.Gray
                    )
                    Text(
                        text = "BEAUTIFUL",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(
                    onClick = {
                        navController.navigate("CartScreen")
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Cart", tint = Color.Gray)
                }
            }
        }

        ProductsScreen(navController, searchQuery)

        NavigationBar {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = selectedItemIndex == index,
                    onClick = {
                        selectedItemIndex = index
                        when (index) {
                            0 -> navController.navigate("CartScreen")
                            1 -> navController.navigate("HomeScreen")
                            2 -> navController.navigate("ProfileScreen")
                        }
                    },
                    label = { Text(text = item.title) },
                    alwaysShowLabel = false,
                    icon = {
                        BadgedBox(
                            badge = {
                                if (item.badgeCount != null) {
                                    Badge { Text(text = item.badgeCount.toString()) }
                                } else if (item.hasNews) {
                                    Badge()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (index == selectedItemIndex) {
                                    item.selectedIcon
                                } else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        }
                    }
                )
            }
        }
    }
}



@Composable
fun CategoryScreen() {
    val itemsList = listOf(
        CategoryItem(R.drawable.icon_start, "Popular"),
//        CategoryItem(R.drawable.ic_launcher_foreground, "New"),
//        CategoryItem(R.drawable.ic_launcher_background, "Trending"),
//        CategoryItem(R.drawable.ic_launcher_foreground, "Featured"),
//        CategoryItem(R.drawable.ic_launcher_background, "Top"),
//        CategoryItem(R.drawable.ic_launcher_foreground, "Recommended")
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(itemsList) { item ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = item.imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .clip(CutCornerShape(5.dp))
                        .width(50.dp)
                        .height(50.dp)
                        .background(color = Color.Gray)
                )
                Text(text = item.text)
            }
        }
    }
}


@Composable
fun ProductsScreen(navController: NavController, searchQuery: String) {
    val apiKey = "42334ca8-99e0-4935-91d7-ee568d5b3f6a"
    var products by remember { mutableStateOf<List<Products>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(searchQuery) {
        ApiClient.apiService.findProducts(searchQuery).enqueue(object : Callback<List<Products>> {
            override fun onResponse(call: Call<List<Products>>, response: Response<List<Products>>) {
                if (response.isSuccessful) {
                    products = response.body()
                } else {
                    errorMessage = "Response error: ${response.code()} - ${response.message()}"
                }
            }

            override fun onFailure(call: Call<List<Products>>, t: Throwable) {
                errorMessage = "API call failed: ${t.message}"
            }
        })
    }

    Column(
        modifier = Modifier
            .height(603.dp)
            .padding(16.dp)
    ) {
        if (products != null) {
            LazyColumn {
                items(products!!.chunked(2)) { productRow ->
                    LazyRow {
                        items(productRow) { product ->
                            ProductItem(product) {
                                navController.navigate("details/${product._id}")
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                        }
                    }
                }
            }
        } else if (errorMessage != null) {
            Text(text = errorMessage!!, color = Color.Red)
        } else {
            Text(text = "Loading...")
        }
    }
}


@Composable
fun ProductItem(product: Products,onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .width(156.dp)
                .height(253.dp)
        ) {
            Image(
                painter = rememberImagePainter(data = product.hinh),
                contentDescription = "Product Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.padding(top = 5.dp))
            Text(
                text = product.ten,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Price: ${product.donGiaBan}",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        Spacer(modifier = Modifier.padding(top = 5.dp))
    }
}
