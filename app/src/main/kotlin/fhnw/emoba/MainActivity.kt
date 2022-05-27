package fhnw.emoba

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import fhnw.emoba.thatsapp.ThatsApp


class MainActivity : ComponentActivity() {
    private lateinit var app: EmobaApp  //alle Beispiele implementieren das Interface EmobaApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        app = ThatsApp

        app.initialize(activity = this)

        setContent {
            app.CreateUI()
        }
    }

    /**
     * Eine der Activity-LiveCycle-Methoden. Im Laufe des Semesters werden weitere benötigt
     * werden. Auch die leiten den Aufruf lediglich an die EmobaApp weiter.
     */
    override fun onStop() {
        super.onStop()
        app.onStop(activity = this)
    }
}

