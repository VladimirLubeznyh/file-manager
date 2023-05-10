package ru.lyubeznyh.filemanager.domain

import ru.lyubeznyh.filemanager.domain.model.FileModel

interface FilesRepository {

    suspend fun getFilesList(path:String?): List<FileModel>
}
