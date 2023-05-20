package com.example.gymlog.ui.log

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymlog.R
import com.example.gymlog.database.AppDataBase_Impl
import com.example.gymlog.model.Training
import com.example.gymlog.repository.TrainingRepositoryImpl
import com.example.gymlog.ui.components.ExerciseList
import com.example.gymlog.ui.log.viewmodel.TrainingLogViewModel
import com.example.gymlog.ui.theme.GymLogTheme
import com.example.gymlog.utils.Resource
import org.koin.androidx.compose.koinViewModel

@Composable
fun TrainingLogScreen(
    onNavIconClick: () -> Unit,
    onError: () -> Unit,
    trainingId: String,
    modifier: Modifier = Modifier,
    viewModel: TrainingLogViewModel = koinViewModel()
) {
    var isLoading: Boolean by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = Unit) {
        viewModel.getTraining(trainingId)
    }

    Scaffold(bottomBar = {
        TrainingLogBottomAppBar(
            onNavIconClick = onNavIconClick,
            onClickDelete = { /*TODO*/ },
            onClickReset = { /*TODO*/ },
            onClickEdit = { /*TODO*/ })
    }) { paddingValues ->
        val resource = viewModel.training.collectAsState(Resource.Loading)
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
        } else {
            if (resource.value is Resource.Success) {
                ExerciseList(
                    modifier = modifier.padding(paddingValues),
                    exercises = (resource.value as Resource.Success<Training>)
                        .data.getExercisesWithMutableState(),
                    onCheckedChange = { _, _ -> })
            }
        }

    }

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
            onError = {}
        )
    }
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

@Preview
@Composable
private fun TrainingLogBottomAppBarPreview() {
    GymLogTheme {
        TrainingLogBottomAppBar(
            onClickEdit = {},
            onNavIconClick = {},
            onClickDelete = {},
            onClickReset = {})
    }
}