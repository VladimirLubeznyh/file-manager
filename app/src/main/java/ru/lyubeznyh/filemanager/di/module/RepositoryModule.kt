package ru.lyubeznyh.filemanager.di.module

import dagger.Binds
import dagger.Module
import ru.lyubeznyh.filemanager.data.FilesRepositoryImpl
import ru.lyubeznyh.filemanager.domain.FilesRepository

@Module
interface RepositoryModule{

    @Binds
    fun bindsFilesRepository(repository: FilesRepositoryImpl): FilesRepository
}
