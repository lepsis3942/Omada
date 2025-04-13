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
    private var paginatedDataState =
        PaginatedDataState(
            imageList = listOf<Image>(),
            currPageNum = 0,
            pageSize = 25,
            totalItems = 0,
            dataContext = ImageListContext.Recent
        )

    private val uiStateFlow: MutableStateFlow<HomeScreenState> =
        MutableStateFlow<HomeScreenState>(HomeScreenState.Loading)
    val uiState: StateFlow<HomeScreenState> = uiStateFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = HomeScreenState.Loading
    )

    init {
        viewModelScope.launch {
            processNewPaginatedState(paginatedDataState.copy(currPageNum = 1))
        }
    }

    fun loadMoreItems() {
        if (paginatedDataState.totalItems == paginatedDataState.imageList.size) return
        viewModelScope.launch {
            processNewPaginatedState(
                paginatedDataState.let {
                    it.copy(currPageNum = it.currPageNum + 1)
                }
            )
        }
    }

    private suspend fun processNewPaginatedState(newPaginatedState: PaginatedDataState) {
        var newState = newPaginatedState
        if (paginatedDataState.dataContext != newState.dataContext) {
            uiStateFlow.emit(HomeScreenState.Loading)
            // Reset pagination state
            newState = newState.copy(
                imageList = listOf(),
                currPageNum = 1,
                totalItems = 0
            )
        }

        val newImagesResult = when (newState.dataContext) {
            ImageListContext.Recent -> imageRepository.getRecentPhotos(
                newPaginatedState.currPageNum,
                newPaginatedState.pageSize
            )

            is ImageListContext.Search -> TODO()
        }

        val paginatedImages = newImagesResult.getOrNull()
        if (paginatedImages == null) {
            // TODO: display error in snackbar
            return
        }
        paginatedDataState = paginatedDataState.let {
            it.copy(
                imageList = it.imageList + paginatedImages.items,
                currPageNum = newPaginatedState.currPageNum,
                totalItems = paginatedImages.total,
                dataContext = newPaginatedState.dataContext
            )
        }
        uiStateFlow.emit(
            HomeScreenState.ImagesLoaded(
                paginatedDataState.imageList.toImmutableList(),
                canLoadMoreImages = paginatedDataState.totalItems > paginatedDataState.imageList.size
            )
        )
    }
}

sealed class HomeScreenState {
    object Loading : HomeScreenState()
    data class ImagesLoaded(val imageList: ImmutableList<Image>, val canLoadMoreImages: Boolean) :
        HomeScreenState()
}

data class PaginatedDataState(
    val imageList: List<Image>,
    val currPageNum: Int,
    val pageSize: Int,
    val totalItems: Int,
    val dataContext: ImageListContext
)

sealed class ImageListContext {
    object Recent : ImageListContext()
    data class Search(val searchText: String) : ImageListContext()
}