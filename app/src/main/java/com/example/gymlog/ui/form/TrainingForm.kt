package com.example.gymlog.ui.form

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.gymlog.ui.components.FilterChipSelectionList
import com.example.gymlog.ui.form.viewmodel.TrainingFormViewModel
import com.example.gymlog.ui.theme.GymLogTheme
import com.example.gymlog.utils.TrainingTypes
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingFormScreen(
    modifier: Modifier = Modifier,
    viewModel: TrainingFormViewModel = viewModel()
) {
    var showDismissDialog: Boolean by rememberSaveable {
        mutableStateOf(false)
    }
    var nameHasError by remember { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            TrainingFormTopAppBar(onNavIconClick = {
                showDismissDialog = true
            })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                nameHasError = viewModel.trainingTitle.isEmpty()
                if (!nameHasError) {
                    scope.launch {
                        snackBarHostState.showSnackbar("Tudo Tranquilo")
                    }
                }
            }) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = stringResource(id = R.string.training_form_save_training)
                )
            }
        }) { paddingValues ->
        var showExerciseDialog: Boolean by rememberSaveable {
            mutableStateOf(false)
        }
        val filters = TrainingTypes.values().map { stringResource(id = it.stringRes()) }
        if (showDismissDialog) {
            DismissTrainingDialog(
                onDismissRequest = { showDismissDialog = false },
            ) {

            }
        }
        if (showExerciseDialog) {
            ExerciseForm(
                onDismiss = { showExerciseDialog = false },
                onExit = { showExerciseDialog = false },
                onConfirm = {
                    viewModel.addExercise(it)
                    showExerciseDialog = false
                }
            )
        }
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(paddingValues)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                DefaultTextField(
                    value = viewModel.trainingTitle,
                    onValueChange = { viewModel.setTrainingTitle(it) },
                    label = {
                        Text(text = stringResource(id = R.string.training_name_label))
                    },
                    charLimit = 50,
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.default_padding))
                        .fillMaxWidth(),
                    isError = nameHasError,
                    errorMessage = stringResource(id = R.string.common_text_field_error_message)
                )
                Spacer(modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.default_padding)))
                ExerciseListForm(
                    exercises = viewModel.exercises,
                    onClickRemove = { viewModel.removeExercise(it) },
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding))
                )
                DefaultTextButton(
                    text = stringResource(id = R.string.training_form_button_add_exercise_text),
                    onClick = { showExerciseDialog = true })
                Spacer(modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.default_padding)))
                Card(
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.default_padding))
                        .fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    FilterChipSelectionList(
                        selectedList = viewModel.filters,
                        filterList = filters,
                        onClick = {
                            if (!viewModel.filters.contains(it)) {
                                viewModel.addFilter(it)
                            } else {
                                viewModel.removeFilter(it)
                            }

                        },
                        title = stringResource(id = R.string.training_form_training_type_filter_title),
                        description = stringResource(id = R.string.training_form_filter_list_description)
                    )
                }
            }
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
            val firstItem = exercises.indexOf(exercise) == 0
            val lastItem = exercises.indexOf(exercise) == exercises.lastIndex
            ExerciseItemForm(
                exercise = exercise,
                onClickRemove = onClickRemove,
                roundedTopRadius = firstItem,
                roundedBottomRadius = lastItem
            )
        }
    }
}


@Composable
fun ExerciseItemForm(
    modifier: Modifier = Modifier,
    exercise: Exercise,
    onClickRemove: (Exercise) -> Unit,
    roundedTopRadius: Boolean = false,
    roundedBottomRadius: Boolean = false
) {
    val topRadius =
        if (roundedTopRadius) dimensionResource(id = R.dimen.default_corner_size) else 0.dp
    val bottomRadius =
        if (roundedBottomRadius) dimensionResource(id = R.dimen.default_corner_size) else 0.dp
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(
            topStart = CornerSize(topRadius),
            topEnd = CornerSize(topRadius),
            bottomStart = CornerSize(bottomRadius),
            bottomEnd = CornerSize(bottomRadius)
        )
    ) {
        Row(
            Modifier
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
            IconButton(onClick = { onClickRemove(exercise) }) {
                Icon(
                    Icons.Rounded.Close,
                    contentDescription = stringResource(id = R.string.delete_exercise_content_description)
                )
            }
        }
    }
}

@Composable
private fun DismissTrainingDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit
) {
    AlertDialog(
        modifier = modifier.fillMaxWidth(),
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(id = R.string.common_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(id = R.string.common_cancel))
            }
        },
        icon = { Icon(imageVector = Icons.Rounded.Delete, contentDescription = null) },
        title = { Text(text = stringResource(id = R.string.training_form_dismiss_dialog_title)) },
        text = { Text(text = stringResource(id = R.string.training_form_dismiss_dialog_text)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrainingFormTopAppBar(onNavIconClick: () -> Unit, modifier: Modifier = Modifier) {
    TopAppBar(title = {
        Text(
            text = stringResource(id = R.string.training_form_top_bar_title),
            modifier = modifier
        )
    },
        navigationIcon = {
            IconButton(onClick = onNavIconClick) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = stringResource(id = R.string.common_go_to_back)
                )
            }
        })
}

@Preview
@Composable
private fun DismissTrainingDialogPreview() {
    DismissTrainingDialog(onDismissRequest = { }) {

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