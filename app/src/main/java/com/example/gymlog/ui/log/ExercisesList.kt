package com.example.gymlog.ui.log

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymlog.R
import com.example.gymlog.data.Mock
import com.example.gymlog.model.ExerciseMutableState
import com.example.gymlog.ui.components.FilterChipList
import com.example.gymlog.ui.components.TextWithIcon
import com.example.gymlog.ui.theme.GymLogTheme

@Composable
fun ExerciseItem(
    exercise: ExerciseMutableState,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.default_padding)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(
                modifier = Modifier
                    .weight(3f)
                    .padding(dimensionResource(id = R.dimen.default_padding)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.default_padding)),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = exercise.title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(
                        bottom = dimensionResource(
                            id = R.dimen.small_padding
                        )
                    )
                )
                Column(horizontalAlignment = Alignment.Start) {
                    TextWithIcon(text = stringResource(
                        id = R.string.exercise_series_place_holder,
                        exercise.series
                    ),
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_check_list),
                                contentDescription = null
                            )
                        })

                    TextWithIcon(
                        text = stringResource(
                            id = R.string.exercise_repetitions_place_holder,
                            exercise.repetitions
                        ),
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_loop),
                                contentDescription = null
                            )
                        }
                    )
                }
                if (exercise.observations.isNotEmpty()) {
                    Text(text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(stringResource(id = R.string.common_observations_prefix))
                        }
                        append(" ")
                        append(exercise.observations)
                    })
                }
            }
            Checkbox(
                checked = exercise.isChecked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.weight(1f)
            )
        }
        FilterChipList(
            filterList = exercise.filters, rows = 1, modifier = Modifier.padding(
                bottom = dimensionResource(
                    id = R.dimen.default_padding
                )
            )
        )
    }
}

@Composable
fun ExerciseList(
    exercises: List<ExerciseMutableState>,
    onCheckedChange: (exercise: ExerciseMutableState, isChecked: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    if (exercises.isNotEmpty()) {
        LazyColumn(modifier = modifier) {
            items(
                items = exercises,
                key = { exercise -> exercise.id }
            ) { exercise ->
                ExerciseItem(
                    modifier = Modifier.padding(
                        horizontal = dimensionResource(id = R.dimen.default_padding),
                        vertical = dimensionResource(
                            id = R.dimen.small_padding
                        )
                    ),
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
                series = 5,
                observations = "",
                filters = emptyList()
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
    val exercise = ExerciseMutableState(
        title = "Flexão de braço",
        repetitions = 20,
        series = 5,
        observations = "Ao realizar o agachamento, é essencial manter a postura correta e evitar que os joelhos ultrapassem a linha dos dedos dos pés.",
        filters = Mock.getFilters().map { stringResource(id = it.stringRes()) }
    )
    var isChecked by rememberSaveable {
        mutableStateOf(false)
    }
    GymLogTheme {
        Card {
            ExerciseItem(
                exercise = exercise,
                onCheckedChange = { isChecked = it },
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}