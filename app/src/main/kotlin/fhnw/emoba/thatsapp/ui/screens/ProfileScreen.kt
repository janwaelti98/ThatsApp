package fhnw.emoba.thatsapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fhnw.emoba.thatsapp.model.ProfilePicture
import fhnw.emoba.thatsapp.model.ThatsAppModel
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.toSize
import fhnw.emoba.thatsapp.data.asProfilePicture
import fhnw.emoba.thatsapp.ui.Notification
import fhnw.emoba.thatsapp.ui.ThumbnailProfilePic

@Composable
fun ProfileScreen(model: ThatsAppModel) {
    ProfileScreenContent(model)
}

@Composable
fun ProfileScreenContent(model: ThatsAppModel) {
    val scaffoldState = rememberScaffoldState()
    Column() {
        ThumbnailProfilePic(
            model.currentProfilePicture,
            Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 20.dp, end = 20.dp)
        )
        NameField(model)
        DropDownMenu(model)
        Spacer(modifier = Modifier.weight(1.0f))
        SaveButton(model)
    }
    Notification(model, scaffoldState.snackbarHostState)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NameField(model: ThatsAppModel) {
    with(model) {
        val keyboard = LocalSoftwareKeyboardController.current

        OutlinedTextField(
            value = currentUserName,
            onValueChange = {
                currentUserName = it
                changed = true
            },
            label = { Text("Username") },
            placeholder = {
                Text(
                    "Username",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    color = MaterialTheme.colors.onBackground
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            trailingIcon = {
                if (!saved) {
                    IconButton(onClick = {
                        currentUserName = ""
                    }) {
                        Icon(Icons.Filled.Clear, "Clear username")
                    }
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = androidx.compose.ui.text.input.ImeAction.Done,
                autoCorrect = false,
                keyboardType = KeyboardType.Ascii
            ),
            keyboardActions = KeyboardActions(onDone = {
                keyboard?.hide()
            }),
            shape = RoundedCornerShape(15.dp),
            singleLine = true,
            readOnly = saved
        )
    }
}

@Composable
fun DropDownMenu(model: ThatsAppModel) {
    with(model) {
        var menuExpanded by remember { mutableStateOf(false) }
        val profilePicOptions = ProfilePicture.values()
        var menuTextFieldSize by remember { mutableStateOf(Size.Zero) }

        if (menuExpanded)
            Icons.Filled.KeyboardArrowUp
        else
            Icons.Filled.KeyboardArrowDown

        if (saved) {
            menuExpanded = false
        }

        Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp)) {
            OutlinedTextField(
                value = currentProfilePicture.name,
                onValueChange = {
                    currentProfilePicture = asProfilePicture(it)
                    changed = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        // This value is used to assign to
                        // the DropDown the same width
                        menuTextFieldSize = coordinates.size.toSize()
                    },
                label = {
                    Text("Profile picture")
                },
                leadingIcon = {
                    ThumbnailProfilePic(currentProfilePicture, Modifier.size(30.dp, 30.dp))
                    Spacer(modifier = Modifier.padding(end = 15.dp))
                },
                trailingIcon = {
                    if (!saved) {
                        IconButton(onClick = {
                            menuExpanded = !menuExpanded
                        }) {
                            if (menuExpanded)
                                Icon(
                                    Icons.Filled.KeyboardArrowUp,
                                    "Key arrow up",
                                    Modifier.scale(1.3F, 1F)
                                )
                            else
                                Icon(
                                    Icons.Filled.KeyboardArrowDown,
                                    "Key arrow down",
                                    Modifier.scale(1.3F, 1F)
                                )
                        }
                    }
                },
                shape = RoundedCornerShape(15.dp),
                singleLine = true,
                readOnly = true
            )

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
                modifier = Modifier
                    .width(with(LocalDensity.current) { menuTextFieldSize.width.toDp() })
            ) {
                profilePicOptions.forEach { profilePic ->
                    DropdownMenuItem(onClick = {
                        currentProfilePicture = profilePic
                        menuExpanded = false
                        changed = true
                    }) {
                        Row() {
                            ThumbnailProfilePic(profilePic, Modifier.size(30.dp, 30.dp))
                            Spacer(modifier = Modifier.padding(end = 15.dp))
                            Text(text = profilePic.name)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SaveButton(model: ThatsAppModel) {
    with(model) {
        Button(
            onClick = {
                saved = true
                changed = false
                subscribe()
                publishContact()
            },
            enabled = changed && currentUserName != "",
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(15.dp)
        ) {
            Icon(
                Icons.Filled.Save,
                contentDescription = "Save",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Save", style = MaterialTheme.typography.h6)
        }
    }
}