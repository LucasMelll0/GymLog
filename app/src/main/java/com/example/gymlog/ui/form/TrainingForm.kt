package com.example.gymlog.ui.form

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymlog.R
import com.example.gymlog.database.AppDataBase_Impl
import com.example.gymlog.model.Exercise
import com.example.gymlog.model.Training
import com.example.gymlog.repository.TrainingRepositoryImpl
import com.example.gymlog.ui.components.DefaultAlertDialog
import com.example.gymlog.ui.components.DefaultTextButton
import com.example.gymlog.ui.components.DefaultTextField
import com.example.gymlog.ui.components.FilterChipList
import com.example.gymlog.ui.form.viewmodel.TrainingFormViewModel
import com.example.gymlog.ui.theme.GymLogTheme
import com.example.gymlog.utils.BackPressHandler
import com.example.gymlog.utils.TrainingTypes
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@Composable
fun TrainingFormScreen(
    onSaveTraining: () -> Unit,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier,
    trainingId: String? = null,
    viewModel: TrainingFormViewModel = koinViewModel()
) {

    val scope = rememberCoroutineScope()
    trainingId?.let {
        LaunchedEffect(key1 = Unit) {
            viewModel.getTrainingById(trainingId)
        }
    }


    var showDismissDialog: Boolean by rememberSaveable {
        mutableStateOf(false)
    }
    BackPressHandler {
        showDismissDialog = true
    }
    var nameHasError by remember { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        bottomBar = {
            TrainingFormBottomBar(
                onConfirm = {
                    nameHasError = viewModel.trainingTitle.isEmpty()
                    if (!nameHasError) {
                        scope.launch {
                            val training = Training(
                                title = viewModel.trainingTitle,
                                filters = viewModel.filters,
                                exercises = viewModel.exercises
                            )
                            viewModel.saveTraining(training)
                            onSaveTraining()
                        }
                    }
                },
                onNavIconClick = { showDismissDialog = true })
        }) { paddingValues ->
        var showExerciseDialog: Boolean by rememberSaveable {
            mutableStateOf(false)
        }
        var exerciseToEditIndex: Int? by rememberSaveable { mutableStateOf(null) }
        TrainingTypes.values().map { stringResource(id = it.stringRes()) }
        if (showDismissDialog) {
            DismissTrainingDialog(
                onDismissRequest = { showDismissDialog = false },
                onConfirm = onDismissClick
            )
        }
        val exerciseToEdit: Exercise? =
            exerciseToEditIndex?.let { viewModel.exercises.getOrNull(it) }
        if (showExerciseDialog) {
            ExerciseForm(
                exerciseToEdit = exerciseToEdit,
                onDismiss = { showExerciseDialog = false },
                onExit = { showExerciseDialog = false },
                onConfirm = {
                    exerciseToEditIndex?.let {
                        viewModel.removeExercise(exerciseToEdit!!)
                    }
                    viewModel.addExercise(it)
                    showExerciseDialog = false
                    exerciseToEditIndex = null
                }
            )
        }
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DefaultTextField(
                        value = viewModel.trainingTitle,
                        onValueChange = { viewModel.setTrainingTitle(it) },
                        label = {
                            Text(text = stringResource(id = R.string.training_name_label))
                        },
                        modifier = Modifier
                            .padding(dimensionResource(id = R.dimen.default_padding))
                            .fillMaxWidth(),
                        isError = nameHasError,
                        errorMessage = stringResource(id = R.string.common_text_field_error_message),
                        charLimit = 50,
                    )
                    Spacer(modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.default_padding)))
                    ExerciseListForm(
                        exercises = viewModel.exercises,
                        onClickAdd = { showExerciseDialog = true },
                        onClickRemove = { viewModel.removeExercise(it) },
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding)),
                        onItemClickListener = {
                            exerciseToEditIndex =
                                if (viewModel.exercises.indexOf(it) != -1) viewModel.exercises.indexOf(
                                    it
                                ) else null
                            showExerciseDialog = true
                        }
                    )
                }
                AnimatedVisibility(viewModel.exercises.isNotEmpty()) {
                    Card(modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding))) {
                        FilterChipList(filterList = viewModel.filters)
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExerciseListForm(
    exercises: List<Exercise>,
    onClickAdd: () -> Unit,
    onClickRemove: (Exercise) -> Unit,
    modifier: Modifier = Modifier,
    onItemClickListener: (Exercise) -> Unit
) {
    Card(modifier = modifier) {
        if (exercises.isNotEmpty()) {
            Text(
                text = "Exercícios",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(id = R.dimen.default_padding)),
                style = MaterialTheme.typography.titleMedium
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 200.dp)
                .animateContentSize()
        ) {
            items(
                items = exercises,
                key = { exercise -> exercise.exerciseId }
            ) { exercise ->
                ExerciseItemForm(
                    modifier = Modifier
                        .animateItemPlacement(),
                    exercise = exercise,
                    onClickRemove = onClickRemove,
                    onClick = onItemClickListener
                )
            }
        }
        if (exercises.isNotEmpty()) {
            Divider(color = DividerDefaults.color.copy(alpha = 0.3f))
        }
        DefaultTextButton(
            text = stringResource(id = R.string.training_form_button_add_exercise_text),
            onClick = onClickAdd
        )
    }
}


@Composable
fun ExerciseItemForm(
    modifier: Modifier = Modifier,
    exercise: Exercise,
    onClickRemove: (Exercise) -> Unit,
    onClick: (Exercise) -> Unit

) {
    Column(
        modifier = modifier
            .padding(dimensionResource(id = R.dimen.default_padding))
            .fillMaxWidth()
            .clickable { onClick(exercise) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = exercise.title,
                modifier = Modifier.padding(start = dimensionResource(id = R.dimen.default_padding)),
                style = MaterialTheme.typography.titleMedium
            )
            Column {
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
        if (exercise.observations.isNotEmpty()) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Observações: ")
                    }
                    append(exercise.observations)
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding))
            )
        }
    }
}

@Composable
private fun DismissTrainingDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit
) {
    DefaultAlertDialog(
        modifier = modifier,
        title = stringResource(id = R.string.common_dialog_title),
        text = stringResource(id = R.string.training_form_dismiss_dialog_text),
        icon = { Icon(imageVector = Icons.Rounded.Delete, contentDescription = null) },
        onDismissRequest = onDismissRequest,
        onConfirm = onConfirm
    )
}

@Composable
private fun TrainingFormBottomBar(
    onConfirm: () -> Unit,
    onNavIconClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BottomAppBar(
        modifier = modifier,
        actions = {
            IconButton(onClick = onNavIconClick) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack, contentDescription = stringResource(
                        id = R.string.common_go_to_back
                    )
                )
            }
        }, floatingActionButton = {
            FloatingActionButton(onClick = onConfirm) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = stringResource(id = R.string.common_confirm)
                )
            }
        })
}

@Preview
@Composable
private fun TrainingFormBottomBarPreview() {
    GymLogTheme {
        TrainingFormBottomBar(onConfirm = {}, onNavIconClick = {})
    }
}

@Preview
@Composable
private fun DismissTrainingDialogPreview() {
    DismissTrainingDialog(onDismissRequest = { }) {

    }
}

@Suppress("UNCHECKED_CAST")
@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun TrainingFormScreenPreview() {
    GymLogTheme {
        val viewModelFactory = object : ViewModelProvider.NewInstanceFactory() {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repositoryImpl = TrainingRepositoryImpl(AppDataBase_Impl().trainingDao())
                return TrainingFormViewModel(repositoryImpl) as T
            }
        }
        val viewModel: TrainingFormViewModel = viewModel(factory = viewModelFactory)
        TrainingFormScreen(
            onDismissClick = {},
            onSaveTraining = {},
            viewModel = viewModel
        )
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun ExerciseListFormPreview() {
    val list =
        List(2) {
            val observations = if (it == 0) "Manter o peitoral sempre tensionado" else ""
            Exercise(
                title = "Test $it",
                repetitions = 10,
                series = 5,
                observations = observations,
                filters = emptyList()
            )
        }.toMutableStateList()
    GymLogTheme {
        ExerciseListForm(
            exercises = list,
            onClickAdd = {},
            onClickRemove = { list.remove(it) },
            onItemClickListener = {})
    }
}

@Preview
@Composable
private fun ExerciseItemFormPreview() {
    GymLogTheme {
        Card() {
            val exercise = Exercise(
                title = "Flexão de braço",
                repetitions = 20,
                series = 5,
                observations = "Manter o peitoral sempre tensionado",
                filters = listOf("peito", "triceps", "abdomen")
            )
            ExerciseItemForm(exercise = exercise, onClickRemove = {}, onClick = {})
        }
    }
}