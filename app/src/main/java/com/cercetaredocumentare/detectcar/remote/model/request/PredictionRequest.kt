package com.cercetaredocumentare.detectcar.remote.model.request

import com.squareup.moshi.Json

data class PredictionRequest(
    @Json
    val id: String,
    @Json(name = "image_data")
    val imgData: ByteArray
)
