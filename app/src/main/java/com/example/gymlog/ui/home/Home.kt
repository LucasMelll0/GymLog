package com.example.gymlog.ui.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymlog.R
import com.example.gymlog.model.Exercise
import com.example.gymlog.model.Training
import com.example.gymlog.ui.theme.GymLogTheme
import com.example.gymlog.utils.TrainingTypes

@Composable
fun TrainingItem(training: Training, modifier: Modifier = Modifier) {
    var isExpanded by remember { mutableStateOf(false) }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
    ) {
        Card() {
            Column(modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.default_padding))) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = training.title,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(
                            start = dimensionResource(
                                id = R.dimen.default_padding
                            )
                        )
                    )
                    IconButton(onClick = { isExpanded = !isExpanded }) {
                        Icon(imageVector = Icons.Rounded.ArrowDropDown, contentDescription = null)
                    }
                }
                FilterListTrainingItem(
                    filters = training.filters,
                    modifier = Modifier.padding(
                        vertical = dimensionResource(
                            id = R.dimen.default_padding
                        )
                    )
                )

            }
        }
        if (isExpanded) {
            ExerciseListTrainingItem(
                exercises = training.exercises,
                modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.default_padding))
            )
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0)
@Composable
private fun TrainingItemPreview() {
    val filters = listOf(
        TrainingTypes.ABDOMEN,
        TrainingTypes.BICEPS,
        TrainingTypes.BACK,
        TrainingTypes.ABDOMEN,
        TrainingTypes.CALF
    ).map {
        stringResource(
            id = it.stringRes()
        )
    }
    val exercises =
        List(5) { i -> Exercise(title = "Exercise $i", repetitions = i + i, series = i + i) }

    val training = Training(title = "Training Test", exercises = exercises, filters = filters)
    GymLogTheme {
        TrainingItem(
            training = training, modifier = Modifier.padding(
                dimensionResource(id = R.dimen.default_padding)
            )
        )
    }
}

@Composable
private fun FilterListTrainingItem(filters: List<String>, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.primaryContainer.copy(0.1f)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(70.dp),
            modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.small_padding)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.small_padding))
        ) {
            items(filters) { filter ->
                FilterTrainingItem(filter = filter)
            }
        }
    }
}

@Composable
private fun FilterTrainingItem(filter: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Text(
            text = filter,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(dimensionResource(id = R.dimen.small_padding))
        )
    }
}

@Composable
private fun ExerciseListTrainingItem(exercises: List<Exercise>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(exercises) { exercise ->
            ExerciseTrainingItem(
                exercise = exercise, modifier = Modifier.padding(
                    dimensionResource(id = R.dimen.small_padding)
                )
            )
        }
    }
}

@Composable
private fun ExerciseTrainingItem(exercise: Exercise, modifier: Modifier = Modifier) {
    Row(

        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(
                    id = R.dimen.small_padding
                )
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = exercise.title,
            style = MaterialTheme.typography.bodySmall,
        )
        Text(
            text = "${exercise.repetitions}X${exercise.series}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}