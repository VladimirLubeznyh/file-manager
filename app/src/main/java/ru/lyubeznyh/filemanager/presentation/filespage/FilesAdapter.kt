package ru.lyubeznyh.filemanager.presentation.filespage

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.view.isGone
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import ru.lyubeznyh.filemanager.R
import ru.lyubeznyh.filemanager.databinding.ItemFileBinding
import ru.lyubeznyh.filemanager.domain.model.FileModel
import ru.lyubeznyh.filemanager.domain.model.FileModelType
import ru.lyubeznyh.filemanager.utilities.toSizeString
import java.text.SimpleDateFormat
import java.util.Locale

const val DATE_FORMAT = "dd/MM/yyyy"

class FilesAdapter(private val onClick: (file: FileModel) -> Unit) :
    ListAdapter<FileModel, FilesViewHolder>(FilesDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilesViewHolder =
        FilesViewHolder(
            ItemFileBinding.bind(
                LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
            ),
            onClick
        )

    override fun onBindViewHolder(holder: FilesViewHolder, position: Int) {
        val file = getItem(position)
        holder.bind(file)
    }

}

class FilesViewHolder(
    private val binding: ItemFileBinding,
    private val onClick: (file: FileModel) -> Unit
) : ViewHolder(binding.root) {

    fun bind(fileModel: FileModel) {
        with(binding) {

            tvFileName.text = fileModel.name
            tvFileDate.text = SimpleDateFormat(DATE_FORMAT, Locale.UK).format(fileModel.date)
            tvFileSize.isGone = fileModel.type == FileModelType.FolderType

            if (fileModel.isModified) ivModified.setColorFilter(
                Color.YELLOW,
                PorterDuff.Mode.SRC_IN
            )
            else ivModified.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN)

            if (fileModel.type != FileModelType.FolderType) tvFileSize.text =
                fileModel.size.toSizeString(itemView.context)

            @DrawableRes
            val icon = when (fileModel.type) {
                FileModelType.ImageType -> R.drawable.ic_image_file
                FileModelType.VideoType -> R.drawable.ic_video_file
                FileModelType.TextType -> R.drawable.ic_text_file
                FileModelType.AudioType -> R.drawable.ic_audio_file
                FileModelType.FolderType -> R.drawable.ic_folder
                FileModelType.UnknownType -> R.drawable.ic_unknown_file
            }

            Glide
                .with(itemView.context)
                .load(icon)
                .into(ivIconFile)
        }
        binding.root.setOnClickListener {
            if (fileModel.type == FileModelType.FolderType) {
                onClick(fileModel)
            }
        }
    }
}

class FilesDiffUtil : DiffUtil.ItemCallback<FileModel>() {

    override fun areItemsTheSame(oldItem: FileModel, newItem: FileModel): Boolean =
        oldItem.name == newItem.name

    override fun areContentsTheSame(oldItem: FileModel, newItem: FileModel): Boolean =
        oldItem == newItem
}
