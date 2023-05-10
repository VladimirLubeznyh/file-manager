package ru.lyubeznyh.filemanager.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FileEntity::class], version = 1)
abstract class AppDataBase : RoomDatabase(){
    abstract fun fileDao(): FileDao
}
