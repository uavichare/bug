package com.example.buglibrary.data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("admin_created")
    val adminCreated: Boolean,
    @SerializedName("appname")
    val appname: String,
    @SerializedName("country_code")
    val countryCode: String,
    @SerializedName("DOB")
    val dOB: String,
    @SerializedName("device_ids")
    val deviceIds: List<String>,
    @SerializedName("deviceType")
    val deviceType: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("_id")
    val id: String,
    @SerializedName("is_admin")
    val isAdmin: Boolean,
    @SerializedName("is_feedbackAdmin")
    val isFeedbackAdmin: Boolean,
    @SerializedName("last_login")
    val lastLogin: Int,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("last_used_ip")
    val lastUsedIp: String,
    @SerializedName("member_since")
    val memberSince: Int,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("profile_completed")
    val profileCompleted: Boolean,
    @SerializedName("profile_pic")
    val profilePic: String,
    @SerializedName("referalId")
    val referalId: String,
    @SerializedName("role")
    val role: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("uniqueId")
    val uniqueId: String,
    @SerializedName("userLang")
    val userLang: String,
    @SerializedName("__v")
    val v: Int,
    @SerializedName("verified")
    val verified: Boolean,
    @SerializedName("user_name")
    val userName: String,
    @SerializedName("user_pic")
    val userPic: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readByte() != 0.toByte(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.createStringArrayList()!!,
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readByte() != 0.toByte(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readString().toString(),
        parcel.readString().toString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (adminCreated) 1 else 0)
        parcel.writeString(appname)
        parcel.writeString(countryCode)
        parcel.writeString(dOB)
        parcel.writeStringList(deviceIds)
        parcel.writeString(deviceType)
        parcel.writeString(email)
        parcel.writeString(firstName)
        parcel.writeString(gender)
        parcel.writeString(id)
        parcel.writeByte(if (isAdmin) 1 else 0)
        parcel.writeByte(if (isFeedbackAdmin) 1 else 0)
        parcel.writeInt(lastLogin)
        parcel.writeString(lastName)
        parcel.writeString(lastUsedIp)
        parcel.writeInt(memberSince)
        parcel.writeString(mobile)
        parcel.writeString(name)
        parcel.writeString(password)
        parcel.writeByte(if (profileCompleted) 1 else 0)
        parcel.writeString(profilePic)
        parcel.writeString(referalId)
        parcel.writeString(role)
        parcel.writeString(status)
        parcel.writeString(type)
        parcel.writeString(uniqueId)
        parcel.writeString(userLang)
        parcel.writeInt(v)
        parcel.writeByte(if (verified) 1 else 0)
        parcel.writeString(userName)
        parcel.writeString(userPic)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}