package ru.lyubeznyh.filemanager.data.localstoreg

import android.content.Context
import android.os.Environment
import ru.lyubeznyh.filemanager.domain.model.FileModel
import ru.lyubeznyh.filemanager.domain.model.FileModelType
import ru.lyubeznyh.filemanager.utilities.getFileExt
import ru.lyubeznyh.filemanager.utilities.mapToFileModelType
import java.io.File
import java.nio.file.Files
import javax.inject.Inject
import kotlin.io.path.Path

class SearchFiles @Inject constructor(val context: Context) {

    fun getFilesAndFoldersList(path: String?): List<FileModel> {
        val root = File(path ?: Environment.getExternalStorageDirectory().path)
        val list: List<FileModel> = root.listFiles()?.toList()?.map {
            FileModel(
                name = it.name,
                date = it.lastModified(),
                size = Files.size(Path(it.path)),
                path = it.path,
                type = if (it.isDirectory) FileModelType.FolderType
                else it.name.getFileExt().mapToFileModelType()
            )
        } ?: emptyList()
        return list.sortedBy { it.name }
    }
}
