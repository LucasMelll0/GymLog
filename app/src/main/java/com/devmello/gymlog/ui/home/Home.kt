package com.devmello.gymlog.ui.home

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import com.devmello.gymlog.R
import com.devmello.gymlog.core.ui.ScaffoldConfig
import com.devmello.gymlog.core.ui.ScaffoldManager
import com.devmello.gymlog.data.Mock
import com.devmello.gymlog.extensions.checkConnection
import com.devmello.gymlog.model.Training
import com.devmello.gymlog.ui.components.DefaultAlertDialog
import com.devmello.gymlog.ui.components.DefaultSearchBar
import com.devmello.gymlog.ui.home.components.DisposableFiltersList
import com.devmello.gymlog.ui.home.components.FiltersBottomSheet
import com.devmello.gymlog.ui.home.components.HomeEmptyListMessage
import com.devmello.gymlog.ui.home.components.TrainingList
import com.devmello.gymlog.ui.home.components.TrainingListShimmer
import com.devmello.gymlog.ui.home.components.TrainingMenuBottomSheet
import com.devmello.gymlog.ui.home.viewmodel.HomeViewModel
import com.devmello.gymlog.ui.home.viewmodel.HomeViewModelImpl
import com.devmello.gymlog.ui.theme.GymLogTheme
import com.devmello.gymlog.utils.BackPressHandler
import com.devmello.gymlog.utils.TrainingTypes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@Composable
fun HomeScreen(
    scaffoldManager: ScaffoldManager,
    onItemClickListener: (trainingId: String) -> Unit,
    onClickEdit: (trainingId: String) -> Unit,
    onButtonAddClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel<HomeViewModelImpl>()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var showFiltersBottomSheet by remember { mutableStateOf(false) }
    var showSearchBar by remember { mutableStateOf(false) }
    var bottomSheetMenuTraining: Training? by remember { mutableStateOf(null) }
    var showTrainingDeleteDialog: Boolean by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val trainings by viewModel.trainings.collectAsState(emptyList())

    scaffoldManager.updateConfig(
        newConfig = ScaffoldConfig(
            tobBarActions = {
                IconButton(onClick = { showSearchBar = !showSearchBar }) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = stringResource(id = R.string.home_button_search_content_description)
                    )
                }
                IconButton(onClick = { showFiltersBottomSheet = !showFiltersBottomSheet }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_filter),
                        contentDescription = stringResource(id = R.string.home_button_filter_content_description)
                    )
                }
            },
            fab = {
                FloatingActionButton(onClick = onButtonAddClick) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = stringResource(id = R.string.home_button_add_content_description)
                    )
                }
            }
        )
    )

    LaunchedEffect(Unit) {  // TODO Tentar alguma forma de refatorar isso
        context.checkConnection {
            isLoading = true
            viewModel.sync()
            isLoading = false
        }
    }

    Box(modifier = modifier) {
        if (isLoading) TrainingListShimmer(Modifier.padding(dimensionResource(id = R.dimen.small_padding)))
        if (trainings.isEmpty() && !isLoading) HomeEmptyListMessage()
        bottomSheetMenuTraining?.let {
            if (showTrainingDeleteDialog) DeleteTrainingDialog(
                onConfirm = {
                    scope.launch {
                        isLoading = true
                        viewModel.deleteTraining(it.trainingId)
                        bottomSheetMenuTraining = null
                        showTrainingDeleteDialog = false
                        isLoading = false
                    }
                },
                onDismissRequest = { showTrainingDeleteDialog = false })
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.default_padding)),
            modifier = Modifier
                .fillMaxSize(),
        ) {
            var query by remember { mutableStateOf("") }
            if (!showSearchBar) {
                query = ""
            }
            AnimatedVisibility(showSearchBar) {
                BackPressHandler {
                    showSearchBar = false
                }
                DefaultSearchBar(
                    focusRequester = focusRequester,
                    value = query,
                    onClickBackButton = {
                        showSearchBar = false
                    },
                    onClickClearText = { query = "" },
                    onValueChanged = { query = it }
                )

            }
            TrainingList(
                onLongClickListener = { bottomSheetMenuTraining = it },
                onClickListener = { training -> onItemClickListener(training.trainingId) },
                trainingWithExercises = trainings.filter {
                    if (query.isNotEmpty()) {
                        it.title.contains(query, true)
                    } else {
                        it.filters.containsAll(viewModel.filters)
                    }
                },
                modifier = Modifier
                    .padding(
                        vertical = dimensionResource(id = R.dimen.default_padding)
                    )
                    .fillMaxHeight()
            )
        }

        AnimatedVisibility(
            viewModel.filters.isNotEmpty(),
            modifier = Modifier.align(alignment = Alignment.BottomCenter)
        ) {
            DisposableFiltersList(
                filters = viewModel.filters,
                onClick = { viewModel.manageFilters(it) }
            )
        }

        bottomSheetMenuTraining?.let {
            TrainingMenuBottomSheet(
                modifier = Modifier.align(alignment = Alignment.BottomCenter),
                onClickEdit = { onClickEdit(it.trainingId) },
                onClickDelete = { showTrainingDeleteDialog = true },
                onDismissRequest = { bottomSheetMenuTraining = null })
        }
        if (showFiltersBottomSheet) FiltersBottomSheet(
            modifier = Modifier.align(alignment = Alignment.BottomCenter),
            selectedList = viewModel.filters,
            filterList = TrainingTypes.entries
                .map { stringResource(id = it.stringRes()) },
            onFilterClick = { filter -> viewModel.manageFilters(filter) },
            onDismissRequest = { showFiltersBottomSheet = false }
        )
    }
}


@Composable
private fun DeleteTrainingDialog(
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit
) {
    DefaultAlertDialog(
        title = stringResource(id = R.string.common_dialog_title),
        text = stringResource(id = R.string.common_training_delete_dialog_text),
        icon = { Icon(imageVector = Icons.Rounded.Delete, contentDescription = null) },
        onDismissRequest = onDismissRequest,
        onConfirm = onConfirm
    )
}


@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun HomeScreenPreview() {
    GymLogTheme {
        val trainings = Mock.getTrainings()
        val viewModel = object : HomeViewModel, ViewModel() {
            private val _trainings: MutableStateFlow<List<Training>> = MutableStateFlow(trainings)
            override val trainings: Flow<List<Training>> get() = _trainings

            private val _filters: MutableList<String> = remember { mutableStateListOf() }
            override val filters: List<String>
                get() = _filters

            override fun manageFilters(filter: String) {
                if (!filters.contains(filter)) _filters.add(filter) else _filters.remove(filter)
            }

            override suspend fun deleteTraining(trainingId: String) {}

            override suspend fun sync() {}
        }
        HomeScreen(
            scaffoldManager = ScaffoldManager(),
            onItemClickListener = {},
            viewModel = viewModel,
            onButtonAddClick = {},
            onClickEdit = {}
        )
    }
}
