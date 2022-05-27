package fhnw.emoba.thatsapp.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fhnw.emoba.freezerapp.ui.theme.ThatsAppTheme
import fhnw.emoba.thatsapp.data.Contact
import fhnw.emoba.thatsapp.model.Screen
import fhnw.emoba.thatsapp.model.ThatsAppModel
import fhnw.emoba.thatsapp.ui.ContactListTopBar
import fhnw.emoba.thatsapp.ui.NoChatAvailableMessage
import fhnw.emoba.thatsapp.ui.Notification
import fhnw.emoba.thatsapp.ui.ThumbnailProfilePic

@Composable
fun ContactListScreen(model: ThatsAppModel) {
    val scaffoldState = rememberScaffoldState()
    ThatsAppTheme(model.isDarkTheme) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { ContactListTopBar(model, scaffoldState) },
            drawerContent = { ProfileScreen(model) },
            content = { ContactListScreenContent(model) }
        )
        Notification(model, scaffoldState.snackbarHostState)
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun ContactListScreenContent(model: ThatsAppModel) {
    with(model) {
        when {
            allContacts.isEmpty() -> {
                NoChatAvailableMessage("Nobody is currently online")
            }
            else -> {
                val scrollState = rememberLazyListState()
                LazyColumn(
                    state = scrollState,
                ) {
                    val groupedContacts = allContacts.groupBy { it.name[0] }
                    groupedContacts.forEach { (initial, allContacts) ->
                        stickyHeader {
                            CharacterHeader(initial)
                        }
                        items(allContacts) { contact -> ContactItem(contact, model) }
                    }
                }
            }
        }
    }
}

@Composable
fun CharacterHeader(letter: Char) {
        Text(
            "$letter",
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.primaryVariant)
                .fillMaxWidth()
                .padding(start = 20.dp, top = 5.dp, bottom = 5.dp),
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.h6
        )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ContactItem(contact: Contact, model: ThatsAppModel) {
    with(contact) {
        ListItem(
            modifier = Modifier
                .clickable {
                    model.currentScreen = Screen.CHAT
                    model.currentChat = model.goToChat(contact)
                    model.currentChatPartner = contact
                },
            text = {
                Text(
                    name,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            },
            icon = {
                ThumbnailProfilePic(
                    profilePic = profilePicture,
                    modifier = Modifier.size(width = 30.dp, height = 30.dp)
                )
            }
        )
        Divider()
    }
}