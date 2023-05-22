package com.example.gymlog.ui.log

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymlog.R
import com.example.gymlog.database.AppDataBase_Impl
import com.example.gymlog.repository.TrainingRepositoryImpl
import com.example.gymlog.ui.components.DefaultAlertDialog
import com.example.gymlog.ui.components.FilterChipSelectionList
import com.example.gymlog.ui.log.viewmodel.TrainingLogViewModel
import com.example.gymlog.ui.theme.GymLogTheme
import com.example.gymlog.utils.BackPressHandler
import com.example.gymlog.utils.Resource
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
    viewModel: TrainingLogViewModel = koinViewModel()
) {
    var isLoading: Boolean by remember {
        mutableStateOf(false)
    }
    var showResetDialog: Boolean by remember {
        mutableStateOf(false)
    }
    var showDeleteDialog: Boolean by remember {
        mutableStateOf(false)
    }
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

    Scaffold(
        bottomBar = {
            TrainingLogBottomAppBar(
                onNavIconClick = {
                    scope.launch {
                        viewModel.updateTraining(trainingId)
                        onNavIconClick()
                    }
                },
                onClickDelete = { showDeleteDialog = true },
                onClickReset = { showResetDialog = true },
                onClickEdit = { onClickEdit(trainingId) }
            )
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
            ResetExercisesDialog(
                onConfirm = {
                    viewModel.resetExercises()
                    showResetDialog = false
                },
                onDismiss = { showResetDialog = false }
            )
        }
        val resource = viewModel.resource.collectAsState(Resource.Loading)
        when (resource.value) {
            is Resource.Loading -> {
                isLoading = true
            }

            is Resource.Success -> {
                isLoading = false
            }

            else -> onError()
        }
        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(15.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(0.1f)
            )
        }
        if (resource.value is Resource.Success) {
            Surface(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier
                        .fillMaxSize()
                        .padding(top = dimensionResource(id = R.dimen.default_padding)),
                    verticalArrangement = Arrangement.SpaceBetween,

                    ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(
                            dimensionResource(id = R.dimen.default_padding)
                        )
                    ) {
                        TipCard(
                            tips = stringArrayResource(id = R.array.training_tips).toList(),
                            modifier = Modifier.padding(
                                horizontal = dimensionResource(
                                    id = R.dimen.default_padding
                                )
                            )
                        )
                        ExerciseList(
                            modifier = modifier.padding(dimensionResource(id = R.dimen.default_padding)),
                            exercises = viewModel.exercises,
                            onCheckedChange = { exercise, isChecked ->
                                viewModel.updateExercise(exercise.id, isChecked)
                            })
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(id = R.dimen.default_padding))
                    ) {
                        FilterChipSelectionList(
                            selectedList = emptyList(),
                            filterList = viewModel.filters,
                            onClick = {},
                            isEnabled = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TipCard(tips: List<String>, modifier: Modifier = Modifier) {
    Card(
        shape = MaterialTheme.shapes.large,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.default_padding)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.default_padding)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_tip),
                contentDescription = "Tip",
                modifier = Modifier.size(dimensionResource(id = R.dimen.default_icon_size))
            )
            Text(
                text = tips.random(),
                style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic)
            )
        }
    }
}

@Preview
@Composable
private fun TipCardPreview() {
    GymLogTheme {
        val tips = stringArrayResource(id = R.array.training_tips)
        TipCard(tips.toList())
    }
}

@Composable
private fun DeleteDialog(
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    DefaultAlertDialog(
        modifier = modifier,
        title = stringResource(id = R.string.common_dialog_title),
        text = stringResource(id = R.string.training_log_delete_dialog_text),
        icon = { Icon(imageVector = Icons.Rounded.Delete, contentDescription = null) },
        onDismissRequest = onDismiss,
        onConfirm = onConfirm
    )
}

@Composable
private fun ResetExercisesDialog(
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
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
private fun TrainingLogBottomAppBar(
    onNavIconClick: () -> Unit,
    onClickDelete: () -> Unit,
    onClickReset: () -> Unit,
    onClickEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    BottomAppBar(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = onClickEdit) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = stringResource(id = R.string.training_log_edit_content_description)
                )
            }
        },
        actions = {
            IconButton(onClick = onNavIconClick) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = stringResource(id = R.string.common_go_to_back)
                )
            }
            IconButton(onClick = onClickDelete) {
                Icon(imageVector = Icons.Rounded.Delete, contentDescription = null)
            }
            IconButton(onClick = onClickReset) {
                Icon(imageVector = Icons.Rounded.Refresh, contentDescription = "")
            }
        }
    )
}

@Suppress("UNCHECKED_CAST")
@Preview
@Composable
private fun TrainingLogScreenPreview() {
    GymLogTheme {
        val viewModelFactory = object : ViewModelProvider.NewInstanceFactory() {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repositoryImpl = TrainingRepositoryImpl(AppDataBase_Impl().trainingDao())
                return TrainingLogViewModel(repositoryImpl) as T
            }
        }
        val viewModel: TrainingLogViewModel = viewModel(factory = viewModelFactory)
        TrainingLogScreen(
            onNavIconClick = {},
            trainingId = "",
            viewModel = viewModel,
            onError = {},
            onClickDelete = {},
            onBackPressed = {},
            onClickEdit = {}
        )
    }
}

@Preview
@Composable
private fun TrainingLogBottomAppBarPreview() {
    GymLogTheme {
        TrainingLogBottomAppBar(
            onNavIconClick = {},
            onClickDelete = {},
            onClickReset = {},
            onClickEdit = {}
        )
    }
}