package com.example.assigmentkotlin.api

import com.example.kotlinasm.model.ChiTietGioHang
import com.example.kotlinasm.model.ChiTietHoaDon
import com.example.kotlinasm.model.ChiTietSanPham
import com.example.kotlinasm.model.GioHang
import com.example.kotlinasm.model.HoaDon
import com.example.kotlinasm.model.Products
import com.example.kotlinasm.model.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // link Api: https://imp-model-widely.ngrok-free.app/api/user/showUser?API_KEY=42334ca8-99e0-4935-91d7-ee568d5b3f6a


    // show user
    @GET("api/user/showUser")
    fun showUser(@Query("API_KEY") API_KEY: String): Call<List<User>>

    // show Products
    @GET("api/products/showProducts")
    fun productsListApi(@Query("API_KEY") apiKey: String): Call<List<Products>>

    // add User
    @POST("api/user/addUser")
    fun creatUser(@Body user: User): Call<User?>

    @POST("api/user/login")
    fun loginUser(@Body user: User): Call<User>

    //forgotpass
    @POST("api/user/forgotpass/{gmail}")
    fun forgotPassword(@Path("gmail") email: String): Call<User>

    @POST("api/user/forgotpassOtp/{gmail}")
    fun forgotPasswordOtp(@Path("gmail") email: String, @Body user: User): Call<User>

    // show ChiTietSanPham
    @GET("api/CTSP/showCTSP")
    fun chiTietSanPhamListApi(@Query("API_KEY") API_KEY: String?): Call<List<ChiTietSanPham>>

    // show ChiTietSanPham ID
    @GET("api/products/showProductsCTSPID/{idChiTietSanPham}")
    fun chitietsanphamID(
        @Path("idChiTietSanPham") idChiTietSanPham: String?,
        @Query("API_KEY") API_KEY: String?
    ): Call<ChiTietSanPham>

    // show Products ID
    @GET("api/products/showProductsID/{id}")
    fun showProductsID(
        @Path("id") id: String?,
        @Query("API_KEY") API_KEY: String?
    ): Call<Products>

    // add Cart
    @POST("api/cart/addCart")
    fun creatCart(@Body gioHang: GioHang): Call<GioHang>

    // add CTGH
    @POST("api/CTGH/addCTGH")
    fun creatChiTietGioHangList(@Body chiTietGioHang: ChiTietGioHang): Call<ChiTietGioHang>

    // check cart
    @GET("api/cart/getCartByUser/{userId}")
    fun getCartByUserId(@Path("userId") userId: String): Call<GioHang>

    // show Cart User
    @GET("api/cart/showGhUser/{userId}")
    fun showGioHangUser(@Path("userId") userId: String?, @Query("API_KEY") API_KEY: String?): Call<List<GioHang>>

    // show CTGH ID Cart
    @GET("api/CTGH/showCtghIdcart/{gioHangID}")
    fun showCtghIDCart(@Path("gioHangID") gioHangID: String, @Query("API_KEY") API_KEY: String?): Call<List<ChiTietGioHang>>

    // update CTGHID
    @PUT("api/CTGH/updateCTGH/{CTGHID}")
    fun updateChiTietGioHang(@Path("CTGHID") CTGHID: String, @Body chiTietGioHang: ChiTietGioHang?): Call<ChiTietGioHang>

    // delete CTGH
    @DELETE("api/CTGH/deleteCTGH/{giohangID}")
    fun deleteCTGH(@Path("giohangID") giohangID: String): Call<ChiTietGioHang>

    // add Hoa Don
    @POST("api/hoaDon/addHoaDon")
    fun creatHoaDon(@Body hoaDon: HoaDon): Call<HoaDon>

    // add Hoa Don
    @POST("api/hoaDon/addHoaDonGmail")
    fun creatHoaDonGmail(@Body hoaDon: HoaDon): Call<HoaDon>

    // add CTHD Hoa Don
    @POST("api/CTHD/addCTHD")
    fun creatCTHD(@Body chiTietHoaDon: ChiTietHoaDon): Call<ChiTietHoaDon>

    // delete CTGHID
    @DELETE("api/CTGH/deleteCTGHid/{giohangID}")
    fun deleteCTGHID(@Path("giohangID") giohangID: String): Call<ChiTietGioHang>

    // show hoa don id user
    @GET("api/hoaDon/showHDID/{userId}")
    fun showHdIdUser(@Path("userId") userId: String?, @Query("API_KEY") API_KEY: String): Call<List<HoaDon>>

    // show cthd id hoa don
    @GET("api/CTHD/showCthdIDhd/{hoaDonID}")
    fun showCTHDidHD(@Path("hoaDonID") hoaDonID: String, @Query("API_KEY") API_KEY: String): Call<List<ChiTietHoaDon>>


    // show user ID
    @GET("api/user/showUserID/{userId}")
    fun showUserId(
        @Path("userId") userId: String?,
        @Query("API_KEY") API_KEY: String?
    ): Call<User>


    @Multipart
    @PUT("api/media/updateUser/{id}")
    fun updateUser(
        @Path("id") userId: String,
        @Part("ten") ten: RequestBody,
        @Part("diaChi") diaChi: RequestBody,
        @Part("gmail") gmail: RequestBody,
        @Part("soDienThoai") soDienThoai: RequestBody,
        @Part file: MultipartBody.Part? = null
    ): Call<User>


    @GET("api/products/find")
    fun findProducts(@Query("ten") ten: String?): Call<List<Products>>


}


