package com.pertamina.spbucare.model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Quiz(
        @SerializedName("quizId")
        var quizId: String = "",
        @SerializedName("name")
        val name: String = "",
        @SerializedName("duration")
        val duration: Int = 0,
        @SerializedName("open")
        var open: Boolean = false,
        @SerializedName("complete")
        var complete: Boolean = false,
        @SerializedName("questions")
        val questions: List<Question> = listOf(),
        @SerializedName("createdOn")
        @ServerTimestamp
        val createdOn: Date = Calendar.getInstance().time,
        @SerializedName("beHeldOn")
        @ServerTimestamp
        val beHeldOn: Date = Calendar.getInstance().time
) : Parcelable