package com.tuqinghai.bookreader.entity

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson
import com.tuqinghai.crawler.entity.Source
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Unique

@Entity
data class LocalBook(
    @Id var id: Long = 0,
    var name: String,
    @Unique var link: String,
    var readLink: String,
    var cover: String,
    var author: String,
    var desc: String,
    var newChapter: String? = null,
    var newChapterTime: String? = null,
    var newChapterLink: String? = null,
    var readIndex: Int = 0,
    var source: String,
    var inBookshelf: Boolean
) : Parcelable {


    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString()?:"",
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(link)
        parcel.writeString(readLink)
        parcel.writeString(cover)
        parcel.writeString(author)
        parcel.writeString(desc)
        parcel.writeString(newChapter)
        parcel.writeString(newChapterTime)
        parcel.writeString(newChapterLink)
        parcel.writeInt(readIndex)
        parcel.writeString(source)
        parcel.writeByte(if (inBookshelf) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LocalBook> {
        override fun createFromParcel(parcel: Parcel): LocalBook {
            return LocalBook(parcel)
        }

        override fun newArray(size: Int): Array<LocalBook?> {
            return arrayOfNulls(size)
        }
    }

}