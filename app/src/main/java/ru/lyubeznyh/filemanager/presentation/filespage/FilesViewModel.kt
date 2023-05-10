package ru.lyubeznyh.filemanager.presentation.filespage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.lyubeznyh.filemanager.domain.FilesRepository
import ru.lyubeznyh.filemanager.domain.model.FileModel
import ru.lyubeznyh.filemanager.domain.model.Result
import ru.lyubeznyh.filemanager.utilities.getFileExt
import javax.inject.Inject

class FilesViewModel @Inject constructor(
    private val repository: FilesRepository,
) : ViewModel() {

    private val _result = MutableStateFlow<Result<List<FileModel>>>(Result.PendingResult())
    val result = _result.asStateFlow()

    private val _sorted = MutableStateFlow<String?>(SORT_ALPHABETICALLY)
    val sorted = _sorted.asStateFlow()

    private val _isReversed = MutableStateFlow(false)
    val isReversed = _isReversed.asStateFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler() { _, throwable ->
        _result.value = Result.ErrorResult(throwable)
    }

    init {
        sort(_sorted.value)
        sorted.onEach(::sort).launchIn(viewModelScope)
        isReversed.onEach { reverse() }.launchIn(viewModelScope)
    }

    fun getFilterModified(): List<FileModel> =
        if (result.value is Result.SuccessResult) {
            (result.value as Result.SuccessResult<List<FileModel>>).data.filter { it.isModified }
        } else emptyList()

    fun getNotFilterModified(): List<FileModel> =
        if (result.value is Result.SuccessResult) {
            (result.value as Result.SuccessResult<List<FileModel>>).data
        } else emptyList()

    //Set the list sorting setting
    fun setSorted(value: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            _sorted.emit(value)
            sort(value)
        }
    }

    //Set the list reversal setting
    fun setReverse(value: Boolean) {
        viewModelScope.launch(coroutineExceptionHandler) {
            _isReversed.emit(value)
        }
    }

    //Upload files from external storage
    fun setFiles(path: String?) {
        viewModelScope.launch(Dispatchers.IO+coroutineExceptionHandler) {
            _result.emit(Result.SuccessResult(repository.getFilesList(path)))
            sort(_sorted.value)
            if (_isReversed.value) reverse()
        }
    }

    fun setError(throwable: Throwable){
        _result.value = Result.ErrorResult(throwable)
    }

    //Sorts depending on the transmitted setting string (FilesViewModel.const)
    private fun sort(sortSetting: String?) {
        viewModelScope.launch(Dispatchers.Default+coroutineExceptionHandler) {
            if (_result.value is Result.SuccessResult) {
                _result.update { result ->
                    result as Result.SuccessResult
                    val list = when (sortSetting) {
                        SORT_ALPHABETICALLY -> result.data.sortedBy { it.name }
                        SORT_CREATION_TIME -> result.data.sortedBy { it.date }
                        SORT_EXTENSION -> result.data.sortedBy { it.path.getFileExt() }
                        SORT_SIZE -> result.data.sortedBy() { it.size }
                        else -> result.data.sortedBy { it.name }
                    }
                    if (isReversed.value) Result.SuccessResult(list.reversed())
                    else Result.SuccessResult(list)
                }
            }
        }
    }

    //Expands the list in flow
    private fun reverse() {
        viewModelScope.launch (coroutineExceptionHandler){
            if (_result.value is Result.SuccessResult<List<FileModel>>) {
                _result.update { data ->
                    data as Result.SuccessResult<List<FileModel>>
                    Result.SuccessResult(data.data.reversed())
                }
            }
        }
    }

    companion object {
        const val SORT_SIZE = "SORT_SIZE"
        const val SORT_ALPHABETICALLY = "SORT_ALPHABETICALLY"
        const val SORT_EXTENSION = "SORT_EXTENSION"
        const val SORT_CREATION_TIME = "SORT_CREATION_TIME"
    }
}

