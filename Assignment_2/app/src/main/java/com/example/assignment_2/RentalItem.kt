package com.example.assignment_2

import android.os.Parcel
import android.os.Parcelable

data class RentalItem(
    val name: String,
    val imageResId: Int,
    val rating: Float,
    val pricePerMonth: Int,
    val features: List<String>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readFloat(),
        parcel.readInt(),
        parcel.createStringArrayList()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(imageResId)
        parcel.writeFloat(rating)
        parcel.writeInt(pricePerMonth)
        parcel.writeStringList(features)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RentalItem> {
        override fun createFromParcel(parcel: Parcel): RentalItem {
            return RentalItem(parcel)
        }

        override fun newArray(size: Int): Array<RentalItem?> {
            return arrayOfNulls(size)
        }
    }
}