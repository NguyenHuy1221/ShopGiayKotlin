package com.example.kotlinasm.app

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.assigmentkotlin.api.ApiClient
import com.example.kotlinasm.R
import com.example.kotlinasm.model.User
import com.example.kotlinasm.ui.theme.KotlinASMTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date

@Composable
fun LoginScren(navController: NavHostController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var myState by remember { mutableStateOf(false) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }


    var users by remember { mutableStateOf<List<User>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val apiKey = "42334ca8-99e0-4935-91d7-ee568d5b3f6a"

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            ApiClient.apiService.showUser(apiKey).enqueue(object : Callback<List<User>> {
                override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                    if (response.isSuccessful) {
                        users = response.body()
                    } else {
                        errorMessage = "Response error: ${response.code()} - ${response.message()}"
                    }
                }

                override fun onFailure(call: Call<List<User>>, t: Throwable) {
                    errorMessage = "API call failed: ${t.message}"
                }
            })
        }
    }

    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .padding(top = 50.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier
                .weight(1f)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Image(
            painter = painterResource(id = R.drawable.icon_sneaker),
            contentDescription = "Logo",
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .border(1.dp, Color.Gray, CircleShape)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Divider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier
                .weight(1f)
        )
    }

    Column(
        modifier = Modifier
            .padding(top = 150.dp)
            .padding(16.dp)
    ) {
        Text(
            text = "HELLO !",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "WELCOME BACK",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(200.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            label = { Text(text = "Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                // Please provide localized description for accessibility services
                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, description)
                }
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 28.dp),
            horizontalArrangement = Arrangement.Start
        ) {

            Checkbox(
                checked = myState,
                onCheckedChange = { myState = it }
            )
            Text(text = "Remember me", Modifier.padding(top = 13.dp))

        }


        Button(
            onClick = {
                val user = users?.find { it.gmail == email && it.matKhau == pass }
                if (user != null){
                    if (user != null && user.isVerified) {

                        with(sharedPreferences.edit()) {
                            putString("user_id", user._id)
                            putString("user_name",user.ten)
                            putString("user_gmail",user.gmail)
                            apply()
                        }

                        navController.navigate("HomeScreen")
                        Toast.makeText(context, "Login successful", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Account not verified or invalid", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(context, "Invalid email or password", Toast.LENGTH_LONG).show()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Log in")
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 28.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "SIGN UP",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .padding(end = 5.dp)
                    .clickable { navController.navigate("RegisterScreen") })
            Text(
                text = "Forgot Password",
                color = Color.Gray,
                modifier = Modifier.clickable { navController.navigate("ForgotPassScreen") })

        }


    }
}


@Composable
fun UsersScreen() {
    val apiKey = "42334ca8-99e0-4935-91d7-ee568d5b3f6a"
    var users by remember { mutableStateOf<List<User>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            ApiClient.apiService.showUser(apiKey).enqueue(object : Callback<List<User>> {
                override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                    if (response.isSuccessful) {
                        users = response.body()
                    } else {
                        errorMessage = "Response error: ${response.code()} - ${response.message()}"
                    }
                }

                override fun onFailure(call: Call<List<User>>, t: Throwable) {
                    errorMessage = "API call failed: ${t.message}"
                }
            })
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (users != null) {
            LazyColumn {
                items(users!!) { user ->
                    UserItem(user)
                }
            }
        } else if (errorMessage != null) {
            Text(text = errorMessage!!, color = androidx.compose.ui.graphics.Color.Red)
        } else {
            Text(text = "Loading...")
        }
    }
}

@Composable
fun UserItem(user: User) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = user.ten,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Email: ${user.gmail}",
            fontSize = 14.sp,
            color = androidx.compose.ui.graphics.Color.Gray
        )
    }
}





