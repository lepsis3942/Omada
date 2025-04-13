package com.cjapps.omada.ui

import android.icu.text.DateFormat
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageDetailBottomSheet(
    onDismissRequest: () -> Unit,
    imageUrl: String,
    title: String?,
    description: String?,
    dateUpload: Date?
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest
    ) {
        val scrollState = rememberScrollState()
        val textStyle = MaterialTheme.typography.titleMedium
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .scrollable(scrollState, Orientation.Vertical)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "detail",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .padding(bottom = 16.dp)
            )
            if (!title.isNullOrBlank()) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Title:",
                        style = textStyle,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                    Text(text = title, style = textStyle)
                }
            }
            if (!description.isNullOrBlank()) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Description:",
                        style = textStyle,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                    Text(text = description, style = textStyle)
                }
            }
            if (dateUpload != null) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Date Uploaded:",
                        style = textStyle,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                    Text(
                        // Normally this would be formatted in the viewmodel so more in-depth formatting and error handling can occur
                        text = DateFormat.getDateTimeInstance().format(dateUpload),
                        style = textStyle
                    )
                }
            }
        }
    }
}