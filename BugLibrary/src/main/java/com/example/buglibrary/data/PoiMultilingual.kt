package com.example.buglibrary.data

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity

@Entity
data class PoiMultilingual(
    @Embedded(prefix = "ar_")
    val ar: Locale?,
    @Embedded
    val en: Locale,
    @Embedded(prefix = "zh_")
    val zh: Locale?

) : Parcelable {

    val arabic: Locale
        get() = ar ?: en
    val chinese: Locale
        get() = zh ?: en

    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Locale::class.java.classLoader)!!,
        parcel.readParcelable(Locale::class.java.classLoader)!!,
        parcel.readParcelable(Locale::class.java.classLoader)!!

    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(ar, flags)
        parcel.writeParcelable(en, flags)
        parcel.writeParcelable(zh, flags)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PoiMultilingual> {
        override fun createFromParcel(parcel: Parcel): PoiMultilingual {
            return PoiMultilingual(parcel)
        }

        override fun newArray(size: Int): Array<PoiMultilingual?> {
            return arrayOfNulls(size)
        }
    }
}

@Entity
data class Locale(
    @ColumnInfo(name = "locale_name")
    var name: String? = null,
    @ColumnInfo(name = "locale_description")
    var description: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Locale> {
        override fun createFromParcel(parcel: Parcel): Locale {
            return Locale(parcel)
        }

        override fun newArray(size: Int): Array<Locale?> {
            return arrayOfNulls(size)
        }
    }
}