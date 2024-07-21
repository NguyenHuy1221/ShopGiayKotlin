package com.example.kotlinasm.app

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.assigmentkotlin.api.ApiClient
import com.example.kotlinasm.R
import com.example.kotlinasm.model.User
import java.util.Date
import retrofit2.Callback


import retrofit2.Call
import retrofit2.Response
import java.util.UUID

@Composable
fun RegisterScreen(navController: NavHostController) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }

    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passError by remember { mutableStateOf("") }
    var confirmPassError by remember { mutableStateOf("") }

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validateFields(): Boolean {
        var isValid = true

        if (name.isBlank()) {
            nameError = "Name is required"
            isValid = false
        } else {
            nameError = ""
        }

        if (email.isBlank()) {
            emailError = "Email is required"
            isValid = false
        } else if (!isValidEmail(email)) {
            emailError = "Invalid email format"
            isValid = false
        } else {
            emailError = ""
        }

        if (pass.isBlank()) {
            passError = "Password is required"
            isValid = false
        } else if (pass.length < 6) {
            passError = "Password must be at least 6 characters"
            isValid = false
        } else {
            passError = ""
        }

        if (confirmPass.isBlank()) {
            confirmPassError = "Confirm Password is required"
            isValid = false
        } else if (confirmPass != pass) {
            confirmPassError = "Passwords do not match"
            isValid = false
        } else {
            confirmPassError = ""
        }

        return isValid
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
            modifier = Modifier.weight(1f)
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
            modifier = Modifier.weight(1f)
        )
    }

    Column(
        modifier = Modifier
            .padding(top = 150.dp)
            .padding(16.dp)
    ) {
        Text(
            text = "WELCOME !",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(100.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(text = "Name") },
            isError = nameError.isNotEmpty()
        )
        if (nameError.isNotEmpty()) {
            Text(text = nameError, color = Color.Red, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email") },
            isError = emailError.isNotEmpty()
        )
        if (emailError.isNotEmpty()) {
            Text(text = emailError, color = Color.Red, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            label = { Text(text = "Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            },
            isError = passError.isNotEmpty()
        )
        if (passError.isNotEmpty()) {
            Text(text = passError, color = Color.Red, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = confirmPass,
            onValueChange = { confirmPass = it },
            label = { Text(text = "Confirm Password") },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (confirmPasswordVisible) "Hide password" else "Show password"

                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            },
            isError = confirmPassError.isNotEmpty()
        )
        if (confirmPassError.isNotEmpty()) {
            Text(text = confirmPassError, color = Color.Red, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(25.dp))

        Button(
            onClick = {
                if (validateFields()) {
                    val randomToken = UUID.randomUUID().toString()
                    val user = User(
                        _id = "1",
                        hinh = "",
                        ten = name,
                        diaChi = "daklak",
                        soDienThoai = 1234567890,
                        gmail = email,
                        matKhau = pass,
                        tinhTrang = "hoat dong",
                        role = "user",
                        ngayTao = Date(),
                        otp = null,
                        verificationToken = randomToken,
                        isVerified = false
                    )
                    ApiClient.apiService.creatUser(user).enqueue(object : Callback<User?> {
                        override fun onResponse(call: Call<User?>, response: Response<User?>) {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    context, "Registration successful",
                                    Toast.LENGTH_LONG
                                ).show()
                                navController.navigate("LoginScren")
                            } else {
                                Toast.makeText(
                                    context, "Registration failed",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<User?>, t: Throwable) {
                            Toast.makeText(
                                context, "An error occurred: ${t.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    })
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
            Text("SIGN UP")
        }
        Spacer(modifier = Modifier.height(5.dp))

        Row {
            Text(text = "Already have an account?  ", color = Color.Gray)
            Text(
                text = "SIGN IN", color = Color.Black,
                modifier = Modifier.clickable { navController.navigate("LoginScren") }
            )
        }
    }
}
