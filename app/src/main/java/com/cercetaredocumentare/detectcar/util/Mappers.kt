package com.cercetaredocumentare.detectcar.util

import android.util.Log

fun Int.mapPredictionToBrand(): Brand? {
    Log.d("Brand", "mapPredictionToBrand: $this")
    Brand.entries.forEach {
        if (it.id == this)
            return it
    }

    return null
}