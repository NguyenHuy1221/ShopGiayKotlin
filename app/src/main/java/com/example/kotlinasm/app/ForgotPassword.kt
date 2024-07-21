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
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.assigmentkotlin.api.ApiClient
import com.example.assigmentkotlin.api.ApiClient.apiService
import com.example.assigmentkotlin.api.ApiService
import com.example.kotlinasm.R
import com.example.kotlinasm.model.User
import com.example.kotlinasm.ui.theme.KotlinASMTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date

//@Composable
//fun ForgotPassScreen(navController: NavHostController) {
//    val context = LocalContext.current
//    var email by remember { mutableStateOf("") }
//    Row(
//        modifier = Modifier
//            .padding(16.dp)
//            .fillMaxWidth()
//            .padding(top = 50.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Divider(
//            color = Color.Gray,
//            thickness = 1.dp,
//            modifier = Modifier
//                .weight(1f)
//        )
//
//        Spacer(modifier = Modifier.width(16.dp))
//
//        Image(
//            painter = painterResource(id = R.drawable.icon_sneaker),
//            contentDescription = "Logo",
//            modifier = Modifier
//                .size(60.dp)
//                .clip(CircleShape)
//                .border(1.dp, Color.Gray, CircleShape)
//        )
//
//        Spacer(modifier = Modifier.width(16.dp))
//
//        Divider(
//            color = Color.Gray,
//            thickness = 1.dp,
//            modifier = Modifier
//                .weight(1f)
//        )
//    }
//
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//            .padding(top = 150.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//
////        verticalArrangement = Arrangement.Center
//    ) {
//
//        Text(
//            text = "Forgot Password",
//            fontSize = 24.sp,
//            fontWeight = FontWeight.Bold
//        )
//        OutlinedTextField(
//            value = email,
//            onValueChange = { email = it },
//            label = { Text(text = "Email") })
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Text(
//            text = "Back to sign in",
//            Modifier
//                .padding(top = 13.dp)
//                .clickable { navController.navigate("LoginScren") })
//
//
//        Button(
//            onClick = {
//                if (email.isNotBlank()) {
//                    apiService.forgotPassword(email).enqueue(object : Callback<User> {
//                        override fun onResponse(call: Call<User>, response: Response<User>) {
//                            if (response.isSuccessful) {
//                                Toast.makeText(
//                                    context,
//                                    "Password reset email sent!",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                navController.navigate("EnterOTPScreen/$email")
//                            } else {
//                                Toast.makeText(
//                                    context,
//                                    "Failed to send reset email. Please try again.",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//
//                        override fun onFailure(call: Call<User>, t: Throwable) {
//                            Toast.makeText(
//                                context,
//                                "Failed to send reset email. Please try again.",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    })
//                } else {
//                    Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
//                }
//            },
//            colors = ButtonDefaults.buttonColors(
//                containerColor = Color.Black,
//                contentColor = Color.White
//            ),
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        ) {
//            Text("Send")
//        }
//
//    }
//}
//
//@Composable
//fun EnterOTPScreen(navController: NavHostController, email: String?, apiService: ApiService) {
//    val context = LocalContext.current
//    var otp by remember { mutableStateOf("") }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(
//            text = "Enter OTP",
//            fontSize = 24.sp,
//            fontWeight = FontWeight.Bold
//        )
//        OutlinedTextField(
//            value = otp,
//            onValueChange = { otp = it },
//            label = { Text(text = "OTP") },
//            modifier = Modifier.fillMaxWidth()
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//        Button(
//            onClick = {
//                val user = User(
//                    _id = "",
//                    ten = "",
//                    diaChi = "",
//                    soDienThoai = 0,
//                    gmail = email ?: "",
//                    matKhau = "",
//                    tinhTrang = "",
//                    role = "",
//                    ngayTao = Date(),
//                    otp = otp,
//                    verificationToken = "",
//                    isVerified = false
//                )
//                if (otp.isNotBlank() && email != null) {
//                    apiService.forgotPasswordOtp(email, user).enqueue(object : Callback<User> {
//                        override fun onResponse(call: Call<User>, response: Response<User>) {
//                            if (response.isSuccessful) {
//                                Toast.makeText(
//                                    context,
//                                    "Password reset successfully!",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                navController.navigate("LoginScreen")
//                            } else {
//                                Toast.makeText(
//                                    context,
//                                    "Failed to reset password. Please try again.",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//
//                        override fun onFailure(call: Call<User>, t: Throwable) {
//                            Toast.makeText(
//                                context,
//                                "Failed to reset password. Please try again.",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    })
//                } else {
//                    Toast.makeText(context, "Please enter OTP and ensure email is valid", Toast.LENGTH_SHORT).show()
//                }
//            },
//            colors = ButtonDefaults.buttonColors(
//                containerColor = Color.Black,
//                contentColor = Color.White
//            ),
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        ) {
//            Text("Verify OTP")
//        }
//    }
//}


@Composable
fun ForgotPassScreen(navController: NavHostController, apiService: ApiService) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }

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
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 150.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Forgot Password",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Back to sign in",
            Modifier
                .padding(top = 13.dp)
                .clickable { navController.navigate("LoginScreen") }
        )

        Button(
            onClick = {
                if (email.isNotBlank()) {
                    apiService.forgotPassword(email).enqueue(object : Callback<User> {
                        override fun onResponse(call: Call<User>, response: Response<User>) {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    context,
                                    "Password reset email sent!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigate("EnterOTPScreen/${email}")
                            } else {
                                Toast.makeText(
                                    context,
                                    "Failed to send reset email. Please try again.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<User>, t: Throwable) {
                            Toast.makeText(
                                context,
                                "Failed to send reset email. Please try again.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                } else {
                    Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
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
            Text("Send")
        }
    }
}

@Composable
fun EnterOTPScreen(navController: NavHostController, email: String?, apiService: ApiService) {
    val context = LocalContext.current
    var otp by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var newPasswordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }

    val newPasswordIcon = if (newPasswordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
    val confirmPasswordIcon = if (confirmPasswordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Enter OTP and New Password",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        OutlinedTextField(
            value = otp,
            onValueChange = { otp = it },
            label = { Text(text = "OTP") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text(text = "New Password") },
            visualTransformation = if (newPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { newPasswordVisibility = !newPasswordVisibility }) {
                    Icon(imageVector = newPasswordIcon, contentDescription = null)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text(text = "Confirm Password") },
            visualTransformation = if (confirmPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisibility = !confirmPasswordVisibility }) {
                    Icon(imageVector = confirmPasswordIcon, contentDescription = null)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (otp.isNotBlank() && newPassword.isNotBlank() && confirmPassword.isNotBlank() && newPassword == confirmPassword) {
                    val user = User(
                        _id = "",
                        hinh ="",
                        ten = "",
                        diaChi = "",
                        soDienThoai = 0,
                        gmail = email ?: "",
                        matKhau = newPassword,
                        tinhTrang = "",
                        role = "",
                        ngayTao = Date(),
                        otp = otp,
                        verificationToken = "",
                        isVerified = false
                    )
                    apiService.forgotPasswordOtp(email ?: "", user).enqueue(object : Callback<User> {
                        override fun onResponse(call: Call<User>, response: Response<User>) {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    context,
                                    "Password reset successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigate("LoginScren")
                            } else {
                                Toast.makeText(
                                    context,
                                    "Failed to reset password. Please try again.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<User>, t: Throwable) {
                            Toast.makeText(
                                context,
                                "Failed to reset password. Please try again.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                } else {
                    Toast.makeText(context, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
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
            Text("Verify OTP and Reset Password")
        }
    }
}

