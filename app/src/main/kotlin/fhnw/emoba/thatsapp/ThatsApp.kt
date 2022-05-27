package fhnw.emoba.thatsapp

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import fhnw.emoba.EmobaApp
import fhnw.emoba.thatsapp.data.fileIO.CameraAppConnector
import fhnw.emoba.thatsapp.data.gps.GPSConnector
import fhnw.emoba.thatsapp.model.ThatsAppModel
import fhnw.emoba.thatsapp.ui.AppUI


object ThatsApp : EmobaApp {
    private lateinit var model: ThatsAppModel

    override fun initialize(activity: ComponentActivity) {
        val gps = GPSConnector(activity)
        val cameraAppConnector = CameraAppConnector(activity)

        model = ThatsAppModel(activity, gps, cameraAppConnector)
        model.connect()
    }

    @Composable
    override fun CreateUI() {
        AppUI(model)
    }
}

