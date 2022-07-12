package com.example.buglibrary.data

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
class Poi(
    @PrimaryKey
    @SerializedName("_id")
    val id: String,
    @SerializedName("appname")
    val appName: String,
    val category: String,
    val description: String,
    val keywords: ArrayList<String>,
    var locations: List<PoiLocation>?,
    @SerializedName("logo_url")
    val logoUrl: String,
    val nature: String,
    @SerializedName("location_text")
    val locationText: String,
    @SerializedName("nature_flight")
    val natureFlight: String,
    @SerializedName("nature_presence")
    val naturePresence: String,
    val audioUrl: String,
    val audioListUrl: List<PoiMedia>?,
    val picUrl: List<PoiMedia>?,
    val videoListUrl: List<PoiMedia>?,
    @Embedded
    @SerializedName("poi_multilingual")
    var poiMultilingual: PoiMultilingual?,
    val subcategory: String,
    val isPartner: Boolean = false,
    var isFavourite: Boolean = false

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.createTypedArrayList(PoiLocation),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(PoiMedia)!!,
        parcel.createTypedArrayList(PoiMedia)!!,
        parcel.createTypedArrayList(PoiMedia)!!,
        parcel.readParcelable(PoiMultilingual::class.java.classLoader),
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(appName)
        parcel.writeString(category)
        parcel.writeString(description)
        parcel.writeStringList(keywords)
        parcel.writeTypedList(locations)
        parcel.writeString(logoUrl)
        parcel.writeString(nature)
        parcel.writeString(locationText)
        parcel.writeString(natureFlight)
        parcel.writeString(naturePresence)
        parcel.writeString(audioUrl)
        parcel.writeTypedList(audioListUrl)
        parcel.writeTypedList(picUrl)
        parcel.writeTypedList(videoListUrl)
        parcel.writeParcelable(poiMultilingual, flags)
        parcel.writeString(subcategory)
        parcel.writeByte(if (isPartner) 1 else 0)
        parcel.writeByte(if (isFavourite) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Poi> {
        override fun createFromParcel(parcel: Parcel): Poi {
            return Poi(parcel)
        }

        override fun newArray(size: Int): Array<Poi?> {
            return arrayOfNulls(size)
        }
    }
}