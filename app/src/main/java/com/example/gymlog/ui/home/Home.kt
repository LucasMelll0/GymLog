package com.example.gymlog.ui.home

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Search
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.example.gymlog.ui.components.DefaultSearchBar
import com.example.gymlog.ui.components.FilterChipSelectionList
import com.example.gymlog.ui.home.viewmodel.HomeViewModel
import com.example.gymlog.ui.theme.GymLogTheme
import com.example.gymlog.utils.TrainingTypes
import org.koin.androidx.compose.koinViewModel


@Composable
fun HomeScreen(
    onItemClickListener: (trainingId: String) -> Unit,
    onButtonAddClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel()
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var showSearchBar by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val training by viewModel.trainings.collectAsState(emptyList())
    Scaffold(bottomBar = {
        HomeBottomBar(
            onButtonSearchClick = { showSearchBar = !showSearchBar },
            onButtonFiltersClick = { showBottomSheet = !showBottomSheet },
            onFabClick = onButtonAddClick
        )
    }) { paddingValues ->

        Surface(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.default_padding)),
            ) {
                var query by remember { mutableStateOf("") }
                if (!showSearchBar) {
                    query = ""
                }
                AnimatedVisibility(showSearchBar) {
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
                    onItemClickListener = { training ->  onItemClickListener(training.trainingId)},
                    trainingWithExercises = training.filter {
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
            if (showBottomSheet) {
                FiltersBottomSheet(
                    selectedList = viewModel.filters,
                    filterList = TrainingTypes.values().map { stringResource(id = it.stringRes()) },
                    onFilterClick = { filter -> viewModel.manageFilters(filter) },
                    onDismissRequest = { showBottomSheet = false }
                )
            }
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
            modifier = modifier.fillMaxWidth()
        ) {
            Text(text = "Filtrar por tipo de treino", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.default_padding)))
            FilterChipSelectionList(
                modifier = Modifier.fillMaxWidth(),
                selectedList = selectedList,
                filterList = filterList,
                onClick = onFilterClick
            )
        }
    }

}

@Composable
fun HomeBottomBar(
    onButtonSearchClick: () -> Unit,
    onButtonFiltersClick: () -> Unit,
    onFabClick: () -> Unit
) {
    BottomAppBar(actions = {
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
    onItemClickListener: (Training) -> Unit,
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
                onClick = { onItemClickListener(training) },
                training = training,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.small_padding))
                    .animateItemPlacement()
            )
        }
    }
}

@Composable
fun TrainingItem(
    onClick: () -> Unit,
    training: Training,
    modifier: Modifier = Modifier
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    Card(
        modifier = modifier
            .clickable {
                onClick()
            }
            .fillMaxWidth()
            .animateContentSize(animationSpec = spring(Spring.DampingRatioLowBouncy)),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Card(colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(
                modifier = Modifier
                    .padding(horizontal = dimensionResource(id = R.dimen.default_padding))

            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(
                            min = dimensionResource(
                                id = R.dimen.minimum_training_item_height
                            )
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = training.title,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(
                            start = dimensionResource(
                                id = R.dimen.default_padding
                            )
                        )
                    )
                    if (training.exercises.isNotEmpty()) {
                        val angle: Float by animateFloatAsState(if (isExpanded) 180f else 0f)
                        IconButton(onClick = { isExpanded = !isExpanded }) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.rotate(angle)
                            )
                        }
                    }
                }
                if (training.filters.isNotEmpty()) {
                    FilterListTrainingItem(
                        filters = training.filters,
                        modifier = Modifier.padding(
                            vertical = dimensionResource(
                                id = R.dimen.default_padding
                            )
                        )
                    )
                }

            }
        }
        if (isExpanded) {
            ExerciseListTrainingItem(
                exercises = training.exercises,
                modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.default_padding))
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FilterListTrainingItem(filters: List<String>, modifier: Modifier = Modifier) {
    val maxHeight = if (filters.size < 8) 40.dp else 80.dp
    Surface(
        modifier = modifier
            .heightIn(max = maxHeight)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.primaryContainer.copy(0.1f)
    ) {
        val rows = if (filters.size < 8) 1 else 2
        FilterChipSelectionList(
            rows = rows,
            selectedList = emptyList(),
            filterList = filters,
            onClick = {},
            isEnabled = false
        )
    }
}

@Composable
private fun ExerciseListTrainingItem(exercises: List<Exercise>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.heightIn(max = 200.dp)) {
        items(exercises) { exercise ->
            ExerciseTrainingItem(
                exercise = exercise, modifier = Modifier.padding(
                    dimensionResource(id = R.dimen.small_padding)
                )
            )
        }
    }
}

@Composable
private fun ExerciseTrainingItem(exercise: Exercise, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(
                    id = R.dimen.small_padding
                )
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = exercise.title,
            style = MaterialTheme.typography.bodySmall,
        )
        Text(
            text = "${exercise.repetitions}X${exercise.series}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Suppress("UNCHECKED_CAST")
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun HomeScreenPreview() {
    GymLogTheme {
        val viewModelFactory = object : ViewModelProvider.NewInstanceFactory() {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repositoryImpl = TrainingRepositoryImpl(AppDataBase_Impl().trainingDao())
                return HomeViewModel(repositoryImpl) as T
            }
        }
        val viewModel: HomeViewModel = viewModel(factory = viewModelFactory)
        HomeScreen({}, viewModel = viewModel, onButtonAddClick = {})
    }
}
