package com.pertamina.spbucare.model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Document(
    @SerializedName("documentId")
    var documentId: String = "",
    @SerializedName("documentDownload")
    var documentDownload: String ="",
    @SerializedName("documentName")
    val documentName: String ="",
    @SerializedName("caption")
    val caption: String ="",
    @SerializedName("createdOn")
    @ServerTimestamp
    val createdOn: Date = Calendar.getInstance().time
) : Parcelable