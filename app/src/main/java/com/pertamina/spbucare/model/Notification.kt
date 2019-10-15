package com.pertamina.spbucare.model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Notification (
    @SerializedName("notificationId")
    val notificationId: String = "",
    @SerializedName("createdOn")
    @ServerTimestamp
    val createdOn: Date = Calendar.getInstance().time,
    @SerializedName("imageUrl")
    val image: String = "",
    @SerializedName("title")
    val title: String= "",
    @SerializedName("body")
    val body: String= "",
    @SerializedName("open")
    val open: Boolean= false,
    @SerializedName("type")
    val type: String= ""): Parcelable