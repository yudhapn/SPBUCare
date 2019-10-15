package com.pertamina.spbucare.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Question(
        @SerializedName("question")
        val question: String = "",
        @SerializedName("option1")
        val option1: String = "",
        @SerializedName("option2")
        val option2: String = "",
        @SerializedName("option3")
        val option3: String = "",
        @SerializedName("option4")
        val option4: String = "",
        @SerializedName("option5")
        val option5: String = "",
        @SerializedName("option6")
        val option6: String = "",
        @SerializedName("option7")
        val option7: String = "",
        @SerializedName("answer")
        val answer: String = ""
) : Parcelable