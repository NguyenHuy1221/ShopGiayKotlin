package com.example.kotlinasm.model

import java.util.Date

data class User(
    val _id: String,
    val hinh: String,
    val ten: String,
    val diaChi: String,
    val soDienThoai: Int,
    val gmail: String,
    val matKhau: String,
    val tinhTrang: String,
    val role: String,
    val ngayTao: Date = Date(),
    val otp: String? = null,
    val verificationToken: String ,
    val isVerified: Boolean = false
)