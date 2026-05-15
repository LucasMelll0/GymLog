package com.devmello.gymlog.ui.home.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.devmello.gymlog.R
import com.devmello.gymlog.model.Training

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrainingList(
    onLongClickListener: (Training) -> Unit,
    onClickListener: (Training) -> Unit,
    trainingWithExercises: List<Training>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = dimensionResource(id = R.dimen.small_padding)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.default_padding))
    ) {
        items(
            trainingWithExercises,
            key = { training -> training.trainingId }
        ) { training ->
            TrainingItem(
                onClick = { onClickListener(training) },
                onLongClick = { onLongClickListener(training) },
                training = training,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.small_padding))
                    .animateItemPlacement(),
            )
        }
    }
}