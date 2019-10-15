package com.pertamina.spbucare.model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Order(
    @SerializedName("orderId")
    var orderId: String = "",
    @SerializedName("createdOn")
    @ServerTimestamp
    val createdOn: Date = Calendar.getInstance().time,
    @SerializedName("modifiedOn")
    @ServerTimestamp
    var modifiedOn: Date = Calendar.getInstance().time,
    @SerializedName("type")
    var type: String = "",
    @SerializedName("adress")
    var adress: String = "",
    @SerializedName("open")
    var open: Boolean = true,
    @SerializedName("soNumber")
    val soNumber: String = "",
    @SerializedName("salesRetailId")
    val salesRetailId: String = "",
    @SerializedName("userId")
    var userId: String = "",
    @SerializedName("manual")
    var manual: Boolean = false,
    @SerializedName("applicantName")
    val applicantName: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("orderVolume")
    var orderVolume: OrderVolume = OrderVolume(),
    @SerializedName("orderConfirmation")
    var orderConfirmation: OrderConfirmation = OrderConfirmation()
) : Parcelable