package ru.lyubeznyh.filemanager.data

import ru.lyubeznyh.filemanager.data.database.FileDao
import ru.lyubeznyh.filemanager.data.database.FileEntity
import ru.lyubeznyh.filemanager.data.localstoreg.SearchFiles
import ru.lyubeznyh.filemanager.domain.FilesRepository
import ru.lyubeznyh.filemanager.domain.model.FileModel
import javax.inject.Inject


class FilesRepositoryImpl @Inject constructor(
    private val searchFiles: SearchFiles,
    private val db: FileDao
) : FilesRepository {

    /*
    Downloads files from external storage
    compares them with a previously saved hash code
    and sets IsModified = true for models.
    Also updates them in the database.
    */
    override suspend fun getFilesList(path: String?): List<FileModel> {
        val listFiles = searchFiles.getFilesAndFoldersList(path).toMutableList()
        val hashCodsFiles = List(listFiles.size) {
            listFiles[it].path.let { path -> db.getCode(path) }
        }
        saveNewCodesInDataBase(listFiles)
        for (i in listFiles.indices) {
            val file = listFiles[i]
            listFiles[i] = file.copy(isModified = file.hashCode() != hashCodsFiles[i]?.code)
        }
        return listFiles.toList()
    }

    private fun saveNewCodesInDataBase(list: List<FileModel>) {
        list.forEach {
            it.path.let { path -> db.insert(FileEntity(path, it.hashCode())) }
        }
    }
}
