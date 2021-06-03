package com.bangkit.dermaapp.retrofit

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @Headers("Authorization: Client-ID 81a44c34bb65c2c")
    @POST("image")
    fun uploadImageImgur(
        @Field("title") title: String?,
        @Field("image") image: String?
    ): Call<ImageResponse>
}