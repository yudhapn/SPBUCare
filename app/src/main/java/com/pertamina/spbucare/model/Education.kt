package com.pertamina.spbucare.model

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@IgnoreExtraProperties
@Parcelize
data class Education(
        @SerializedName("educationId")
        var educationId: String = "",
        @SerializedName("imageUrl")
        var imageUrl: String = "",
        @SerializedName("imageName")
        val imageName: String = "",
        @SerializedName("author")
        val author: String ="",
        @SerializedName("title")
        val title: String = "",
        @SerializedName("description")
        val description: String = "",
        @Exclude
        val files: List<Document> = listOf(),
        @SerializedName("createdOn")
        @ServerTimestamp
        val createdOn: Date = Calendar.getInstance().time
) : Parcelable