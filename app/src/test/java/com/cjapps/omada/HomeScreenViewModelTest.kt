package com.cjapps.omada

import MainDispatcherRule
import com.cjapps.omada.data.IImageRepository
import com.cjapps.omada.data.models.Image
import com.cjapps.omada.data.models.Paginated
import com.cjapps.omada.ui.HomeScreenState
import com.cjapps.omada.ui.HomeScreenViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.sql.Date

@OptIn(ExperimentalCoroutinesApi::class)
class HomeScreenViewModelTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    lateinit var mockImageRepository: IImageRepository

    private lateinit var viewModel: HomeScreenViewModel

    @Before
    fun setUp() {
        coEvery {
            mockImageRepository.getRecentPhotos(
                any<Int>(),
                any<Int>()
            )
        } returns Result.success(
            Paginated<Image>(
                page = 1,
                pages = 2,
                perPage = 1,
                total = 2,
                items = listOf(
                    Image(
                        id = "1",
                        imageUrl = "url",
                        title = "title",
                        description = "desc",
                        dateUpload = Date(100000L)
                    )
                )
            )
        )
        viewModel = HomeScreenViewModel(
            mockImageRepository
        )
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testViewModelInitializesToRecentsState() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }

        val uiState = viewModel.uiState.value

        assert(uiState is HomeScreenState.ImagesLoaded)
        assertEquals(1, (uiState as HomeScreenState.ImagesLoaded).imageList.size)
        coVerify { mockImageRepository.getRecentPhotos(1, 25) }
    }

    @Test
    fun testViewModelSearchIgnoresEmptyString() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }

        viewModel.executeSearch()

        val uiState = viewModel.uiState.value
        assert(uiState is HomeScreenState.ImagesLoaded)
        coVerify(exactly = 0) { mockImageRepository.searchPhotos(any(), any(), any()) }
    }

    @Test
    fun testViewModelSearchReturnsValues() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        coEvery { mockImageRepository.searchPhotos(any(), any(), any()) } returns Result.success(
            Paginated<Image>(
                page = 1,
                pages = 1,
                perPage = 1,
                total = 1,
                items = listOf(
                    Image(
                        id = "2",
                        imageUrl = "url2",
                        title = "title2",
                        description = "desc2",
                        dateUpload = Date(100000L)
                    )
                )
            )
        )

        viewModel.updateUserSearchString("spiderman")
        viewModel.executeSearch()

        val uiState = viewModel.uiState.value
        assert(uiState is HomeScreenState.ImagesLoaded)
        assertEquals("2", (uiState as HomeScreenState.ImagesLoaded).imageList.first().id)
        coVerify(exactly = 1) { mockImageRepository.searchPhotos("spiderman", 1, 25) }
    }

    @Test
    fun testViewModelCanAdvancePagination() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        coEvery { mockImageRepository.getRecentPhotos(2, 25) } returns Result.success(
            Paginated<Image>(
                page = 2,
                pages = 2,
                perPage = 1,
                total = 2,
                items = listOf(
                    Image(
                        id = "2",
                        imageUrl = "url2",
                        title = "title2",
                        description = "desc2",
                        dateUpload = Date(100000L)
                    )
                )
            )
        )

        viewModel.loadMoreItems()

        val uiState = viewModel.uiState.value
        assert(uiState is HomeScreenState.ImagesLoaded)
        assertEquals(2, (uiState as HomeScreenState.ImagesLoaded).imageList.size)
        assertEquals("1", uiState.imageList.first().id)
        assertEquals("2", uiState.imageList.last().id)
        coVerify(exactly = 1) { mockImageRepository.getRecentPhotos(2, 25) }
    }
}