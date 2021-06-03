package com.bangkit.dermaapp.retrofit

import com.google.gson.annotations.SerializedName

data class ImageResponse(

	@field:SerializedName("data")
	val data: Data,

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("status")
	val status: Int
)