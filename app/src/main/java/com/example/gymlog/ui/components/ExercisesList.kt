package com.example.gymlog.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymlog.R
import com.example.gymlog.model.Exercise
import com.example.gymlog.ui.theme.GymLogTheme

@Composable
fun ExerciseItem(
    exercise: Exercise,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.medium.copy(all = CornerSize(8.dp)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(dimensionResource(id = R.dimen.default_padding)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = exercise.name,
                modifier = Modifier.padding(start = dimensionResource(id = R.dimen.default_padding)),
                style = MaterialTheme.typography.titleMedium
            )
            Column() {
                Text(
                    text = "${exercise.series} séries de\n${exercise.repetitions} repetições",
                    style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic)
                )
            }
            Checkbox(checked = exercise.isChecked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
fun ExerciseList(
    exercises: List<Exercise>,
    onCheckedChange: (exercise: Exercise, isChecked: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
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

@Preview(showSystemUi = true, widthDp = 320)
@Composable
private fun ExerciseListPreview() {
    val list =
        List(20) { Exercise(name = "Test $it", repetitions = 10, series = 5) }.toMutableStateList()
    GymLogTheme {
        ExerciseList(
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
    val exercise = Exercise(name = "Flexão de braço", repetitions = 20, series = 5)
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