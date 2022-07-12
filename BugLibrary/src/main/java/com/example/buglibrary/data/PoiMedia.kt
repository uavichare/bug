package com.example.buglibrary.data

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity

@Entity
data class PoiMedia(

    val name: String,
    val imgurl: String,
    val originalUrl: String

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(imgurl)
        parcel.writeString(originalUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PoiMedia> {
        override fun createFromParcel(parcel: Parcel): PoiMedia {
            return PoiMedia(parcel)
        }

        override fun newArray(size: Int): Array<PoiMedia?> {
            return arrayOfNulls(size)
        }
    }
}
