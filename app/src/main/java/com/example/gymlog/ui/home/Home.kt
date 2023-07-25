package com.example.gymlog.ui.home

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.ViewModel
import com.example.gymlog.R
import com.example.gymlog.data.Mock
import com.example.gymlog.extensions.checkConnection
import com.example.gymlog.model.Training
import com.example.gymlog.ui.components.DefaultAlertDialog
import com.example.gymlog.ui.components.DefaultSearchBar
import com.example.gymlog.ui.components.FilterChipList
import com.example.gymlog.ui.components.FilterChipSelectionList
import com.example.gymlog.ui.components.LoadingDialog
import com.example.gymlog.ui.home.viewmodel.HomeViewModel
import com.example.gymlog.ui.home.viewmodel.HomeViewModelImpl
import com.example.gymlog.ui.theme.GymLogTheme
import com.example.gymlog.utils.BackPressHandler
import com.example.gymlog.utils.TrainingTypes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.androidx.compose.koinViewModel


@Composable
fun HomeScreen(
    onItemClickListener: (trainingId: String) -> Unit,
    onClickEdit: (trainingId: String) -> Unit,
    onButtonAddClick: () -> Unit,
    onNavIconClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel<HomeViewModelImpl>()
) {
    val context = LocalContext.current
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var showFiltersBottomSheet by remember { mutableStateOf(false) }
    var showSearchBar by remember { mutableStateOf(false) }
    var bottomSheetMenuTraining: Training? by remember { mutableStateOf(null) }
    var showTrainingDeleteDialog: Boolean by remember { mutableStateOf(false) }
    val trainingIdForDelete: String? = bottomSheetMenuTraining?.trainingId
    val focusRequester = remember { FocusRequester() }
    val trainings by viewModel.trainings.collectAsState(emptyList())

    LaunchedEffect(Unit) {
        context.checkConnection {
            isLoading = true
            viewModel.sync()
            isLoading = false
        }
    }

    Scaffold(bottomBar = {
        HomeBottomBar(
            onButtonSearchClick = { showSearchBar = !showSearchBar },
            onButtonFiltersClick = { showFiltersBottomSheet = !showFiltersBottomSheet },
            onFabClick = onButtonAddClick,
            onNavIconClick = onNavIconClick
        )
    }, modifier = modifier) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) LoadingDialog(text = stringResource(id = R.string.common_synchronizing))
            if (trainings.isEmpty()) HomeEmptyListMessage()
            trainingIdForDelete?.let {
                if (showTrainingDeleteDialog) DeleteTrainingDialog(
                    onConfirm = {
                        viewModel.deleteTraining(it)
                        bottomSheetMenuTraining = null
                    },
                    onDismissRequest = { showTrainingDeleteDialog = false })
            }
            ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                val (column, disposableFilters) = createRefs()
                Column(
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.default_padding)),
                    modifier = Modifier
                        .constrainAs(column) {
                            top.linkTo(parent.top)
                            bottom.linkTo(disposableFilters.top)
                            height = Dimension.preferredWrapContent
                        }
                        .fillMaxWidth(),
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
                    modifier = Modifier.constrainAs(disposableFilters) {
                        bottom.linkTo(parent.bottom)
                    }) {
                    DisposableFiltersList(
                        filters = viewModel.filters,
                        onClick = { viewModel.manageFilters(it) }
                    )
                }

            }
        }
        bottomSheetMenuTraining?.let {
            TrainingMenuBottomSheet(
                onClickEdit = { onClickEdit(it.trainingId) },
                onClickDelete = { showTrainingDeleteDialog = true },
                onDismissRequest = { bottomSheetMenuTraining = null })
        }
        if (showFiltersBottomSheet) FiltersBottomSheet(
            selectedList = viewModel.filters,
            filterList = TrainingTypes.values()
                .map { stringResource(id = it.stringRes()) },
            onFilterClick = { filter -> viewModel.manageFilters(filter) },
            onDismissRequest = { showFiltersBottomSheet = false }
        )


    }

}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun DisposableFiltersList(
    filters: List<String>,
    onClick: (filter: String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyHorizontalStaggeredGrid(
        rows = StaggeredGridCells.Fixed(2),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = dimensionResource(id = R.dimen.filter_chip_list_max_height))
    ) {
        items(filters) { filter ->
            FilterChip(
                onClick = { onClick(filter) },
                selected = true,
                label = { Text(text = filter) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = null
                    )
                },
                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding))
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersBottomSheet(
    selectedList: List<String>,
    filterList: List<String>,
    onFilterClick: (String) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = dimensionResource(id = R.dimen.large_padding))
        ) {
            Text(
                text = stringResource(id = R.string.home_filters_bottom_sheet_title),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.default_padding)))
            FilterChipSelectionList(
                selectedList = selectedList,
                filterList = filterList,
                onClick = onFilterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(id = R.dimen.default_padding))
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingMenuBottomSheet(
    onClickEdit: () -> Unit,
    onClickDelete: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(onDismissRequest = onDismissRequest) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.large_padding))
        ) {
            Text(
                text = stringResource(id = R.string.home_training_menu_bottom_sheet_title),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.default_padding)))
            Button(onClick = onClickEdit, modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(id = R.string.common_edit))
            }
            OutlinedButton(onClick = onClickDelete, modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(id = R.string.common_delete))
            }
        }
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

@Composable
fun HomeBottomBar(
    onButtonSearchClick: () -> Unit,
    onButtonFiltersClick: () -> Unit,
    onFabClick: () -> Unit,
    onNavIconClick: () -> Unit
) {
    BottomAppBar(actions = {
        IconButton(onClick = onNavIconClick) {
            Icon(
                imageVector = Icons.Rounded.Menu,
                contentDescription = stringResource(id = R.string.common_open_navigation_drawer)
            )
        }
        IconButton(onClick = onButtonSearchClick) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = stringResource(id = R.string.home_button_search_content_description)
            )
        }
        IconButton(onClick = onButtonFiltersClick) {
            Icon(
                painter = painterResource(id = R.drawable.ic_filter),
                contentDescription = stringResource(id = R.string.home_button_filter_content_description)
            )
        }
    }, floatingActionButton = {
        FloatingActionButton(onClick = onFabClick) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = stringResource(id = R.string.home_button_add_content_description)
            )
        }
    })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrainingList(
    onLongClickListener: (Training) -> Unit,
    onClickListener: (Training) -> Unit,
    trainingWithExercises: List<Training>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = dimensionResource(id = R.dimen.small_padding)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.default_padding))
    ) {
        items(
            trainingWithExercises,
            key = { training -> training.trainingId }
        ) { training ->
            TrainingItem(
                onClick = { onClickListener(training) },
                onLongClick = { onLongClickListener(training) },
                training = training,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.small_padding))
                    .animateItemPlacement(),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrainingItem(
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    training: Training,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = modifier.combinedClickable(onClick = onClick, onLongClick = onLongClick)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.default_padding)),
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.large_padding))
                .fillMaxWidth()
                .heightIn(
                    min = dimensionResource(
                        id = R.dimen.minimum_training_item_height
                    )
                )
        ) {
            Text(text = training.title, style = MaterialTheme.typography.titleLarge)
            ProvideTextStyle(value = MaterialTheme.typography.bodyLarge) {
                if (training.exercises.isNotEmpty()) {
                    Text(
                        text = stringResource(
                            id = R.string.home_training_item_exercises_count_place_holder,
                            training.exercises.size
                        ),
                    )
                    Text(
                        text = stringResource(
                            id = R.string.home_training_item_estimated_time_place_holder,
                            training.getEstimatedTime()
                        )
                    )
                } else {
                    Text(text = stringResource(id = R.string.home_training_item_empty_exercises_message))
                }
            }

            if (training.filters.isNotEmpty()) {
                val filtersSize = training.filters.size
                FilterChipList(
                    rows = if (filtersSize < 4) 1 else 2,
                    filterList = training.filters,
                    modifier = Modifier
                        .padding(
                            vertical = dimensionResource(
                                id = R.dimen.default_padding
                            )
                        )
                        .heightIn(max = if (filtersSize < 4) 40.dp else 80.dp)
                )
            }

        }
    }
}

@Preview
@Composable
private fun TrainingItemPreview() {
    GymLogTheme {
        TrainingItem(
            onClick = { },
            training = Mock.getTrainings().random(),
            onLongClick = {})
    }
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

            override fun deleteTraining(trainingId: String) {
                TODO("Not yet implemented")
            }

            override suspend fun sync() {
                TODO("Not yet implemented")
            }
        }
        HomeScreen(
            onItemClickListener = {},
            viewModel = viewModel,
            onButtonAddClick = {},
            onClickEdit = {},
            onNavIconClick = {}
        )
    }
}

@Composable
private fun HomeEmptyListMessage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(shape = MaterialTheme.shapes.extraLarge) {
            Icon(
                painter = painterResource(id = R.drawable.ic_empty),
                contentDescription = null,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.large_padding))
                    .size(dimensionResource(id = R.dimen.empty_list_icon_size))
            )
        }
        Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.large_padding)))
        Text(
            text = stringResource(id = R.string.home_empty_list_message),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.large_padding))
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeEmptyListMessagePreview() {
    GymLogTheme {
        HomeEmptyListMessage()
    }
}
