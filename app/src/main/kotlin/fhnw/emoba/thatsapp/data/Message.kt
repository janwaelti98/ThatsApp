package fhnw.emoba.thatsapp.data

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageBitmap
import fhnw.emoba.thatsapp.data.gps.GeoPosition
import fhnw.emoba.thatsapp.model.MessageType
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

data class Message(
    val type: MessageType,
    val sender: Contact,
    val receiver: Contact,
    val time: Long,
    val message: String?,
    val image: String?, // link to access image
    val geoPosition: GeoPosition?,
) {
    var imageBitmap by mutableStateOf(
        Bitmap.createBitmap(
            200,
            200,
            Bitmap.Config.ALPHA_8
        ).asImageBitmap() // creates default bitmap image
    )

    var sent by mutableStateOf(false) // message was sent successfully (published to topic)

    constructor(json: JSONObject) : this(
        MessageType.valueOf(json.getString("type")),
        Contact(
            json.getJSONObject("sender").getString("name"),
            asProfilePicture(json.getJSONObject("sender").getString("profilePicture"))
        ),
        Contact(
            json.getJSONObject("receiver").getString("name"),
            asProfilePicture(json.getJSONObject("receiver").getString("profilePicture"))
        ),
        json.getLong("time"),
        json.getString("message"),
        json.getString("image"),
        GeoPosition(
            json.getJSONObject("location").getDouble("longitude"),
            json.getJSONObject("location").getDouble("latitude"),
            json.getJSONObject("location").getDouble("altitude")
        )
    );

    fun asJsonString(): String {
        return """
       {
                "type": "$type",
                "sender": {
                    "name": "${sender.name}",
                    "profilePicture": "${sender.profilePicture}"
                },
                "receiver": {
                    "name": "${receiver.name}",
                    "profilePicture": "${receiver.profilePicture}"
                },
                "time": "$time",
                "message": "$message",
                "image": "$image",
                "location": {
                    "longitude": "${geoPosition?.longitude}",
                    "latitude": "${geoPosition?.latitude}",
                    "altitude": "${geoPosition?.altitude}"
                }
            }
         """.trimIndent()
    }
}

@SuppressLint("SimpleDateFormat")
fun millisToDate(currentMilliSeconds: Long, dateFormat: String): String {
    val date = Date(currentMilliSeconds)
    val simpleDateFormat = SimpleDateFormat(dateFormat)
    return simpleDateFormat.format(date)
}