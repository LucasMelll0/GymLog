package com.example.gymlog.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExitToApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.gymlog.R
import com.example.gymlog.extensions.capitalizeAllWords
import com.example.gymlog.navigation.Bmi
import com.example.gymlog.navigation.Destination
import com.example.gymlog.navigation.DropdownTimer
import com.example.gymlog.navigation.Home
import com.example.gymlog.navigation.Stopwatch
import com.example.gymlog.navigation.UserProfile
import com.example.gymlog.ui.auth.authclient.UserData
import com.example.gymlog.ui.theme.GymLogTheme
import java.util.Date

@Composable
fun DrawerBody(
    items: List<Destination>,
    modifier: Modifier = Modifier,
    onItemClick: (Destination) -> Unit,
    currentDestinationRoute: String,
    isOpen: Boolean,
    onClickExit: () -> Unit,
    user: UserData?
) {
    val bigCornerSize = dimensionResource(id = R.dimen.large_corner_size)
    AnimatedVisibility(visible = isOpen, enter = slideInHorizontally { -it / 2 }) {
        ConstraintLayout(
            modifier
                .fillMaxHeight()
                .clip(RoundedCornerShape(topEnd = bigCornerSize, bottomEnd = bigCornerSize))
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxWidth(0.8f)
        ) {
            val (content, exitButton) = createRefs()
            Column(
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.default_padding))
                    .constrainAs(content) {
                        linkTo(parent.top, exitButton.top, bias = 0f)
                        height = Dimension.fillToConstraints
                    }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.default_padding))
                ) {
                    DefaultAsyncImage(
                        data = user?.profilePicture,
                        diskCacheKey = "user_image_${Date().time}",
                        error = painterResource(id = R.drawable.ic_person),
                        contentDescription = stringResource(id = R.string.user_profile_photo_content_description),
                        modifier = Modifier
                            .size(dimensionResource(id = R.dimen.navigation_drawer_user_photo_size))
                            .background(MaterialTheme.colorScheme.surface)
                            .clip(CircleShape)
                            .weight(0.25f)
                    )
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.weight(0.75f)
                    ) {
                        Text(
                            text = stringResource(id = R.string.app_name),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        user?.userName?.let { userName ->
                            Text(
                                text = stringResource(
                                    id = R.string.drawer_welcome_message,
                                    userName.capitalizeAllWords()
                                ),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.default_padding)))
                        }
                    }
                }
                Divider()
                Spacer(modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.default_padding)))
                LazyColumn() {
                    items(items) {
                        val isCurrentDestination = it.route == currentDestinationRoute
                        DrawerBodyItem(
                            destination = it, onItemClick = onItemClick, isCurrentDestination
                        )
                    }
                }
            }
            TextButton(
                onClick = onClickExit,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(id = R.dimen.default_padding))
                    .constrainAs(exitButton) {
                        linkTo(top = content.bottom, bottom = parent.bottom, bias = 0f)
                    },

                ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ExitToApp,
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
    }
}


@Composable
fun DrawerBodyItem(
    destination: Destination, onItemClick: (Destination) -> Unit, isCurrentDestination: Boolean
) {
    val containerColor =
        if (isCurrentDestination) MaterialTheme.colorScheme.primary.copy(0.8f) else Color.Transparent
    val contentColor =
        if (isCurrentDestination) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    Card(
        modifier = Modifier
            .padding(vertical = dimensionResource(id = R.dimen.default_padding))
            .height(dimensionResource(id = R.dimen.default_drawer_item_height))
            .fillMaxWidth()
            .clickable { onItemClick(destination) }, colors = CardDefaults.cardColors(
            containerColor = containerColor, contentColor = contentColor
        ), shape = MaterialTheme.shapes.large
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxHeight()) {
            destination.icon?.let {
                Icon(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    modifier = Modifier.padding(start = dimensionResource(id = R.dimen.default_padding))
                )
            }
            Text(text = destination.title?.let { stringResource(id = it) } ?: "",
                style = MaterialTheme.typography.titleMedium,
                color = contentColor,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding)))
        }
    }
}

@Composable
fun AppNavigationDrawer(
    currentDestinationRoute: String,
    onItemClick: (Destination) -> Unit,
    drawerState: DrawerState,
    gesturesEnabled: Boolean = true,
    user: UserData?,
    onClickExit: () -> Unit,
    content: @Composable () -> Unit
) {
    val destinations = listOf(Home, Bmi, DropdownTimer, Stopwatch, UserProfile)
    ModalNavigationDrawer(
        gesturesEnabled = gesturesEnabled, drawerContent = {
            DrawerBody(
                items = destinations,
                onItemClick = onItemClick,
                currentDestinationRoute = currentDestinationRoute,
                isOpen = drawerState.isOpen,
                onClickExit = onClickExit,
                user = user
            )
        }, content = content, drawerState = drawerState, modifier = Modifier.fillMaxHeight()
    )
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview
@Composable
fun AppNavigationDrawerPreview() {
    GymLogTheme {
        AppNavigationDrawer(
            currentDestinationRoute = Home.route,
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