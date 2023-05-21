package com.example.gymlog.ui.log

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymlog.R
import com.example.gymlog.model.ExerciseMutableState
import com.example.gymlog.ui.theme.GymLogTheme

@Composable
fun ExerciseItem(
    exercise: ExerciseMutableState,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(dimensionResource(id = R.dimen.default_padding))
            .fillMaxWidth(),
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
        Checkbox(checked = exercise.isChecked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun ExerciseList(
    exercises: List<ExerciseMutableState>,
    onCheckedChange: (exercise: ExerciseMutableState, isChecked: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val percent = (exercises.filter { it.isChecked }.size.toFloat() / exercises.size.toFloat())
    Card(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(
                vertical = dimensionResource(
                    id = R.dimen.default_padding
                )
            )
        ) {
            Text(
                text = stringResource(
                    R.string.training_log_exercise_list_percent_place_holder,
                    (percent * 100).toInt()
                ),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                progress = percent,
                trackColor = MaterialTheme.colorScheme.primary.copy(0.1f),
                color = MaterialTheme.colorScheme.primary
            )
        }
        LazyColumn(modifier = modifier) {
            items(
                items = exercises,
                key = { exercise -> exercise.id }
            ) { exercise ->
                ExerciseItem(
                    exercise = exercise,
                    onCheckedChange = { isChecked -> onCheckedChange(exercise, isChecked) })
            }
        }
    }
}

@Preview(showSystemUi = true, uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Preview(showSystemUi = true)
@Composable
private fun ExerciseListPreview() {
    val list =
        List(20) {
            ExerciseMutableState(
                title = "Test $it",
                repetitions = 10,
                series = 5
            )
        }.toMutableStateList()
    GymLogTheme {
        ExerciseList(
            modifier = Modifier.padding(8.dp),
            exercises = list,
            onCheckedChange = { exercise, isChecked ->
                list.find { it.id == exercise.id }?.let { it.isChecked = isChecked }
            }
        )
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview()
@Composable
private fun ExerciseItemPreview() {
    val exercise = ExerciseMutableState(title = "Flexão de braço", repetitions = 20, series = 5)
    var isChecked by rememberSaveable {
        mutableStateOf(false)
    }
    GymLogTheme {
        ExerciseItem(
            exercise = exercise,
            onCheckedChange = { isChecked = it },
            modifier = Modifier.padding(8.dp)
        )
    }
}