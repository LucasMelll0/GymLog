package com.devmello.gymlog.ui.home.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.devmello.gymlog.R

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DisposableFiltersList(
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