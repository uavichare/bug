package com.example.buglibrary.data

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Furqan on 03-06-2018.
 */
data class NavDetails(
    val direction: String,
    val distanceStr: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(direction)
        parcel.writeString(distanceStr)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NavDetails> {
        override fun createFromParcel(parcel: Parcel): NavDetails {
            return NavDetails(parcel)
        }

        override fun newArray(size: Int): Array<NavDetails?> {
            return arrayOfNulls(size)
        }
    }
}