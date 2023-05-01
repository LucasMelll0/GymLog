package com.example.gymlog.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.text.isDigitsOnly
import com.example.gymlog.R
import com.example.gymlog.model.Exercise
import com.example.gymlog.ui.theme.GymLogTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseForm(
    onDismiss: () -> Unit,
    onExit: () -> Unit,
    onConfirm: (Exercise) -> Unit,
    modifier: Modifier = Modifier
) {
    var title by rememberSaveable { mutableStateOf("") }
    var series by rememberSaveable { mutableStateOf(0) }
    var repetitions by rememberSaveable { mutableStateOf(0) }
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Card(
            shape = RoundedCornerShape(10.dp),
            modifier = modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.default_padding))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { newTitle -> title = newTitle },
                    label = {
                        Text(
                            text = stringResource(id = R.string.exercise_name_label)
                        )
                    })
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                ) {
                    OutlinedTextField(
                        series.toString(),
                        onValueChange = {
                            if (it.isDigitsOnly()) {
                                series = it.toInt()
                            }
                        },
                        label = {
                            Text(text = stringResource(id = R.string.exercise_series_label))
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(id = R.dimen.default_padding))
                            .weight(1f)
                    )
                    OutlinedTextField(
                        repetitions.toString(),
                        onValueChange = {
                            if (it.isDigitsOnly()) {
                                repetitions = it.toInt()
                            }
                        },
                        label = {
                            Text(text = stringResource(id = R.string.exercise_repetitions_label))
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(id = R.dimen.default_padding))
                            .weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(
                        onClick = { onExit() },
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                dimensionResource(
                                    id = R.dimen.default_padding
                                )
                            )
                            .weight(1f)
                    ) {
                        Text(text = stringResource(id = R.string.common_cancel))
                    }
                    Button(
                        onClick = {
                            val exercise =
                                Exercise(name = title, series = series, repetitions = repetitions)
                            onConfirm(exercise)
                        },
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                dimensionResource(
                                    id = R.dimen.default_padding
                                )
                            )
                            .weight(1.5f)
                    ) {
                        Text(text = stringResource(id = R.string.common_confirm))
                    }
                }

            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun ExerciseFormPreview() {

    GymLogTheme {
        ExerciseForm(onDismiss = {}, onExit = {}, onConfirm = {})
    }
}