package com.bangkit.dermaapp.retrofit

import com.google.gson.annotations.SerializedName

data class Data(

	@field:SerializedName("in_most_viral")
	val inMostViral: Boolean,

	@field:SerializedName("ad_type")
	val adType: Int,

	@field:SerializedName("link")
	val link: String,

	@field:SerializedName("description")
	val description: Any,

	@field:SerializedName("section")
	val section: Any,

	@field:SerializedName("title")
	val title: Any,

	@field:SerializedName("type")
	val type: String,

	@field:SerializedName("deletehash")
	val deletehash: String,

	@field:SerializedName("datetime")
	val datetime: Int,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("in_gallery")
	val inGallery: Boolean,

	@field:SerializedName("vote")
	val vote: Any,

	@field:SerializedName("views")
	val views: Int,

	@field:SerializedName("height")
	val height: Int,

	@field:SerializedName("bandwidth")
	val bandwidth: Int,

	@field:SerializedName("nsfw")
	val nsfw: Any,

	@field:SerializedName("is_ad")
	val isAd: Boolean,

	@field:SerializedName("ad_url")
	val adUrl: String,

	@field:SerializedName("tags")
	val tags: List<Any>,

	@field:SerializedName("account_id")
	val accountId: Int,

	@field:SerializedName("size")
	val size: Int,

	@field:SerializedName("width")
	val width: Int,

	@field:SerializedName("account_url")
	val accountUrl: Any,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("animated")
	val animated: Boolean,

	@field:SerializedName("favorite")
	val favorite: Boolean
)