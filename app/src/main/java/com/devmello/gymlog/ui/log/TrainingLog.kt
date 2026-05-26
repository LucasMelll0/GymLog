package com.devmello.gymlog.ui.log

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.devmello.gymlog.R
import com.devmello.gymlog.core.ui.ScaffoldConfig
import com.devmello.gymlog.core.ui.ScaffoldManager
import com.devmello.gymlog.data.Mock
import com.devmello.gymlog.model.ExerciseMutableState
import com.devmello.gymlog.model.Training
import com.devmello.gymlog.ui.components.AppDropdownTimer
import com.devmello.gymlog.ui.components.AppStopwatch
import com.devmello.gymlog.ui.components.CustomLinearProgressBar
import com.devmello.gymlog.ui.components.DefaultAlertDialog
import com.devmello.gymlog.ui.components.LoadingDialog
import com.devmello.gymlog.ui.log.viewmodel.TrainingLogViewModel
import com.devmello.gymlog.ui.log.viewmodel.TrainingLogViewModelImpl
import com.devmello.gymlog.ui.theme.GymLogTheme
import com.devmello.gymlog.utils.BackPressHandler
import com.devmello.gymlog.utils.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun TrainingLogScreen(
    onClickEdit: (String) -> Unit,
    onBackPressed: () -> Unit,
    onError: () -> Unit,
    onClickDelete: () -> Unit,
    trainingId: String,
    scaffoldManager: ScaffoldManager,
    modifier: Modifier = Modifier,
    viewModel: TrainingLogViewModel = koinViewModel<TrainingLogViewModelImpl>()
) {
    val state by viewModel.state.collectAsStateWithLifecycle(State.Loading)
    var showResetDialog: Boolean by remember { mutableStateOf(false) }
    var showDeleteDialog: Boolean by remember { mutableStateOf(false) }
    var showTimerBottomSheet: Boolean by rememberSaveable { mutableStateOf(false) }
    var showStopwatchBottomSheet: Boolean by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(trainingId) {
        viewModel.getTraining(trainingId)
    }


    scaffoldManager.updateConfig(
        ScaffoldConfig(
            fab = {
                FloatingActionButton(onClick = {
                    onClickEdit(trainingId)
                    viewModel.setLoading()
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = stringResource(id = R.string.common_edit)
                    )
                }
            },
            tobBarActions = {
                TrainingLogAppBarActions(
                    onClickDelete = { showDeleteDialog = true },
                    onClickDropdownTimer = { showTimerBottomSheet = true },
                    onClickReset = { showResetDialog = true },
                    onClickStopwatch = { showStopwatchBottomSheet = true })
            },
            onNavigateBack = {
                viewModel.updateTraining(trainingId)
                true
            },
            showBottomBar = false,
        )
    )

    val scope = rememberCoroutineScope()

    BackPressHandler {
        viewModel.updateTraining(trainingId)
        onBackPressed()

    }

    Box(modifier = modifier) {
        if (showDeleteDialog) {
            DeleteDialog(onConfirm = {
                scope.launch {
                    viewModel.removeTraining(trainingId)
                    onClickDelete()
                }
            }, onDismiss = { showDeleteDialog = false })
        }
        if (showResetDialog) {
            ResetExercisesDialog(onConfirm = {
                viewModel.resetExercises()
                showResetDialog = false
            }, onDismiss = { showResetDialog = false })
        }
        if (state is State.Loading) LoadingDialog()

        if (state is State.Success) {
            Column(
                modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .verticalScroll(
                        rememberScrollState()
                    )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.default_padding)),
                    modifier = Modifier
                        .padding(vertical = dimensionResource(id = R.dimen.default_padding))
                ) {

                    TipCard(
                        modifier = Modifier.padding(
                            horizontal = dimensionResource(
                                id = R.dimen.default_padding
                            )
                        )
                    )
                    TrainingProgressBar(
                        exercises = viewModel.exercises, modifier = Modifier.padding(
                            dimensionResource(id = R.dimen.default_padding)
                        )
                    )
                }
                if (viewModel.exercises.isNotEmpty()) ExerciseList(
                    modifier = modifier
                        .padding(dimensionResource(id = R.dimen.default_padding))
                        .heightIn(
                            max = dimensionResource(
                                id = R.dimen.default_max_list_height
                            )
                        ),
                    exercises = viewModel.exercises,
                    onCheckedChange = { exercise, isChecked ->
                        viewModel.updateExercise(exercise.id, isChecked)
                    }) else TrainingLogEmptyListMessage(
                )
            }
        }
        if (showTimerBottomSheet) TimerBottomSheet(onDismissRequest = {
            showTimerBottomSheet = false
        })
        if (showStopwatchBottomSheet) StopwatchBottomSheet(
            savedTimesList = viewModel.savedStopwatchTimes,
            onSaveTime = { viewModel.saveStopwatchTime(it) },
            onReset = { viewModel.resetStopwatchTimes() },
            onDismissRequest = { showStopwatchBottomSheet = false })
    }
}

@Composable
fun TrainingLogAppBarActions(
    onClickStopwatch: () -> Unit,
    onClickDropdownTimer: () -> Unit,
    onClickReset: () -> Unit,
    onClickDelete: () -> Unit,
) {
    IconButton(onClick = onClickDropdownTimer) {
        Icon(
            painter = painterResource(id = R.drawable.ic_hourglass),
            contentDescription = stringResource(
                id = R.string.common_timer
            )
        )
    }
    IconButton(onClick = onClickStopwatch) {
        Icon(
            painter = painterResource(id = R.drawable.ic_stopwatch),
            contentDescription = stringResource(
                id = R.string.common_stopwatch
            )
        )
    }
    IconButton(onClick = onClickReset) {
        Icon(
            imageVector = Icons.Rounded.Refresh,
            contentDescription = stringResource(id = R.string.common_reset)
        )
    }
    IconButton(onClick = onClickDelete) {
        Icon(
            imageVector = Icons.Rounded.Delete,
            contentDescription = stringResource(id = R.string.common_delete)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerBottomSheet(onDismissRequest: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest, sheetState = rememberModalBottomSheetState(true)
    ) {
        AppDropdownTimer(modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.default_padding)))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopwatchBottomSheet(
    savedTimesList: List<Long>,
    onSaveTime: (Long) -> Unit,
    onReset: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(true)
    ) {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(dimensionResource(id = R.dimen.default_padding))
        ) {
            AppStopwatch(
                savedTimesList = savedTimesList,
                onSaveTime = onSaveTime,
                onReset = onReset
            )
        }
    }
}

@Composable
private fun TrainingProgressBar(
    exercises: List<ExerciseMutableState>, modifier: Modifier = Modifier
) {
    val percent by animateFloatAsState(
        exercises.filter { it.isChecked }.size.toFloat() / exercises.size.toFloat(),
        animationSpec = tween(
            durationMillis = 600, easing = LinearOutSlowInEasing, delayMillis = 50
        ),
        label = ""
    )
    CustomLinearProgressBar(
        modifier = modifier, percent = percent, text = stringResource(
            id = R.string.training_log_exercise_list_percent_place_holder, (percent * 100).toInt()
        )
    )
}


@Composable
fun TipCard(modifier: Modifier = Modifier) {
    val tip = stringArrayResource(id = R.array.training_tips).toList().random()
    Card(
        shape = MaterialTheme.shapes.large,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.tertiary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.default_padding)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.default_padding)),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_tip),
                contentDescription = "Tip",
            )
            Text(
                text = tip,
                style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic)
            )
        }
    }
}


@Composable
private fun DeleteDialog(
    modifier: Modifier = Modifier, onConfirm: () -> Unit, onDismiss: () -> Unit
) {
    DefaultAlertDialog(
        modifier = modifier,
        title = stringResource(id = R.string.common_dialog_title),
        text = stringResource(id = R.string.common_training_delete_dialog_text),
        icon = { Icon(imageVector = Icons.Rounded.Delete, contentDescription = null) },
        onDismissRequest = onDismiss,
        onConfirm = onConfirm
    )
}

@Composable
private fun ResetExercisesDialog(
    modifier: Modifier = Modifier, onConfirm: () -> Unit, onDismiss: () -> Unit
) {
    DefaultAlertDialog(
        modifier = modifier,
        title = stringResource(id = R.string.common_dialog_title),
        text = stringResource(id = R.string.training_log_reset_dialog_text),
        icon = { Icon(imageVector = Icons.Rounded.Refresh, contentDescription = null) },
        onDismissRequest = onDismiss,
        onConfirm = onConfirm
    )
}

@Composable
private fun TrainingLogEmptyListMessage(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth()
    ) {
        Card(shape = MaterialTheme.shapes.extraLarge) {
            Icon(
                painter = painterResource(id = R.drawable.ic_empty),
                contentDescription = null,
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.empty_list_icon_size))
            )
        }
        Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.large_padding)))
        Text(
            text = stringResource(id = R.string.training_log_empty_list_message),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(dimensionResource(id = R.dimen.large_padding))
        )
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun TrainingLogScreenPreview() {
    GymLogTheme {
        val training = Mock.getTrainings()[0]
        val viewModel = object : TrainingLogViewModel, ViewModel() {
            override val title: String = training.title
            override val exercises: List<ExerciseMutableState> = emptyList()
            override val filters: List<String> = training.filters
            override val state: Flow<State<Training>> =
                flow { emit(State.Success(training)) }
            override val savedStopwatchTimes: List<Long>
                get() = TODO("Not yet implemented")

            override fun setLoading() {}

            override suspend fun getTraining(id: String) {}

            override fun updateExercise(exerciseId: String, isChecked: Boolean) {}

            override fun resetExercises() {}

            override fun removeTraining(trainingId: String) {}

            override fun updateTraining(trainingId: String) {}
            override fun saveStopwatchTime(time: Long) {
                TODO("Not yet implemented")
            }

            override fun resetStopwatchTimes() {
                TODO("Not yet implemented")
            }

        }
        TrainingLogScreen(
            trainingId = "",
            viewModel = viewModel,
            onError = {},
            onClickDelete = {},
            onBackPressed = {},
            scaffoldManager = ScaffoldManager(),
            onClickEdit = {})
    }
}

