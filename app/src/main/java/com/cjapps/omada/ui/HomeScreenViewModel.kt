package com.cjapps.omada.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjapps.omada.data.IImageRepository
import com.cjapps.omada.data.models.Image
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val imageRepository: IImageRepository
) : ViewModel() {
    private val pageSize = 25
    private var currentPage = 1
    private val uiStateFlow: MutableStateFlow<HomeScreenState> =
        MutableStateFlow<HomeScreenState>(HomeScreenState.Loading)
    val uiState: StateFlow<HomeScreenState> = uiStateFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = HomeScreenState.Loading
    )

    init {
        viewModelScope.launch {
            val newImagesResult = imageRepository.getRecentPhotos(currentPage, pageSize)
            val paginatedImages = newImagesResult.getOrNull()
            if (paginatedImages == null) {
                // TODO: display error in snackbar
                return@launch
            }
            uiStateFlow.emit(
                HomeScreenState.ImagesLoaded(
                    paginatedImages.items.toImmutableList(),
                    paginatedImages.items.size == paginatedImages.total
                )
            )
        }
    }

}

sealed class HomeScreenState {
    object Loading : HomeScreenState()
    data class ImagesLoaded(val imageList: ImmutableList<Image>, val canLoadMoreImages: Boolean) :
        HomeScreenState()
}