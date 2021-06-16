package com.uharris.wedding.domain.model

import android.os.Parcel
import android.os.Parcelable

data class Wish(
    var user: User? = null,
    var comment: String = ""
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readParcelable<User>(User::class.java.classLoader),
        source.readString() ?: ""
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeParcelable(user, 0)
        writeString(comment)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Wish> = object : Parcelable.Creator<Wish> {
            override fun createFromParcel(source: Parcel): Wish = Wish(source)
            override fun newArray(size: Int): Array<Wish?> = arrayOfNulls(size)
        }
    }
}