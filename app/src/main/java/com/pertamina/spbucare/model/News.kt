package com.pertamina.spbucare.model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class News(
    @SerializedName("newsId")
    var newsId: String = "",
    @SerializedName("imageUrl")
    var imageUrl: String ="",
    @SerializedName("imageName")
    val imageName: String = "",
    @SerializedName("title")
    val title: String ="",
    @SerializedName("author")
    val author: String ="",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("document")
    var document: Document = Document(),
    @SerializedName("createdOn")
    @ServerTimestamp
    val createdOn: Date = Calendar.getInstance().time
) : Parcelable