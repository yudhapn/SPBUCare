package com.pertamina.spbucare.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    @SerializedName("userId")
    var userId: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("operator")
    var operator: Boolean = false,
    @SerializedName("employeeNumber")
    var employeeNumber: String = "",
    @SerializedName("adress")
    var adress: String = "",
    @SerializedName("type")
    var type: String = "",
    @SerializedName("pertamina")
    var pertamina: Boolean = false,
    @SerializedName("profileImage")
    var profileImage: String = "",
    @SerializedName("picName")
    var picName: String = "",
    @SerializedName("phone")
    var phone: String = "",
    @SerializedName("position")
    var position: String = "",
    @SerializedName("token")
    var token: String = "",
    @SerializedName("salesRetailId")
    var salesRetailId: String = ""
) : Parcelable