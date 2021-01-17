package com.example.kotlinmessenger.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//User model class
@Parcelize
class User(val uid: String, val username: String, val profileImageUrl: String) : Parcelable {
    constructor() : this("", "", "")
}