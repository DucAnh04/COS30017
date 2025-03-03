package com.example.w6_tutorials


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Result(val opResult: Int): Parcelable {
}