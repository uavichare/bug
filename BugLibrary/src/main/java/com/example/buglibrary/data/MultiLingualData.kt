package com.example.buglibrary.data

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class MultiLingualData(
    val en: String,
    val ar: String? = null
) : Serializable, Parcelable {
    val arabic get() = ar ?: en

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(en)
        parcel.writeString(ar)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MultiLingualData> {
        override fun createFromParcel(parcel: Parcel): MultiLingualData {
            return MultiLingualData(parcel)
        }

        override fun newArray(size: Int): Array<MultiLingualData?> {
            return arrayOfNulls(size)
        }
    }
}