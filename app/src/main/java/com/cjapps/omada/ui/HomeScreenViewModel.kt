package com.cjapps.omada.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjapps.omada.data.IImageRepository
import com.cjapps.omada.data.models.Image
import com.cjapps.omada.data.models.Paginated
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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

    fun updateUserSearchString(queryString: String) {
        val uiState = uiStateFlow.value
        if (uiState !is HomeScreenState.ImagesLoaded || uiState.userSearchString == queryString) return

        uiStateFlow.update {
            if (it is HomeScreenState.ImagesLoaded) {
                it.copy(userSearchString = queryString)
            } else {
                it
            }
        }
        if (queryString.isEmpty()) {
            viewModelScope.launch {
                // Switch back to Recents view
                processNewPaginatedState(paginatedDataState.copy(dataContext = ImageListContext.Recent))
            }
        }
    }

    fun executeSearch() {
        val uiState = uiStateFlow.value
        if (uiState !is HomeScreenState.ImagesLoaded || uiState.userSearchString == "") return
        viewModelScope.launch {
            processNewPaginatedState(paginatedDataState.copy(dataContext = ImageListContext.Search))
        }
    }

    private suspend fun processNewPaginatedState(newPaginatedState: PaginatedDataState) {
        var newState = newPaginatedState
        var searchText =
            (uiStateFlow.value as? HomeScreenState.ImagesLoaded)?.userSearchString ?: ""
        var currentImageList = paginatedDataState.imageList
        if (paginatedDataState.dataContext != newState.dataContext) {
            uiStateFlow.emit(HomeScreenState.Loading)
            // Reset pagination state
            newState = newState.copy(
                imageList = listOf(),
                currPageNum = 1,
                totalItems = 0
            )
            if (newState.dataContext == ImageListContext.Recent) {
                searchText = ""
            }
            currentImageList = newState.imageList
        }

        val newImagesResult = getImagesForContext(
            newState.dataContext,
            newPaginatedState.currPageNum,
            newPaginatedState.pageSize,
            searchText
        )

        val paginatedImages = newImagesResult.getOrNull()
        if (paginatedImages == null) {
            // TODO: display error in snackbar
            return
        }
        paginatedDataState = paginatedDataState.copy(
            imageList = currentImageList + paginatedImages.items,
            currPageNum = newPaginatedState.currPageNum,
            totalItems = paginatedImages.total,
            dataContext = newPaginatedState.dataContext
        )
        uiStateFlow.update {
            HomeScreenState.ImagesLoaded(
                imageList = paginatedDataState.imageList.toImmutableList(),
                canLoadMoreImages = paginatedDataState.totalItems > paginatedDataState.imageList.size,
                userSearchString = searchText
            )
        }
    }

    private suspend fun getImagesForContext(
        context: ImageListContext,
        pageNum: Int,
        pageSize: Int,
        searchText: String = ""
    ): Result<Paginated<Image>> {
        return when (context) {
            ImageListContext.Recent -> imageRepository.getRecentPhotos(
                pageNum,
                pageSize
            )

            ImageListContext.Search -> {
                imageRepository.searchPhotos(
                    searchText,
                    pageNum,
                    pageSize
                )
            }
        }
    }
}

sealed class HomeScreenState {
    object Loading : HomeScreenState()
    data class ImagesLoaded(
        val imageList: ImmutableList<Image>,
        val canLoadMoreImages: Boolean,
        val userSearchString: String
    ) : HomeScreenState()
}

data class PaginatedDataState(
    val imageList: List<Image>,
    val currPageNum: Int,
    val pageSize: Int,
    val totalItems: Int,
    val dataContext: ImageListContext
)

enum class ImageListContext {
    Recent,
    Search
}