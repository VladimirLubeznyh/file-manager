package ru.lyubeznyh.filemanager.di.module

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.lyubeznyh.filemanager.data.database.AppDataBase
import ru.lyubeznyh.filemanager.data.database.FileDao
import javax.inject.Singleton

@Module
class DataBaseModule {

    @Provides
    @Singleton
    fun provideFileDataBase(context: Context): AppDataBase =
        Room.databaseBuilder(context, AppDataBase::class.java, "filesHashCodeDB").build()

    @Provides
    @Singleton
    fun providesPhotoDao(db: AppDataBase): FileDao {
        return db.fileDao()
    }
}
