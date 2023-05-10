package ru.lyubeznyh.filemanager.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface FileDao {

    @Query("SELECT * FROM filesHashCode WHERE path = :path")
    fun getCode(path:String):FileEntity?

    @Insert(onConflict = REPLACE)
    fun insert(file:FileEntity)

}
