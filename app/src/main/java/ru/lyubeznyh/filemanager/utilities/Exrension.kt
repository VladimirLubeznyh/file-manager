package ru.lyubeznyh.filemanager.utilities

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import ru.lyubeznyh.filemanager.R
import ru.lyubeznyh.filemanager.databinding.PageResultBinding
import ru.lyubeznyh.filemanager.domain.model.FileModelType
import ru.lyubeznyh.filemanager.domain.model.Result


fun String.mapToFileModelType(): FileModelType = when {
    FileModelType.IMAGE_FILE_EXTENSION_LIST.contains(this) -> FileModelType.ImageType
    FileModelType.AUDIO_FILE_EXTENSION_LIST.contains(this) -> FileModelType.AudioType
    FileModelType.VIDEO_FILE_EXTENSION_LIST.contains(this) -> FileModelType.VideoType
    FileModelType.TEXT_FILE_EXTENSION_LIST.contains(this) -> FileModelType.TextType
    else -> FileModelType.UnknownType
}

fun String.getFileExt(): String = this.substring(this.lastIndexOf(".") + 1, this.length)

fun Long.toSizeString(context: Context): String = when {
    (this < 1022976) -> context.getString(R.string.number_KB, (this / 1024))
    (this < 1047527424) -> context.getString(R.string.number_MB, (this / 1048576))
    else -> context.getString(R.string.number_G, (this / 1073741824))
}

fun <T> Fragment.simpleRenderResult(
    viewGroup: ViewGroup,
    result: Result<T>,
    onClickUpdate: (() -> Unit)? = null,
    onSuccess: (data: T) -> Unit
) {
    viewGroup.children.forEach { it.visibility = View.GONE }
    val binding = PageResultBinding.bind(viewGroup)
    when (result) {
        is Result.ErrorResult<T> -> {
            binding.llErrorInfo.visibility = View.VISIBLE
            binding.btUpdate.setOnClickListener { onClickUpdate?.invoke() }
        }
        is Result.PendingResult -> {
            binding.pbLoading.visibility = View.VISIBLE
        }
        is Result.SuccessResult -> {
            viewGroup.children.filter {
                it.id != binding.llErrorInfo.id && it.id != binding.pbLoading.id
            }.forEach {
                it.visibility = View.VISIBLE
            }
            onSuccess(result.data)
        }
    }
}
