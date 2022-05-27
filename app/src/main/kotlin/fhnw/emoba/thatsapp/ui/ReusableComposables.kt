package fhnw.emoba.thatsapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fhnw.emoba.R
import fhnw.emoba.thatsapp.data.toImageResource
import fhnw.emoba.thatsapp.model.ProfilePicture
import fhnw.emoba.thatsapp.model.Screen
import fhnw.emoba.thatsapp.model.ThatsAppModel
import kotlinx.coroutines.launch

@Composable
fun ChatListTopBar(scaffoldState: ScaffoldState, model: ThatsAppModel) {
    TopAppBar(title = {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Chats")
            IconToggleButton(
                checked = model.isDarkTheme,
                onCheckedChange = { model.toggleTheme() }) {
                if (model.isDarkTheme) {
                    Icon(
                        imageVector = Icons.Filled.DarkMode,
                        contentDescription = "DarkMode"
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.DarkMode,
                        contentDescription = "LightMode"
                    )
                }
            }
        }
    },
        navigationIcon = {
            DrawerIcon(scaffoldState = scaffoldState)
        }
    )
}

@Composable
fun ContactListTopBar(model: ThatsAppModel, scaffoldState: ScaffoldState) {
    with(model) {
        TopAppBar(
            title = { Text("Contacts") },
            navigationIcon = {
                DrawerIcon(scaffoldState = scaffoldState)
            },
            actions = {
                IconButton(onClick = {
                    currentScreen = Screen.CHATLIST
                }) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        "Back to home screen",
                        tint = if (isDarkTheme) {
                            MaterialTheme.colors.onBackground
                        } else {
                            MaterialTheme.colors.onPrimary
                        }
                    )
                }
            }
        )
    }
}

@Composable
fun ChatTopBar(model: ThatsAppModel) {
    with(model) {
        TopAppBar(
            title = {
                ThumbnailProfilePic(
                    currentChatPartner!!.profilePicture,
                    Modifier.size(40.dp, 40.dp)
                )
                Spacer(modifier = Modifier.padding(end = 10.dp))
                Text(
                    currentChatPartner!!.name,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            },
            navigationIcon = {
                IconButton(onClick = { currentScreen = Screen.CHATLIST }) {
                    Icon(Icons.Filled.ArrowBack, "Back to home screen")
                }
            }
        )
    }
}

@Composable
fun DrawerIcon(scaffoldState: ScaffoldState) {
    val scope = rememberCoroutineScope()
    IconButton(onClick = { scope.launch { scaffoldState.drawerState.open() } }) {
        Icon(Icons.Filled.Menu, "Menu")
    }
}

@Composable
fun NoChatAvailableMessage(message: String) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(message, style = MaterialTheme.typography.h5)
    }
}

@Composable
fun ThumbnailProfilePic(profilePic: ProfilePicture, modifier: Modifier) {
    Box(modifier = modifier) {
        Image(
            bitmap = ImageBitmap
                .imageResource(id = R.drawable.white_background),
            "Profile Picture Thumbnail",
            modifier = Modifier
                .clip(RoundedCornerShape(100))
                .fillMaxWidth(),
            contentScale = ContentScale.Crop,
        )
        Image(
            bitmap = ImageBitmap
                .imageResource(id = toImageResource(profilePic)),
            "Profile Picture Thumbnail",
            modifier = Modifier
                .clip(RoundedCornerShape(100))
                .fillMaxWidth(),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
fun Notification(model: ThatsAppModel, snackbarState: SnackbarHostState) {
    with(model) {
        if (notificationMessage.isNotBlank()) {
            LaunchedEffect(
                snackbarState
            ) {
                snackbarState.showSnackbar(
                    message = notificationMessage,
                    actionLabel = "Dismiss",
                )
                notificationMessage = ""
            }
        }
    }
}
