package fhnw.emoba.thatsapp.model

import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.AndroidUriHandler
import fhnw.emoba.thatsapp.data.*
import fhnw.emoba.thatsapp.data.fileIO.CameraAppConnector
import fhnw.emoba.thatsapp.data.fileIO.downloadBitmapFromFileIO
import fhnw.emoba.thatsapp.data.fileIO.uploadBitmapToFileIO
import fhnw.emoba.thatsapp.data.gps.GPSConnector
import fhnw.emoba.thatsapp.data.gps.GeoPosition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.streams.toList

class ThatsAppModel(
    private val context: ComponentActivity,
    private val locator: GPSConnector,
    private val cameraAppConnector: CameraAppConnector
) {
    private val modelScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // General
    var isDarkTheme by mutableStateOf(true)
    var currentScreen by mutableStateOf(Screen.CHATLIST)
    var notificationMessage by mutableStateOf("")

    // Profile
    var saved by mutableStateOf(false)
    var changed by mutableStateOf(false)

    // MQTT
    private val mqttBroker = "broker.hivemq.com"
    private val messageTopic = "fhnw/emoba/thatsappJAS/"
    private val contactTopic = "fhnw/emoba/thatsappJAS/contacts/"
    private val mqttConnector by lazy { MqttConnector(mqttBroker) }


    var allChats = mutableStateListOf<Chat>()
    var allContacts = mutableStateListOf<Contact>()

    // Message
    var currentMessageType by mutableStateOf(MessageType.PLAINTEXT)
    var currentMessage by mutableStateOf<String?>("")
    var currentGeoPosition by mutableStateOf(
        GeoPosition(
            0.0,
            0.0,
            0.0
        )
    )
    var currentChatPartner by mutableStateOf<Contact?>(null)
    var currentChat by mutableStateOf<Chat?>(null)

    // Photo
    var currentPhotoURL by mutableStateOf<String?>(null)
    var currentPhoto by mutableStateOf<Bitmap?>(null)

    // "me"
    var currentUserName by mutableStateOf("")
    var currentProfilePicture by mutableStateOf(ProfilePicture.BOB)


    // Establish connection
    fun connect() {
        mqttConnector.connect()
    }

    // Subscribe for messages and contact
    fun subscribe() {
        // Message
        mqttConnector.subscribe(topic = messageTopic + currentUserName,
            onNewMessage = {
                onNewMessage(it)
            })

        // Contact
        mqttConnector.subscribe(
            topic = "$contactTopic#",
            onNewMessage = {
                onNewContact(it)
            })
    }

    // Message
    private fun onNewMessage(it: JSONObject) {
        val newMessage = Message(it)

        // Save image as imageBitmap to the message
        if (newMessage.type == MessageType.IMAGE && newMessage.image != "") {
            downloadImageForMessage(newMessage)
        } else {
            processMessage(newMessage)
        }
    }

    private fun processMessage(message: Message) {
        val allNames = allContacts.stream().map { contact -> contact.name }.toList()
        val senderName = message.sender.name

        // Sender doesn't exists in allContacts list
        if (!allNames.contains(senderName)) {

            // Create new contact
            val newContact = message.sender

            // Add contact to contact list
            allContacts.add(newContact)
            allContacts.sortBy { contact -> contact.name[0] }

            // Create new empty message list and add message
            val newMessagesList = mutableStateListOf<Message>()
            newMessagesList.add(message)

            // Create new chat and add to allChat list
            val newChat = Chat(newContact, newMessagesList)
            allChats.add(newChat)


        } else { // Sender already exists in allContacts list

            // Find chat of sender
            val chat =
                allChats.find { chat -> chat.chatPartner.name == senderName }

            // Chat exists: Add message to message list of chat
            if (chat != null) {
                chat.messages.add(message)
            } else { // Chat doesn't exist:

                // Create new empty message list and add message
                val newMessagesList = mutableStateListOf<Message>()
                newMessagesList.add(message)

                // Find contact in allContacts list
                val oldContact =
                    allContacts.find { contact -> contact.name == message.sender.name }

                // Create new chat with oldContact and message list
                val newChat = Chat(oldContact!!, newMessagesList)

                // Add chats to allChat list
                allChats.add(newChat)
            }
        }
    }

    fun publishMessage() {
        // Create new message
        val message =
            Message(
                type = currentMessageType,
                sender = Contact(currentUserName, currentProfilePicture),
                receiver = currentChatPartner!!,
                time = System.currentTimeMillis(),
                message = currentMessage,
                image = currentPhotoURL,
                geoPosition = currentGeoPosition
            )

        if (message.type == MessageType.IMAGE) {
            message.imageBitmap = currentPhoto!!.asImageBitmap()
            currentPhoto = null
        }

        // Publish message to specific topic
        mqttConnector.publishMessage(
            topic = messageTopic + currentChatPartner!!.name,
            message = message,
            onPublished = {
                message.sent = true

                // Find chat of current chatPartner
                val chat =
                    allChats.find { chat -> chat.chatPartner.name == currentChatPartner!!.name }

                // Chat exists: Add message to message list of chat
                if (chat != null) {
                    chat.messages.add(message)
                } else { // Chat doesn't exist:
                    // Create new Contact
                    val receiver = message.receiver.name
                    val profilePicture = message.receiver.profilePicture
                    val newContact = Contact(receiver, profilePicture)

                    // Add new contact to allContacts list
                    allContacts.add(newContact)
                    allContacts.sortBy { contact -> contact.name[0] }

                    // Create new message list
                    val newMessagesList = mutableStateListOf<Message>()
                    newMessagesList.add(message)

                    // Create new chat and add to allChats list
                    val newChat = Chat(newContact, newMessagesList)
                    allChats.add(newChat)
                }
            },
            onError = {
                notificationMessage = "Message could not be sent! Please try again"
            }
        )
        currentMessage = ""
        currentMessageType = MessageType.PLAINTEXT
    }

    // Contact
    private fun onNewContact(it: JSONObject) {
        val allNames = allContacts.stream().map { contact -> contact.name }.toList()
        val newName = it.getString("name")
        val newProfilePic = asProfilePicture(it.getString("profilePicture"))

        // Contact name doesn't already exist and it's not myself
        if (!allNames.contains(newName) && currentUserName != newName) { // sender is new
            // Create new contact and add to allContacts list
            val newContact = Contact(newName, newProfilePic)
            allContacts.add(newContact)
            allContacts.sortBy { contact -> contact.name[0] }

            // Notify others, that allContacts list has changed
            publishContact()

        } else if (allNames.contains(newName)) { // Contact already exists
            val existingContact = allContacts.find { contact -> contact.name == newName }
            existingContact!!.profilePicture = newProfilePic
            existingContact.name = newName
        }
    }

    fun publishContact() {
        // Create new contact
        val contact = Contact(
            name = currentUserName,
            profilePicture = currentProfilePicture
        )

        // Publish contact to specific topic
        mqttConnector.publishContact(
            topic = contactTopic + currentUserName,
            contact = contact,
        )
    }

    // GPS
    fun rememberCurrentPosition() {
        locator.getLocation(
            onNewLocation = {
                currentMessageType = MessageType.GEOPOSITION
                currentMessage = it.dms()
                currentGeoPosition = it
            },
            onFailure = {
                notificationMessage = "An error occurred while accessing geo position!"
            },
            onPermissionDenied = {
                notificationMessage = "Permission denied! Please allow access to the geo position."
            },
        )
    }

    fun showOnMap(position: GeoPosition) =
        AndroidUriHandler(context).openUri(position.asGoogleMapsURL())


    // Photo
    fun takePhoto() {
        cameraAppConnector.getBitmap(onSuccess = {
            currentMessageType = MessageType.IMAGE
            currentPhoto = it
        },
            onCanceled = { notificationMessage = "New photo canceled!" })
    }


    // FileIO
    fun uploadToFileIO() {
        currentPhotoURL = null
        modelScope.launch {
            uploadBitmapToFileIO(bitmap = currentPhoto!!,
                onSuccess = {
                    currentPhotoURL = it
                    publishMessage()
                },
                onError = { _, _ ->
                    notificationMessage = "An error occurred while sending photo!"
                })
        }
    }

    private fun downloadImageForMessage(message: Message) {
        modelScope.launch {
            downloadBitmapFromFileIO(url = message.image!!,
                onSuccess = {
                    message.imageBitmap = it.asImageBitmap()
                    processMessage(message)
                },
                onDeleted = { notificationMessage = "File is no longer available" },
                onError = { notificationMessage = "An error occurred while downloading photo!" })
        }
    }

    // Helper function
    fun goToChat(contact: Contact): Chat {
        val existingChat = allChats.find { chat -> chat.chatPartner == contact }
        return if (existingChat != null) {
            existingChat
        } else {
            val newMessagesList = mutableStateListOf<Message>()
            val newChat = Chat(contact, newMessagesList)
            allChats.add(newChat)
            newChat
        }
    }

    fun toggleTheme() {
        isDarkTheme = !isDarkTheme
    }
}