package fhnw.emoba.thatsapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fhnw.emoba.freezerapp.ui.theme.ThatsAppTheme
import fhnw.emoba.thatsapp.data.Chat
import fhnw.emoba.thatsapp.data.millisToDate
import fhnw.emoba.thatsapp.model.MessageType
import fhnw.emoba.thatsapp.model.Screen
import fhnw.emoba.thatsapp.model.ThatsAppModel
import fhnw.emoba.thatsapp.ui.ChatListTopBar
import fhnw.emoba.thatsapp.ui.NoChatAvailableMessage
import fhnw.emoba.thatsapp.ui.Notification
import fhnw.emoba.thatsapp.ui.ThumbnailProfilePic

@Composable
fun ChatListScreen(model: ThatsAppModel) {
    val scaffoldState = rememberScaffoldState()

    ThatsAppTheme(model.isDarkTheme) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { ChatListTopBar(scaffoldState, model) },
            drawerContent = { ProfileScreen(model) },
            floatingActionButton = { FAB(model) },
            floatingActionButtonPosition = FabPosition.End,
            isFloatingActionButtonDocked = true,
            content = { ChatListScreenContent(model) }
        )
        Notification(model, scaffoldState.snackbarHostState)
    }
}

@Composable
fun FAB(model: ThatsAppModel) {
    with(model) {
        FloatingActionButton(
            onClick = {
                currentScreen = Screen.CONTACTLIST
            },
            backgroundColor = MaterialTheme.colors.primary
        ) { Icon(Icons.Filled.Chat, "New chat", tint = MaterialTheme.colors.onPrimary) }
    }
}

@Composable
fun ChatListScreenContent(model: ThatsAppModel) {
    with(model) {
        when {
            allChats.isEmpty() -> {
                NoChatAvailableMessage("You have no chats")
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp)
                ) {
                    items(allChats) { ChatItem(it, model) }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatItem(chat: Chat, model: ThatsAppModel) {
    with(chat) {
        ListItem(
            modifier = Modifier.clickable {
                model.currentScreen = Screen.CHAT
                model.currentChat = chat
                model.currentChatPartner = chatPartner
            },
            text = {
                Text(
                    chatPartner.name,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            },
            secondaryText = {
                if (!chat.messages.isEmpty()) {
                    when (chat.messages.last().type) {
                        MessageType.PLAINTEXT -> {
                            Text(
                                chat.messages.last().message!!,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        }
                        MessageType.IMAGE -> {
                            Text(text = "Sent an image " + "🖼")
                        }

                        MessageType.GEOPOSITION -> {
                            Text(text = "Sent a location " + "📍")
                        }
                    }
                }
            },
            icon = {
                ThumbnailProfilePic(
                    profilePic = chatPartner.profilePicture,
                    modifier = Modifier.size(width = 50.dp, height = 50.dp)
                )
            },
            trailing = {
                if (!chat.messages.isEmpty()) {
                    Column(modifier = Modifier.fillMaxHeight()) {
                        Text(text = millisToDate(messages.last().time, "dd.MM.yy"))
                    }
                }
            },
        )
    }
    Divider()
}

