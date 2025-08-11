package com.cercetaredocumentare.detectcar.remote.model

import com.squareup.moshi.Json

data class Prediction(
    @Json
    val id: String,
    @Json
    val prediction: Int
)
