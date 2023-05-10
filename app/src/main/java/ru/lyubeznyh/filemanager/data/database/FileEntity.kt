package ru.lyubeznyh.filemanager.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "filesHashCode")
data class FileEntity(

    @PrimaryKey
    @ColumnInfo
    val path:String,

    @ColumnInfo
    val code:Int
)
