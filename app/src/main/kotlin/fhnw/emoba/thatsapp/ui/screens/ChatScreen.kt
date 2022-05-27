package fhnw.emoba.thatsapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import fhnw.emoba.freezerapp.ui.theme.ThatsAppTheme
import fhnw.emoba.freezerapp.ui.theme.transparent
import fhnw.emoba.thatsapp.data.Message
import fhnw.emoba.thatsapp.data.gps.GeoPosition
import fhnw.emoba.thatsapp.data.millisToDate
import fhnw.emoba.thatsapp.model.MessageType
import fhnw.emoba.thatsapp.model.ThatsAppModel
import fhnw.emoba.thatsapp.ui.ChatTopBar
import fhnw.emoba.thatsapp.ui.Notification

@Composable
fun ChatScreen(model: ThatsAppModel) {
    val scaffoldState = rememberScaffoldState()

    ThatsAppTheme(model.isDarkTheme) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { ChatTopBar(model) },
            drawerContent = { ProfileScreen(model) },
            content = { ChatScreenContent(model) }
        )
        Notification(model, scaffoldState.snackbarHostState)
    }
}

@Composable
fun ChatScreenContent(model: ThatsAppModel) {
    with(model) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (allMessagesPanel, inputField, sendButton, photoThumbnail, deletePhoto) = createRefs()

            AllMessagesPanel(
                currentChat!!.messages, model,
                Modifier.constrainAs(allMessagesPanel) {
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                    start.linkTo(parent.start, 0.dp)
                    end.linkTo(parent.end, 0.dp)
                    top.linkTo(parent.top, 0.dp)
                    bottom.linkTo(inputField.top, 10.dp)
                })

            InputField(model,
                Modifier
                    .heightIn(50.dp, 150.dp)
                    .constrainAs(inputField) {
                        width = Dimension.fillToConstraints
                        start.linkTo(parent.start, 10.dp)
                        end.linkTo(parent.end, 70.dp)
                        bottom.linkTo(parent.bottom, 10.dp)
                    })

            SendButton(model,
                Modifier.constrainAs(sendButton) {
                    start.linkTo(inputField.end, 10.dp)
                    end.linkTo(parent.end, 10.dp)
                    bottom.linkTo(parent.bottom, 12.dp)
                })

            if (currentPhoto != null) {
                PhotoThumbnail(
                    model, Modifier.constrainAs(photoThumbnail) {
                        start.linkTo(parent.start, 10.dp)
                        bottom.linkTo(inputField.top, 10.dp)
                    })

                DeletePhotoIcon(model, Modifier.constrainAs(deletePhoto) {
                    top.linkTo(photoThumbnail.top, 5.dp)
                    end.linkTo(photoThumbnail.end, 5.dp)
                })
            }
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InputField(model: ThatsAppModel, modifier: Modifier) {
    with(model) {
        val keyboard = LocalSoftwareKeyboardController.current
        OutlinedTextField(
            value = currentMessage!!,
            onValueChange = { currentMessage = it },
            placeholder = { Text("Message") },
            modifier = modifier,
            trailingIcon = {
                TrailingIcons(model)
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { keyboard?.hide() }),
            shape = RoundedCornerShape(15.dp)
        )
    }
}

@Composable
fun TrailingIcons(model: ThatsAppModel) {
    with(model) {
        Row() {
            IconButton(onClick = {
                rememberCurrentPosition()
            }) {
                Icon(Icons.Filled.GpsFixed, "GPS Icon")
            }
            IconButton(onClick = { takePhoto() }) {
                Icon(Icons.Filled.PhotoCamera, "Camera Icon")
            }
        }
    }
}

@Composable
fun SendButton(model: ThatsAppModel, modifier: Modifier) {
    with(model) {
        val enabled = currentMessage != "" || currentPhoto != null
        IconButton(
            onClick = {
                when (currentMessageType) {
                    MessageType.PLAINTEXT -> {
                        publishMessage()
                    }
                    MessageType.GEOPOSITION -> {
                        publishMessage()
                        currentGeoPosition = GeoPosition(0.0, 0.0, 0.0)
                    }
                    MessageType.IMAGE -> {
                        uploadToFileIO()
                    }
                }
            },
            modifier = modifier,
            enabled = enabled,
            content = {
                Icon(
                    Icons.Filled.Send,
                    "Send Button",
                    tint = if (enabled) {
                        MaterialTheme.colors.primary
                    } else {
                        MaterialTheme.colors.onBackground
                    }
                )
            },
        )
    }
}

@Composable
fun PhotoThumbnail(model: ThatsAppModel, modifier: Modifier) {
    with(model) {
        Image(
            bitmap = currentPhoto!!.asImageBitmap(),
            contentDescription = "Thumbnail of current photo",
            modifier = modifier
                .height(150.dp)
                .clip(RoundedCornerShape(15.dp))
        )
    }
}

@Composable
fun DeletePhotoIcon(model: ThatsAppModel, modifier: Modifier) {
    with(model) {
        IconButton(
            onClick = {
                currentPhoto = null
                currentMessageType = MessageType.PLAINTEXT
            },
            modifier = modifier
                .clip(RoundedCornerShape(100))
                .background(MaterialTheme.colors.error)
                .size(20.dp)
        ) {
            Icon(
                Icons.Filled.Clear,
                "Delete current photo",
                tint = MaterialTheme.colors.onPrimary,
                modifier = Modifier.size(15.dp)
            )
        }
    }
}

@Composable
fun ShowPhoto(message: Message) {
    Image(
        bitmap = message.imageBitmap,
        contentDescription = "Photo of message",
        modifier = Modifier
            .fillMaxHeight()
            .padding(10.dp)
            .clip(RoundedCornerShape(10.dp))
    )
}

@Composable
fun AllMessagesPanel(messages: List<Message>, model: ThatsAppModel, modifier: Modifier) {
    val scrollState = rememberLazyListState()
    Box(
        modifier.border(
            width = 1.dp,
            brush = SolidColor(transparent),
            shape = RectangleShape
        )
    ) {
        LazyColumn(state = scrollState, modifier = modifier) {
            items(messages) {
                MessageCard(it, model)
            }
        }

        LaunchedEffect(messages.size) {
            scrollState.animateScrollToItem(messages.size)
        }
    }
}

// Layout inspired by https://getstream.io/blog/android-jetpack-compose-chat-example/
@Composable
fun MessageCard(message: Message, model: ThatsAppModel) {
    with(message) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 5.dp),
            horizontalAlignment = when (message.sender.name) {
                model.currentUserName -> Alignment.End
                else -> Alignment.Start
            },
        ) {
            Card(
                modifier = Modifier
                    .widthIn(max = 340.dp)
                    .clickable(onClick = {
                        if (type == MessageType.GEOPOSITION) {
                            model.showOnMap(geoPosition!!)
                        }
                    }),
                shape = cardShapeFor(message, model),
                backgroundColor = when (message.sender.name) {
                    model.currentUserName -> MaterialTheme.colors.primary
                    else -> MaterialTheme.colors.secondary
                },
            ) {
                when (type) {
                    MessageType.IMAGE -> ShowPhoto(message)
                    MessageType.GEOPOSITION -> {
                        Text(
                            modifier = Modifier.padding(10.dp),
                            text = message.geoPosition!!.dms(),
                            color = when (message.sender.name) {
                                model.currentUserName -> MaterialTheme.colors.onPrimary
                                else -> MaterialTheme.colors.onSecondary
                            },
                        )
                    }
                    MessageType.PLAINTEXT -> Text(
                        modifier = Modifier.padding(10.dp),
                        text = message.message!!,
                        color = when (message.sender.name) {
                            model.currentUserName -> MaterialTheme.colors.onPrimary
                            else -> MaterialTheme.colors.onSecondary
                        },
                    )
                }
            }
            MessageStatus(message)
        }
    }
}

@Composable
fun cardShapeFor(message: Message, model: ThatsAppModel): RoundedCornerShape {
    val roundedCorners = RoundedCornerShape(16.dp)
    return when (message.sender.name) {
        model.currentUserName -> {
            roundedCorners.copy(bottomEnd = CornerSize(0))
        }
        else -> {
            roundedCorners.copy(bottomStart = CornerSize(0))
        }
    }
}

@Composable
fun MessageStatus(message: Message) {
    Row(verticalAlignment = Alignment.Bottom) {
        Text(
            modifier = Modifier.padding(top = 5.dp),
            text = millisToDate(message.time, "hh:mm"),
            fontSize = 12.sp
        )

        if (message.sent) {
            Spacer(modifier = Modifier.width(5.dp))
            Icon(
                Icons.Filled.Done,
                "Send Button",
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(15.dp)
            )
        }
    }
}