package com.example.gymlog.ui.form

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.gymlog.extensions.isZeroOrEmpty
import com.example.gymlog.model.Exercise
import com.example.gymlog.ui.components.DefaultTextField
import com.example.gymlog.ui.theme.GymLogTheme

@Composable
fun ExerciseForm(
    onDismiss: () -> Unit,
    onExit: () -> Unit,
    onConfirm: (Exercise) -> Unit,
    modifier: Modifier = Modifier
) {
    var title by rememberSaveable { mutableStateOf("") }
    var series: String by rememberSaveable { mutableStateOf("") }
    var repetitions: String by rememberSaveable { mutableStateOf("") }
    var titleHasError by remember { mutableStateOf(false) }
    var seriesHasError by remember { mutableStateOf(false) }
    var repetitionsHasError by remember { mutableStateOf(false) }
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Card(
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(dimensionResource(id = R.dimen.default_padding))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.default_padding))
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = stringResource(id = R.string.exercise_form_title),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.default_padding))
                )
                DefaultTextField(
                    value = title,
                    onValueChange = { newTitle -> title = newTitle },
                    label = {
                        Text(
                            text = stringResource(id = R.string.exercise_name_label)
                        )
                    },
                    isError = titleHasError,
                    errorMessage = stringResource(id = R.string.default_text_field_error_message),
                    charLimit = 50
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                ) {

                    DefaultTextField(
                        value = series,
                        onValueChange = {
                            if (it.isDigitsOnly()) {
                                series = it
                            }
                        },
                        isError = seriesHasError,
                        errorMessage = stringResource(id = R.string.series_text_field_error_message),
                        label = {
                            Text(stringResource(id = R.string.exercise_series_label))
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(id = R.dimen.default_padding))
                            .weight(1f),

                        )

                    DefaultTextField(
                        value = repetitions,
                        onValueChange = {
                            if (it.isDigitsOnly()) {
                                repetitions = it
                            }
                        },
                        isError = repetitionsHasError,
                        errorMessage = stringResource(id = R.string.repetitions_text_field_error_message),
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
                            titleHasError = title.isEmpty()
                            seriesHasError = series.isZeroOrEmpty()
                            repetitionsHasError = repetitions.isZeroOrEmpty()
                            if (!titleHasError &&
                                !seriesHasError &&
                                !repetitionsHasError
                            ) {
                                val exercise =
                                    Exercise(
                                        title = title,
                                        series = series.toInt(),
                                        repetitions = repetitions.toInt()
                                    )
                                onConfirm(exercise)
                            }
                        },
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                dimensionResource(
                                    id = R.dimen.default_padding
                                )
                            )
                            .weight(1.3f)
                    ) {
                        Text(text = stringResource(id = R.string.common_confirm))
                    }
                }

            }
        }
    }
}


@Preview(name = "Night Mode", uiMode = UI_MODE_NIGHT_YES, widthDp = 320)
@Preview(widthDp = 320)
@Composable
private fun ExerciseFormPreview() {
    var showDialog by remember { mutableStateOf(true) }
    GymLogTheme {
        if (showDialog) {
            ExerciseForm(
                onDismiss = { showDialog = false },
                onExit = { showDialog = false },
                onConfirm = {
                    showDialog = false
                    Log.i("Test", "ExerciseFormPreview: $it")
                })
        }
    }
}