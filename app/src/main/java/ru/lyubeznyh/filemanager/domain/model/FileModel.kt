package ru.lyubeznyh.filemanager.domain.model

data class FileModel(
    val date: Long,
    val name: String,
    val size: Long,
    val path: String,
    val type: FileModelType = FileModelType.UnknownType,
    var isModified: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (other !is FileModel) return false
        return (this.name == other.name && this.type == other.type && this.path == other.path)
    }

    override fun hashCode(): Int {
        return name.hashCode() + (date % 10000000).toInt()
    }
}

sealed class FileModelType() {
    object UnknownType : FileModelType()
    object ImageType : FileModelType()
    object AudioType : FileModelType()
    object VideoType : FileModelType()
    object TextType : FileModelType()
    object FolderType : FileModelType()

    //Popular file permissions divided by category
    companion object {
        val IMAGE_FILE_EXTENSION_LIST = listOf("jpg", "jpeg", "png", "gif", "bmp")
        val AUDIO_FILE_EXTENSION_LIST =
            listOf("m3u", "m4a", "m4p", "mp2", "mp3", "mpga", "ogg", "rmvb", "wav", "wma", "wmv")
        val VIDEO_FILE_EXTENSION_LIST = listOf("mp4", "mpe", "asf", "avi", "mov")
        val TEXT_FILE_EXTENSION_LIST = listOf(
            "xml",
            "txt",
            "sh",
            "rc",
            "prop",
            "log",
            "html",
            "htm",
            "h",
            "cpp",
            "conf",
            "c",
            "pdf",
            "java",
            "docx"
        )
    }
}
