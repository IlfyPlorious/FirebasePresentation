package com.cercetaredocumentare.detectcar.remote.model.request

import com.squareup.moshi.Json

data class PredictionReviewRequest(
    @Json
    val review: Boolean,
    @Json
    val prediction: Int,
    @Json(name = "img_id")
    val imageId: String
)
