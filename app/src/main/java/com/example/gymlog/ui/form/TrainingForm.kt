package com.example.gymlog.ui.form

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymlog.R
import com.example.gymlog.model.Exercise
import com.example.gymlog.ui.components.DefaultTextButton
import com.example.gymlog.ui.components.DefaultTextField
import com.example.gymlog.ui.form.viewmodel.TrainingFormViewModel
import com.example.gymlog.ui.theme.GymLogTheme


@Composable
fun TrainingFormScreen(
    modifier: Modifier = Modifier,
    trainingFormViewModel: TrainingFormViewModel = viewModel()
) {
    var showExerciseDialog: Boolean by rememberSaveable {
        mutableStateOf(false)
    }
    if (showExerciseDialog) {
        ExerciseForm(
            onDismiss = { showExerciseDialog = false },
            onExit = { showExerciseDialog = false },
            onConfirm = {
                trainingFormViewModel.addExercise(it)
                showExerciseDialog = false
            }
        )
    }
    Surface(modifier = modifier
        .fillMaxWidth()
        .fillMaxHeight()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            DefaultTextField(
                value = trainingFormViewModel.trainingTitle,
                onValueChange = { trainingFormViewModel.setTrainingTitle(it) },
                label = {
                    Text(text = stringResource(id = R.string.training_name_label))
                },
                charLimit = 30,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.default_padding))
                    .fillMaxWidth()
            )
            ExerciseListForm(
                exercises = trainingFormViewModel.exercises,
                onClickRemove = { trainingFormViewModel.removeExercise(it) }
            )
            DefaultTextButton(text = "Adicionar Exercício", onClick = { showExerciseDialog = true })

        }
    }
}


@Composable
fun ExerciseListForm(
    exercises: List<Exercise>,
    onClickRemove: (Exercise) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(
            items = exercises,
            key = { exercise -> exercise.id }
        ) { exercise ->
            ExerciseItemForm(exercise = exercise, onClickRemove = onClickRemove)
            if (exercises.indexOf(exercise) != exercises.lastIndex) {
                Divider()
            }
        }
    }
}


@Composable
fun ExerciseItemForm(
    exercise: Exercise,
    onClickRemove: (Exercise) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(dimensionResource(id = R.dimen.default_padding)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = exercise.title,
                modifier = Modifier.padding(start = dimensionResource(id = R.dimen.default_padding)),
                style = MaterialTheme.typography.titleMedium
            )
            Column() {
                Text(
                    text = stringResource(
                        R.string.exercise_repetions_place_holder,
                        exercise.series,
                        exercise.repetitions
                    ),
                    style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic)
                )
            }
            IconButton(onClick = { onClickRemove(exercise) }) {
                Icon(
                    Icons.Rounded.Close,
                    contentDescription = stringResource(id = R.string.delete_exercise_content_description)
                )
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun TrainingFormScreenPreview() {
    GymLogTheme {
        TrainingFormScreen()
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun ExerciseItemFormPreview() {
    val exercise = Exercise(title = "Flexão de Braço", repetitions = 20, series = 5)
    GymLogTheme {
        ExerciseItemForm(exercise = exercise, onClickRemove = {}, modifier = Modifier.padding(8.dp))
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES, showSystemUi = true)
@Preview(showSystemUi = true)
@Composable
private fun ExerciseListFormPreview() {
    val list =
        List(20) { Exercise(title = "Test $it", repetitions = 10, series = 5) }.toMutableStateList()
    GymLogTheme {
        ExerciseListForm(exercises = list, onClickRemove = { list.remove(it) })
    }
}