package com.example.gymlog.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.gymlog.R
import com.example.gymlog.navigation.Bmi
import com.example.gymlog.navigation.Destination
import com.example.gymlog.navigation.Home

@Composable
fun DrawerBody(
    items: List<Destination>,
    modifier: Modifier = Modifier,
    onItemClick: (Destination) -> Unit
) {
    LazyColumn(
        modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        items(items) {
            Row(
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.default_padding))
                    .fillMaxWidth(0.6f)
                    .clickable { onItemClick(it) },
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = it.title),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun AppNavigationDrawer(
    onItemClick: (Destination) -> Unit,
    drawerState: DrawerState,
    content: @Composable () -> Unit
) {
    val destinations = listOf(Home, Bmi)
    ModalNavigationDrawer(
        drawerContent = { DrawerBody(items = destinations, onItemClick = onItemClick) },
        content = content,
        drawerState = drawerState,
        modifier = Modifier.fillMaxHeight()
    )
}