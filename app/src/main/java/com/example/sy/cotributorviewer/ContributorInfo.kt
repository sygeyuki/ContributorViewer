package com.example.sy.cotributorviewer

import android.os.Parcel
import android.os.Parcelable

data class ContributorInfo(
        val login: String?,
        val id: String?,
        val node_id: String?,
        val avatar_url: String?,
        val gravatar_id: String?,
        val url: String?,
        val html_url: String?,
        val followers_url: String?,
        val following_url: String?,
        val gists_url: String?,
        val starred_url: String?,
        val subscriptions_url: String?,
        val organizations_url: String?,
        val repos_url: String?,
        val events_url: String?,
        val received_events_url: String?,
        val type: String?,
        val site_admin: Boolean,
        val contributions: Int) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readByte() != 0.toByte(),
            parcel.readInt()) {
    }

    override fun toString(): String {
        val sb = StringBuffer()
        sb.append(login).append(":")
        sb.append(id)
        return sb.toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(login)
        parcel.writeString(id)
        parcel.writeString(node_id)
        parcel.writeString(avatar_url)
        parcel.writeString(gravatar_id)
        parcel.writeString(url)
        parcel.writeString(html_url)
        parcel.writeString(followers_url)
        parcel.writeString(following_url)
        parcel.writeString(gists_url)
        parcel.writeString(starred_url)
        parcel.writeString(subscriptions_url)
        parcel.writeString(organizations_url)
        parcel.writeString(repos_url)
        parcel.writeString(events_url)
        parcel.writeString(received_events_url)
        parcel.writeString(type)
        parcel.writeByte(if (site_admin) 1 else 0)
        parcel.writeInt(contributions)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ContributorInfo> {
        override fun createFromParcel(parcel: Parcel): ContributorInfo {
            return ContributorInfo(parcel)
        }

        override fun newArray(size: Int): Array<ContributorInfo?> {
            return arrayOfNulls(size)
        }
    }
}