package com.cjapps.omada.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.cjapps.omada.R
import com.cjapps.omada.data.models.Image
import kotlinx.collections.immutable.ImmutableList

@Composable
fun HomeScreen(
    modifier: Modifier,
    snackBarHostState: SnackbarHostState,
) {
    val viewModel = hiltViewModel<HomeScreenViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // For ease of demo app used separate flows for these but I usually like to keep UI state consolidated to as few flows as possible
    val snackBarState by viewModel.errorSnackBarFlow.collectAsStateWithLifecycle()
    val modalBottomSheetState by viewModel.modalBottomSheetStateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(snackBarState) {
        if (snackBarState != null) {
            snackBarHostState.showSnackbar("An Error Occurred")
        }
    }

    val sheetState = modalBottomSheetState
    if (sheetState != null) {
        ImageDetailBottomSheet(
            onDismissRequest = viewModel::dismissModalBottomSheet,
            imageUrl = sheetState.imageUrl,
            title = sheetState.title,
            description = sheetState.description,
            dateUpload = sheetState.dateUpload,
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            SearchInput(
                modifier = Modifier.fillMaxWidth(),
                value = (uiState as? HomeScreenState.ImagesLoaded)?.userSearchString ?: "",
                onSubmit = viewModel::executeSearch,
                onValueChange = viewModel::updateUserSearchString,
                isEnabled = uiState !is HomeScreenState.Loading
            )
        }
        when (uiState) {
            HomeScreenState.Loading -> Row(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator()
            }

            is HomeScreenState.ImagesLoaded -> {
                val uiState = uiState as HomeScreenState.ImagesLoaded
                if (uiState.imageList.isNotEmpty()) {
                    ImageList(
                        images = uiState.imageList,
                        showLoadingItem = uiState.canLoadMoreImages,
                        endOfListReached = viewModel::loadMoreItems,
                        imageTapped = viewModel::imageTapped
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "No images found")
                    }
                }
            }
        }
    }
}

@Composable
fun SearchInput(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onSubmit: () -> Unit,
    isEnabled: Boolean
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isTextFieldFocused = interactionSource.collectIsFocusedAsState()
    val submitButtonColor by animateColorAsState(
        targetValue = if (isTextFieldFocused.value) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.outline
        },
        label = "submit button color"
    )
    OutlinedTextField(
        modifier = modifier,
        value = value,
        placeholder = { Text(text = "Search") },
        onValueChange = onValueChange,
        singleLine = true,
        enabled = isEnabled,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { onSubmit() }
        ),
        trailingIcon = {
            IconButton(onClick = onSubmit)
            {
                Icon(
                    modifier = Modifier
                        .size(36.dp),
                    painter = painterResource(id = R.drawable.ic_circle_arrow_right),
                    contentDescription = "submit",
                    tint = submitButtonColor
                )
            }
        },
        shape = RoundedCornerShape(60.dp),
        interactionSource = interactionSource
    )
}

@Composable
fun ImageList(
    modifier: Modifier = Modifier,
    images: ImmutableList<Image>,
    showLoadingItem: Boolean,
    endOfListReached: () -> Unit,
    imageTapped: (Image) -> Unit
) {
    val listSize = if (showLoadingItem) {
        images.size + 1
    } else {
        images.size
    }
    val listState = rememberLazyGridState()
    LaunchedEffect(listState.canScrollForward) {
        if (!listState.canScrollForward) endOfListReached()
    }

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        state = listState
    ) {
        items(count = listSize) { index ->
            if (index != images.size) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(images[index].imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = images[index].id,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable {
                            imageTapped(images[index])
                        }
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}