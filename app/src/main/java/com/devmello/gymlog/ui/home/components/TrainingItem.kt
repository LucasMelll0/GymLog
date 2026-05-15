package com.devmello.gymlog.ui.home.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devmello.gymlog.R
import com.devmello.gymlog.data.Mock
import com.devmello.gymlog.model.Training
import com.devmello.gymlog.ui.components.FilterChipList
import com.devmello.gymlog.ui.theme.GymLogTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrainingItem(
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    training: Training,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = modifier.combinedClickable(onClick = onClick, onLongClick = onLongClick)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.default_padding)),
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.large_padding))
                .fillMaxWidth()
                .heightIn(
                    min = dimensionResource(
                        id = R.dimen.minimum_training_item_height
                    )
                )
        ) {
            Text(text = training.title, style = MaterialTheme.typography.titleLarge)
            ProvideTextStyle(value = MaterialTheme.typography.bodyLarge) {
                if (training.exercises.isNotEmpty()) {
                    Text(
                        text = stringResource(
                            id = R.string.home_training_item_exercises_count_place_holder,
                            training.exercises.size
                        ),
                    )
                    Text(
                        text = stringResource(
                            id = R.string.home_training_item_estimated_time_place_holder,
                            training.getEstimatedTime()
                        )
                    )
                } else {
                    Text(text = stringResource(id = R.string.home_training_item_empty_exercises_message))
                }
            }

            if (training.filters.isNotEmpty()) {
                val filtersSize = training.filters.size
                FilterChipList(
                    rows = if (filtersSize < 4) 1 else 2,
                    filterList = training.filters,
                    modifier = Modifier
                        .padding(
                            vertical = dimensionResource(
                                id = R.dimen.default_padding
                            )
                        )
                        .heightIn(max = if (filtersSize < 4) 40.dp else 80.dp)
                )
            }

        }
    }
}

@Preview
@Composable
private fun TrainingItemPreview() {
    GymLogTheme {
        TrainingItem(
            onClick = { },
            training = Mock.getTrainings().random(),
            onLongClick = {})
    }
}