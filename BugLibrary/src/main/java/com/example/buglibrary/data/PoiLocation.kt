package com.example.buglibrary.data

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity

@Entity
data class PoiLocation(
    val lat: Double,
    val lng: Double,
    val openat: String,
    val closeat: String,
    val banner_url: String,
    val rating: Float,
    val email: ArrayList<String>,
    val contact_number: ArrayList<String>,
    val offers: ArrayList<String>,
    val location_text: String,
    val levelid: String,
    val slice: String,
    val terminal: String,
    val nearBy: String,
    val availability: ArrayList<String>,
    var level: String?,
    val sliceid: String,
    @Embedded
    val openClose: PoiTiming?,
    val locationId: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readFloat(),
        parcel.createStringArrayList()!!,
        parcel.createStringArrayList()!!,
        parcel.createStringArrayList()!!,
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.createStringArrayList()!!,
        parcel.readString(),
        parcel.readString().toString(),
        parcel.readParcelable(PoiTiming::class.java.classLoader),
        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(lat)
        parcel.writeDouble(lng)
        parcel.writeString(openat)
        parcel.writeString(closeat)
        parcel.writeString(banner_url)
        parcel.writeFloat(rating)
        parcel.writeStringList(email)
        parcel.writeStringList(contact_number)
        parcel.writeStringList(offers)
        parcel.writeString(location_text)
        parcel.writeString(levelid)
        parcel.writeString(slice)
        parcel.writeString(terminal)
        parcel.writeString(nearBy)
        parcel.writeStringList(availability)
        parcel.writeString(level)
        parcel.writeString(sliceid)
        parcel.writeParcelable(openClose, flags)
        parcel.writeString(locationId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PoiLocation> {
        override fun createFromParcel(parcel: Parcel): PoiLocation {
            return PoiLocation(parcel)
        }

        override fun newArray(size: Int): Array<PoiLocation?> {
            return arrayOfNulls(size)
        }
    }
}