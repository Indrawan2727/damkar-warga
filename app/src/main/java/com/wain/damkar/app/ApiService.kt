package com.wain.damkar.app

import com.wain.damkar.model.ResponModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    fun register(
            @Field("name") name: String,
            @Field("nik") nik: String,
            @Field("email") email: String,
            @Field("phone") phone: String,
            @Field("level") level: String,
            @Field("password") password: String
    ):Call<ResponModel>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ):Call<ResponModel>

    @FormUrlEncoded
    @POST("update/{id}")
    fun update(
            @Path("id") id: Integer?,
            @Field("name") name: String?,
            @Field("email") email: String?,
            @Field("phone") phone: String?
    ):Call<ResponModel>

    @Multipart
    @POST("store")
    fun laporan(
            @Part( "user_id") user_id: RequestBody,
            @Part( "name") name: RequestBody,
            @Part( "hp") hp: RequestBody,
            @Part("lokasi") lokasi: RequestBody,
            @Part("lat") lat: RequestBody,
            @Part("long") long: RequestBody,
            @Part("kategori") kategori: RequestBody,
            @Part("deskripsi") deskripsi: RequestBody,
            @Part image: MultipartBody.Part,
            @Part("status") status: RequestBody
    ):Call<ResponModel>

    @GET("laporan2")
    fun getHistory(
    ): Call<ResponModel>

    @FormUrlEncoded
    @POST("updatestatus/{id}")
    fun statusasa(
            @Path("id") phone: String?,
            @Field("level") level: String
    ):Call<ResponModel>
}