package com.example.gymlog.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymlog.ui.theme.GymLogTheme
import com.example.gymlog.utils.TrainingTypes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipSelectionList(
    selectedList: List<String>,
    filterList: List<String>,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyHorizontalGrid(
        rows = GridCells.Fixed(2),

    ) {
        items(filterList) { filter ->
            FilterChip(
                selected = selectedList.contains(filter),
                onClick = { onClick(filter) },
                label = {
                    Text(
                        text = filter
                    )
                })
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