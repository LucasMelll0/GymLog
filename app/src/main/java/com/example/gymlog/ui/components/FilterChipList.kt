package com.example.gymlog.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FilterChipSelectionList(
    selectedList: List<String>,
    filterList: List<String>,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    rows: Int = 2,
    title: String? = null,
    description: String? = null,
    isEnabled: Boolean = true
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.wrapContentHeight()
    ) {
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
        description?.let {
            Text(
                text = description,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
            )
        }
        LazyHorizontalStaggeredGrid(
            rows = StaggeredGridCells.Fixed(rows),
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.default_padding))
                .fillMaxWidth()
                .heightIn(max = 80.dp),
            contentPadding = PaddingValues(horizontal = dimensionResource(id = R.dimen.default_padding)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.default_padding))

        ) {
            items(filterList) { filter ->
                val isSelected = selectedList.contains(filter)
                FilterChip(enabled = isEnabled,
                    colors = FilterChipDefaults.filterChipColors(
                        disabledContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                            0.5f
                        ),
                        disabledLabelColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.small_padding)),
                    selected = isSelected,
                    onClick = { onClick(filter) },
                    label = {
                        Text(
                            text = filter, textAlign = TextAlign.Center
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
                    })
            }
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true)
@Composable
private fun FilterChipSelectionListPreview() {
    val filters = TrainingTypes.values().map { stringResource(id = it.stringRes()) }
    val selectedList = remember {
        mutableStateListOf<String>()
    }

    GymLogTheme {
        FilterChipSelectionList(
            isEnabled = false,
            selectedList = selectedList,
            filterList = filters,
            onClick = {
                if (!selectedList.contains(it)) {
                    selectedList.add(it)
                } else {
                    selectedList.remove(it)
                }
            },
            modifier = Modifier.padding(8.dp),
            title = "Filtros",
            description = "Marque quantos o treino se enquadrar"
        )
    }
}