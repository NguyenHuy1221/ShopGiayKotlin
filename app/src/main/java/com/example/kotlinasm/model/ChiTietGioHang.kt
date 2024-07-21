package com.example.kotlinasm.model

data class ChiTietGioHang(
    var _id: String,
    val gioHangID: String,
    val chiTietSanPhamID: String ,
    val soLuong: Int,
    val donGia: Double,
    val tongTien: Double
)