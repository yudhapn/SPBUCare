package com.pertamina.spbucare.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderVolume(
        @SerializedName("premium")
        var premium: Int = 0,
        @SerializedName("biosolar")
        var biosolar: Int = 0,
        @SerializedName("pertamax")
        var pertamax: Int = 0,
        @SerializedName("pertalite")
        var pertalite: Int = 0,
        @SerializedName("dexlite")
        var dexlite: Int = 0,
        @SerializedName("pertadex")
        var pertadex: Int = 0,
        @SerializedName("pxturbo")
        var pxturbo: Int = 0
) : Parcelable