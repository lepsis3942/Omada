package com.cjapps.omada.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    modifier: Modifier
) {
    val viewModel = hiltViewModel<HomeScreenViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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

            is HomeScreenState.ImagesLoaded -> ImageList(images = (uiState as HomeScreenState.ImagesLoaded).imageList)
        }
    }
}

@Composable
fun SearchInput(modifier: Modifier = Modifier, isEnabled: Boolean) {
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
        value = "text",
        placeholder = { Text(text = "Search") },
        onValueChange = { },
        singleLine = true,
        enabled = isEnabled,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onDone = {}
        ),
        trailingIcon = {
            IconButton(onClick = {})
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
fun ImageList(modifier: Modifier = Modifier, images: ImmutableList<Image>) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(count = images.size, key = { index -> images[index].id }) { index ->
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
            )
        }
    }
}