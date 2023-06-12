package com.example.gymlog.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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
    currentDestinationRoute: String
) {
    val bigCornerSize = dimensionResource(id = R.dimen.big_corner_size)
    Column(
        modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(topEnd = bigCornerSize, bottomEnd = bigCornerSize))
            .background(MaterialTheme.colorScheme.surface)
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
            .fillMaxWidth(0.8f)
            .clickable { onItemClick(destination) },
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Text(
            text = stringResource(id = destination.title),
            style = MaterialTheme.typography.titleMedium,
            color = contentColor,
            modifier = Modifier.padding(dimensionResource(id = R.dimen.large_padding))
        )
    }
}

@Composable
fun AppNavigationDrawer(
    currentDestinationRoute: String,
    onItemClick: (Destination) -> Unit,
    drawerState: DrawerState,
    content: @Composable () -> Unit
) {
    val destinations = listOf(Home, Bmi)
    ModalNavigationDrawer(
        drawerContent = {
            DrawerBody(
                items = destinations,
                onItemClick = onItemClick,
                currentDestinationRoute = currentDestinationRoute
            )
        },
        content = content,
        drawerState = drawerState,
        modifier = Modifier.fillMaxHeight()
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