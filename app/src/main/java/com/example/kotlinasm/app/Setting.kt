package com.example.kotlinasm.app

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.assigmentkotlin.api.ApiClient
import com.example.kotlinasm.R
import com.example.kotlinasm.model.User
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


@Composable
fun SettingsScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE) }
    val userId = sharedPreferences.getString("user_id", null)

    LaunchedEffect(userId) {
        ApiClient.apiService.showUserId(userId, "").enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                response.body()?.let { user ->
                    name = user.ten
                    email = user.gmail
                    imageUrl = user.hinh ?: ""
                    phone = user.soDienThoai.toString()
                    address = user.diaChi
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(context, "Failed to load user data: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedImageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        PersonalInformationSection(
            name = name,
            email = email,
            imageUrl = imageUrl,
            phone = phone,
            address = address,
            selectedImageUri = selectedImageUri,
            onNameChange = { newName -> name = newName },
            onGmailChange = { newGmail -> email = newGmail },
            onAddressChange = { newAddress -> address = newAddress },
            onPhoneChange = { newPhone -> phone = newPhone },
            onImageClick = { imagePickerLauncher.launch("image/*") },
            onUpdateClick = {

                val namePart = name.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val emailPart = email.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val phonePart = phone.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val addressPart = address.toRequestBody("multipart/form-data".toMediaTypeOrNull())

                val filePart = selectedImageUri?.let { uri ->
                    val filePath = getRealPathFromURI(context, uri)
                    val file = File(filePath)
                    val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("file", file.name, requestFile)
                }

                ApiClient.apiService.updateUser(
                    userId!!,
                    namePart,
                    addressPart,
                    emailPart,
                    phonePart,
                    filePart
                ).enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Update successful", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        Toast.makeText(context, "Update failed: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        )
    }
}

@Composable
fun PersonalInformationSection(
    name: String,
    email: String,
    imageUrl: String,
    phone: String,
    address: String,
    selectedImageUri: Uri?,
    onNameChange: (String) -> Unit,
    onGmailChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onImageClick: () -> Unit,
    onUpdateClick: () -> Unit,
) {
    Column {
        Text(text = "Personal Information", fontSize = 18.sp, modifier = Modifier.padding(bottom = 8.dp))

        val painter = if (selectedImageUri != null) {
            rememberImagePainter(selectedImageUri)
        } else if (imageUrl.isNotEmpty()) {
            rememberImagePainter(imageUrl)
        } else {
            painterResource(id = R.drawable.hotgirl)
        }

        Image(
            painter = painter,
            contentDescription = "User Image",
            modifier = Modifier
                .padding(start = 115.dp)
                .padding(top = 30.dp)
                .size(100.dp)
                .clip(CircleShape)
                .border(1.dp, Color.Gray, CircleShape)
                .clickable { onImageClick() }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        TextField(
            value = email,
            onValueChange = onGmailChange,
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        TextField(
            value = address,
            onValueChange = onAddressChange,
            label = { Text("Address") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        TextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = { Text("Phone") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Button(
            onClick = onUpdateClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(color = Color.Black),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            ),
        ) {
            Text(text = "Update")
        }
    }
}

fun getRealPathFromURI(context: Context, uri: Uri): String {
    var result: String? = null
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    if (cursor != null) {
        if (cursor.moveToFirst()) {
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            if (idx != -1) {
                result = cursor.getString(idx)
            }
        }
        cursor.close()
    }
    return result ?: uri.path ?: ""
}



