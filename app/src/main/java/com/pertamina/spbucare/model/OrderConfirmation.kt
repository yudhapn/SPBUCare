package com.pertamina.spbucare.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderConfirmation(
    @SerializedName("confirmSR")
    var confirmSR: Boolean = false,
    @SerializedName("confirmSSGA")
    var confirmSSGA: Boolean = false,
    @SerializedName("confirmOH")
    var confirmOH: Boolean = false,
    @SerializedName("confirmPN")
    var confirmPN: Boolean = false,
    @SerializedName("complete")
    var complete: Boolean = false) : Parcelable