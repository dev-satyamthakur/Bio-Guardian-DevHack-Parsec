package com.satyamthakur.bio_guardian.data.entity


import com.google.gson.annotations.SerializedName

data class FakeApiResponse(
    @SerializedName("body")
    val body: String? = null,
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("userId")
    val userId: Int? = null
)