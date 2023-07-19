package com.example.gymlog.ui.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest

@Composable
fun DefaultAsyncImage(
    data: Any?,
    diskCacheKey: String,
    error: Painter,
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    contentDescription: String? = null
) {
    AsyncImage(
        model = ImageRequest.Builder(context).data(data)
            .diskCacheKey(diskCacheKey)
            .diskCachePolicy(CachePolicy.DISABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .build(),
        error = error,
        contentScale = ContentScale.Crop,
        alignment = Alignment.Center,
        contentDescription = contentDescription,
        modifier = modifier
    )
}