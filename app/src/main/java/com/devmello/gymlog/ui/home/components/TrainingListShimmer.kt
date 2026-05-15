package com.devmello.gymlog.ui.home.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.devmello.gymlog.extensions.shimmerEffect

@Composable
fun TrainingListShimmer(modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(5) {
            TrainingItemShimmer(modifier, Modifier.shimmerEffect())
        }
    }
}