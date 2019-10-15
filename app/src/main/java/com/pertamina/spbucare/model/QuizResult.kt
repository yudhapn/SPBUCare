package com.pertamina.spbucare.model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class QuizResult(
        @SerializedName("name")
        val name: String = "",
        @SerializedName("score")
        val score: Int = 0
) : Parcelable