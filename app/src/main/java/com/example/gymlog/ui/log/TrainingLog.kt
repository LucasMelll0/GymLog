package com.example.gymlog.ui.log

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.rounded.ArrowBack
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
import androidx.compose.material3.Scaffold
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gymlog.R
import com.example.gymlog.data.Mock
import com.example.gymlog.model.ExerciseMutableState
import com.example.gymlog.model.Training
import com.example.gymlog.ui.components.AppDropdownTimer
import com.example.gymlog.ui.components.CustomLinearProgressBar
import com.example.gymlog.ui.components.DefaultAlertDialog
import com.example.gymlog.ui.components.LoadingDialog
import com.example.gymlog.ui.log.viewmodel.TrainingLogViewModel
import com.example.gymlog.ui.log.viewmodel.TrainingLogViewModelImpl
import com.example.gymlog.ui.theme.GymLogTheme
import com.example.gymlog.utils.BackPressHandler
import com.example.gymlog.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun TrainingLogScreen(
    onClickEdit: (String) -> Unit,
    onBackPressed: () -> Unit,
    onNavIconClick: () -> Unit,
    onError: () -> Unit,
    onClickDelete: () -> Unit,
    trainingId: String,
    modifier: Modifier = Modifier,
    viewModel: TrainingLogViewModel = koinViewModel<TrainingLogViewModelImpl>()
) {
    val resource by viewModel.resource.collectAsStateWithLifecycle(Resource.Loading)
    var isLoading: Boolean = resource is Resource.Loading
    var showResetDialog: Boolean by remember { mutableStateOf(false) }
    var showDeleteDialog: Boolean by remember { mutableStateOf(false) }
    var showTimerBottomSheet: Boolean by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(key1 = Unit) {
        viewModel.getTraining(trainingId)
    }
    val scope = rememberCoroutineScope()

    BackPressHandler {
        scope.launch {
            viewModel.updateTraining(trainingId)
            onBackPressed()
        }
    }

    Scaffold(bottomBar = {
        TrainingLogBottomAppBar(onNavIconClick = {
            scope.launch {
                viewModel.updateTraining(trainingId)
                onNavIconClick()
            }
        },
            onClickDelete = { showDeleteDialog = true },
            onClickReset = { showResetDialog = true },
            onClickEdit = {
                onClickEdit(trainingId)
                viewModel.setLoading()
            },
            onClickTimer = { showTimerBottomSheet = true })
    }) { paddingValues ->
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
        when (resource) {
            is Resource.Loading -> {
                isLoading = true
            }

            is Resource.Success -> {
                isLoading = false
            }

            else -> onError()
        }
        if (isLoading) LoadingDialog()

        if (resource is Resource.Success) {
            ConstraintLayout(
                modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(paddingValues)
                    .verticalScroll(
                        rememberScrollState()
                    )
            ) {
                val (header, exercises, emptyListMessage) = createRefs()
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.default_padding)),
                    modifier = Modifier
                        .constrainAs(header) {
                            top.linkTo(parent.top)
                            height = Dimension.wrapContent
                        }
                        .padding(vertical = dimensionResource(id = R.dimen.default_padding))
                ) {
                    val tips = stringArrayResource(id = R.array.training_tips).toList()
                    TipCard(
                        tips = tips, modifier = Modifier.padding(
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
                if (viewModel.exercises.isNotEmpty()) ExerciseList(modifier = modifier
                    .constrainAs(
                        exercises
                    ) {
                        linkTo(header.bottom, parent.bottom, bias = 0f)
                        height = Dimension.fillToConstraints
                    }
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
                    modifier = Modifier.constrainAs(
                        emptyListMessage
                    ) {
                        top.linkTo(header.bottom)
                        height = Dimension.wrapContent
                    }
                )
            }
        }
        if (showTimerBottomSheet) {
            TimerBottomSheet(onDismissRequest = { showTimerBottomSheet = false })
        }
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
fun TipCard(tips: List<String>, modifier: Modifier = Modifier) {
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
                text = tips.random(),
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

@Composable
private fun TrainingLogBottomAppBar(
    onNavIconClick: () -> Unit,
    onClickDelete: () -> Unit,
    onClickReset: () -> Unit,
    onClickEdit: () -> Unit,
    onClickTimer: () -> Unit,
    modifier: Modifier = Modifier
) {
    BottomAppBar(modifier = modifier, floatingActionButton = {
        FloatingActionButton(onClick = onClickEdit) {
            Icon(
                imageVector = Icons.Rounded.Edit,
                contentDescription = stringResource(id = R.string.common_edit)
            )
        }
    }, actions = {
        IconButton(onClick = onNavIconClick) {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = stringResource(id = R.string.common_go_to_back)
            )
        }
        IconButton(onClick = onClickDelete) {
            Icon(
                imageVector = Icons.Rounded.Delete,
                contentDescription = stringResource(id = R.string.common_delete)
            )
        }
        IconButton(onClick = onClickReset) {
            Icon(
                imageVector = Icons.Rounded.Refresh,
                contentDescription = stringResource(id = R.string.common_reset)
            )
        }
        IconButton(onClick = onClickTimer) {
            Icon(
                painter = painterResource(id = R.drawable.ic_stopwatch),
                contentDescription = stringResource(
                    id = R.string.common_timer
                )
            )
        }
    })
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
            override val resource: Flow<Resource<Training>> =
                flow { emit(Resource.Success(training)) }

            override fun setLoading() {}

            override suspend fun getTraining(id: String) {}

            override fun updateExercise(exerciseId: String, isChecked: Boolean) {}

            override fun resetExercises() {}

            override suspend fun removeTraining(trainingId: String) {}

            override suspend fun updateTraining(trainingId: String) {}

        }
        TrainingLogScreen(onNavIconClick = {},
            trainingId = "",
            viewModel = viewModel,
            onError = {},
            onClickDelete = {},
            onBackPressed = {},
            onClickEdit = {})
    }
}

