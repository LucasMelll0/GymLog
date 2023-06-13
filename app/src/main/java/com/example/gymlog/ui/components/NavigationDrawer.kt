package com.example.gymlog.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.gymlog.R
import com.example.gymlog.navigation.Bmi
import com.example.gymlog.navigation.Destination
import com.example.gymlog.navigation.Home
import com.example.gymlog.ui.theme.GymLogTheme

@Composable
fun DrawerBody(
    items: List<Destination>,
    modifier: Modifier = Modifier,
    onItemClick: (Destination) -> Unit,
    currentDestinationRoute: String,
    isOpen: Boolean
) {
    val bigCornerSize = dimensionResource(id = R.dimen.big_corner_size)
    val widthFraction by animateFloatAsState(
        if (isOpen) 0.8f else 0f,
        animationSpec = tween(durationMillis = 150)
    )
    Column(
        modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(topEnd = bigCornerSize, bottomEnd = bigCornerSize))
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxWidth(widthFraction)
    ) {
        LazyColumn(
            Modifier
                .fillMaxHeight()
        ) {
            items(items) {
                val isCurrentDestination = it.route == currentDestinationRoute
                DrawerBodyItem(destination = it, onItemClick = onItemClick, isCurrentDestination)
            }
        }
    }
}


@Composable
fun DrawerBodyItem(
    destination: Destination,
    onItemClick: (Destination) -> Unit,
    isCurrentDestination: Boolean
) {
    val containerColor =
        if (isCurrentDestination) MaterialTheme.colorScheme.primary.copy(0.8f) else Color.Transparent
    val contentColor =
        if (isCurrentDestination) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    Card(
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen.default_padding))
            .height(dimensionResource(id = R.dimen.default_drawer_item_height))
            .fillMaxWidth()
            .clickable { onItemClick(destination) },
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxHeight()) {
            Text(
                text = stringResource(id = destination.title),
                style = MaterialTheme.typography.titleMedium,
                color = contentColor,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.default_padding))
            )
        }
    }
}

@Composable
fun AppNavigationDrawer(
    currentDestinationRoute: String,
    onItemClick: (Destination) -> Unit,
    drawerState: DrawerState,
    gesturesEnabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val destinations = listOf(Home, Bmi)
    ModalNavigationDrawer(
        gesturesEnabled = gesturesEnabled,
        drawerContent = {
            DrawerBody(
                items = destinations,
                onItemClick = onItemClick,
                currentDestinationRoute = currentDestinationRoute,
                isOpen = drawerState.isOpen
            )
        },
        content = content,
        drawerState = drawerState,
        modifier = Modifier
            .fillMaxHeight()
    )
}

@Preview
@Composable
fun AppNavigationDrawerPreview() {
    GymLogTheme {
        AppNavigationDrawer(
            currentDestinationRoute = Home.route,
            onItemClick = {},
            drawerState = rememberDrawerState(initialValue = DrawerValue.Open)
        ) {

        }
    }
}