package com.example.gymlog.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymlog.R
import com.example.gymlog.ui.theme.GymLogTheme
import com.example.gymlog.utils.TrainingTypes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipSelectionList(
    selectedList: List<String>,
    filterList: List<String>,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    countOfRows: Int = 3
) {
    OutlinedCard(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            title?.let {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(
                        vertical = dimensionResource(
                            id = R.dimen.default_padding
                        )
                    ),
                    textAlign = TextAlign.Center
                )
            }
            LazyHorizontalGrid(
                rows = GridCells.Fixed(countOfRows),
                modifier = modifier.heightIn(max = 120.dp),
                contentPadding = PaddingValues(horizontal = dimensionResource(id = R.dimen.default_padding)),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.default_padding)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.default_padding))

            ) {
                items(filterList) { filter ->
                    val isSelected = selectedList.contains(filter)
                    FilterChip(
                        selected = isSelected,
                        onClick = { onClick(filter) },
                        label = {
                            Text(
                                text = filter
                            )
                        },
                        leadingIcon = if (isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null,
                                    Modifier.size(
                                        dimensionResource(id = R.dimen.default_chip_icon_size)
                                    )
                                )
                            }
                        } else {
                            null
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FilterChipSelectionListPreview() {
    val filters = TrainingTypes.values().map { stringResource(id = it.stringRes()) }
    val selectedList = remember {
        mutableStateListOf<String>()
    }

    GymLogTheme {
        FilterChipSelectionList(
            title = "Filtros",
            filterList = filters,
            selectedList = selectedList,
            onClick = {
                if (!selectedList.contains(it)) {
                    selectedList.add(it)
                } else {
                    selectedList.remove(it)
                }
            },
            modifier = Modifier.padding(8.dp)
        )
    }
}