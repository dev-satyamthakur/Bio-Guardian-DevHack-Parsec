package com.satyamthakur.bio_guardian.data.entity


import com.google.gson.annotations.SerializedName

data class AnimalPredictionResponse(
    @SerializedName("image")
    val image: Image? = null,
    @SerializedName("predictions")
    val predictions: List<Prediction?>? = null,
    @SerializedName("time")
    val time: Double? = null
) {
    data class Image(
        @SerializedName("height")
        val height: Int? = null,
        @SerializedName("width")
        val width: Int? = null
    )

    data class Prediction(
        @SerializedName("class_id")
        val classId: Int? = null,
        @SerializedName("class")
        val classX: String? = null,
        @SerializedName("confidence")
        val confidence: Double? = null,
        @SerializedName("detection_id")
        val detectionId: String? = null,
        @SerializedName("height")
        val height: Double? = null,
        @SerializedName("width")
        val width: Double? = null,
        @SerializedName("x")
        val x: Double? = null,
        @SerializedName("y")
        val y: Double? = null
    )
}