package com.devmello.gymlog.ui.home.components

import androidx.compose.animation.core.Spring.StiffnessMediumLow
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.IntOffset
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
            Modifier
                .padding(dimensionResource(id = R.dimen.small_padding))
            TrainingItem(
                onClick = { onClickListener(training) },
                onLongClick = { onLongClickListener(training) },
                training = training,
                modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null, placementSpec = spring(
                            stiffness = StiffnessMediumLow,
                            visibilityThreshold = IntOffset.VisibilityThreshold
                        )
                ),
            )
        }
    }
}