package fhnw.emoba.thatsapp.ui

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import fhnw.emoba.freezerapp.ui.theme.ThatsAppTheme
import fhnw.emoba.thatsapp.model.Screen
import fhnw.emoba.thatsapp.model.ThatsAppModel
import fhnw.emoba.thatsapp.ui.screens.ChatScreen
import fhnw.emoba.thatsapp.ui.screens.ContactListScreen
import fhnw.emoba.thatsapp.ui.screens.ChatListScreen
import fhnw.emoba.thatsapp.ui.screens.ProfileScreen

@Composable
fun AppUI(model: ThatsAppModel) {
    with(model) {
        ThatsAppTheme(isDarkTheme) {
            Crossfade(targetState = currentScreen) { screen ->
                when (screen) {
                    Screen.CHATLIST -> {
                        ChatListScreen(model)
                    }
                    Screen.CHAT -> {
                        ChatScreen(model)
                    }
                    Screen.CONTACTLIST -> {
                        ContactListScreen(model)
                    }
                    Screen.PROFILE -> {
                        ProfileScreen(model)
                    }
                }
            }
        }
    }
}
