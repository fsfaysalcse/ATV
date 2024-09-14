package com.swiftie.androidtv.data.model

import android.graphics.drawable.BitmapDrawable

data class AppInfo(
    val label: String,
    val packageName: String,
    val icon: BitmapDrawable?
)
