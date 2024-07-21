package com.example.kotlinasm.model

data class ChiTietHoaDon(
    var _id: String,
    val hoaDonID: String ,
    val chiTietSanPhamID: String,
    val soLuong: Int,
    val donGia: Double,
    val tongTien: Double,
)