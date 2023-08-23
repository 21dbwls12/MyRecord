package com.example.editmyrecord

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Record(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo("title") var title: String? = null,
    @ColumnInfo("place") var place: String,
    @ColumnInfo("date") var date: String,
    @ColumnInfo("mainText") var mainText: String? = null,
    @ColumnInfo("tag") var tag: String? = null,
    @ColumnInfo("photo") var photo: String,
)
