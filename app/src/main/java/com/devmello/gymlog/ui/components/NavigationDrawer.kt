package com.devmello.gymlog.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.devmello.gymlog.R
import com.devmello.gymlog.core.model.UserData
import com.devmello.gymlog.extensions.capitalizeAllWords
import com.devmello.gymlog.navigation.BmiDestination
import com.devmello.gymlog.navigation.Destination
import com.devmello.gymlog.navigation.DropdownTimerDestination
import com.devmello.gymlog.navigation.HomeDestination
import com.devmello.gymlog.navigation.NavRoute
import com.devmello.gymlog.navigation.StopwatchDestination
import com.devmello.gymlog.navigation.UserProfileDestination
import com.devmello.gymlog.ui.theme.GymLogTheme
import java.util.Date

@Composable
fun NavigationDrawerHeader(user: UserData?, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.default_padding)),
        modifier = Modifier.padding(dimensionResource(R.dimen.default_padding))
            .clickable(true, onClick = onClick)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.weight(0.75f)
        ) {
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            user?.userName?.let { userName ->
                Text(
                    text = stringResource(
                        id = R.string.drawer_welcome_message,
                        userName.capitalizeAllWords()
                    ),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.default_padding)))
            }

        }
    }
}


@Composable
fun AppNavigationDrawer(
    currentDestinationRoute: NavRoute,
    onItemClick: (Destination) -> Unit,
    drawerState: DrawerState,
    gesturesEnabled: Boolean = true,
    user: UserData?,
    onClickExit: () -> Unit,
    content: @Composable () -> Unit
) {
    val destinations = listOf(
        HomeDestination, BmiDestination, DropdownTimerDestination,
        StopwatchDestination
    )
    ModalNavigationDrawer(
        gesturesEnabled = gesturesEnabled, drawerContent = {
            ModalDrawerSheet {
                NavigationDrawerHeader(user, onClick = { onItemClick(UserProfileDestination) })
                HorizontalDivider()
                LazyColumn {
                    items(destinations) { destination ->
                        NavigationDrawerItem(
                            label = {
                                val icon = destination.icon?.let { painterResource(it) }
                                val title = destination.title?.let { stringResource(it) } ?: ""
                                Row {
                                    if (icon != null) Icon(
                                        painter = icon,
                                        contentDescription = null
                                    )
                                    Text(text = title)
                                }
                            },
                            selected = currentDestinationRoute == destination.navRoute,
                            onClick = {
                                onItemClick(destination)
                            }
                        )
                    }
                }
                HorizontalDivider()
                TextButton(
                    onClick = onClickExit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dimensionResource(id = R.dimen.default_padding)),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ExitToApp,
                            contentDescription = null,
                            modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.default_padding))
                        )
                        Text(
                            text = stringResource(id = R.string.drawer_exit_button),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }, drawerState = drawerState, modifier = Modifier.fillMaxHeight()
    ) {
        content()
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview
@Composable
fun AppNavigationDrawerPreview() {
    GymLogTheme {
        AppNavigationDrawer(
            currentDestinationRoute = HomeDestination.navRoute,
            onItemClick = {},
            drawerState = rememberDrawerState(initialValue = DrawerValue.Open),
            onClickExit = {},
            user = UserData(
                uid = "",
                userName = "Lucas Mello",
                profilePicture = null,
            )
        ) {

        }
    }
}