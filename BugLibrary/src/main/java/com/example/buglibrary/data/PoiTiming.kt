package com.example.buglibrary.data

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity

@Entity
data class PoiTiming(
    @Embedded
    val Mon: DayTime?,
    @Embedded
    val Tue: DayTime?,
    @Embedded
    val Wed: DayTime?,
    @Embedded
    val Thu: DayTime?,
    @Embedded
    val Fri: DayTime?,
    @Embedded
    val Sat: DayTime?,
    @Embedded
    val Sun: DayTime?,
    @Embedded
    val All: DayTime?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(DayTime::class.java.classLoader),
        parcel.readParcelable(DayTime::class.java.classLoader),
        parcel.readParcelable(DayTime::class.java.classLoader),
        parcel.readParcelable(DayTime::class.java.classLoader),
        parcel.readParcelable(DayTime::class.java.classLoader),
        parcel.readParcelable(DayTime::class.java.classLoader),
        parcel.readParcelable(DayTime::class.java.classLoader),
        parcel.readParcelable(DayTime::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(Mon, flags)
        parcel.writeParcelable(Tue, flags)
        parcel.writeParcelable(Wed, flags)
        parcel.writeParcelable(Thu, flags)
        parcel.writeParcelable(Fri, flags)
        parcel.writeParcelable(Sat, flags)
        parcel.writeParcelable(Sun, flags)
        parcel.writeParcelable(All, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PoiTiming> {
        override fun createFromParcel(parcel: Parcel): PoiTiming {
            return PoiTiming(parcel)
        }

        override fun newArray(size: Int): Array<PoiTiming?> {
            return arrayOfNulls(size)
        }
    }
}

@Entity
data class DayTime(
    val open: String,
    val openat: String,
    val close: String,
    val closeat: String,
    val isTwentyFour: Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(open)
        parcel.writeString(openat)
        parcel.writeString(close)
        parcel.writeString(closeat)
        parcel.writeByte(if (isTwentyFour) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DayTime> {
        override fun createFromParcel(parcel: Parcel): DayTime {
            return DayTime(parcel)
        }

        override fun newArray(size: Int): Array<DayTime?> {
            return arrayOfNulls(size)
        }
    }
}
