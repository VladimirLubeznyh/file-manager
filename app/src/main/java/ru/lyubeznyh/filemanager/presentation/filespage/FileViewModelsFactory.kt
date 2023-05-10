package ru.lyubeznyh.filemanager.presentation.filespage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import javax.inject.Inject

class FileViewModelsFactory @Inject constructor(private val filesViewModel: FilesViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FilesViewModel::class.java)) {
            return filesViewModel as T
        } else {
            throw IllegalArgumentException("Unknown class name")
        }
    }
}
