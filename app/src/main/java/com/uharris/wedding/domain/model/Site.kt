package com.uharris.wedding.domain.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Site(
    @SerializedName("_id") var id: String = "",
    var name: String = "",
    var description: String = "",
    var address: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var phone: String = "",
    var email: String = "",
    var secondPhone: String = "",
    var url: String = "",
    var picture: String = ""
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString() ?: "",
        source.readString() ?: "",
        source.readString() ?: "",
        source.readString() ?: "",
        source.readDouble(),
        source.readDouble(),
        source.readString() ?: "",
        source.readString() ?: "",
        source.readString() ?: "",
        source.readString() ?: "",
        source.readString() ?: ""
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(name)
        writeString(description)
        writeString(address)
        writeDouble(latitude)
        writeDouble(longitude)
        writeString(phone)
        writeString(email)
        writeString(secondPhone)
        writeString(url)
        writeString(picture)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Site> = object : Parcelable.Creator<Site> {
            override fun createFromParcel(source: Parcel): Site = Site(source)
            override fun newArray(size: Int): Array<Site?> = arrayOfNulls(size)
        }
    }
}