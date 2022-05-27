package fhnw.emoba.thatsapp.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

class Chat(
    val chatPartner: Contact,
    var messages: SnapshotStateList<Message> = mutableStateListOf()
)