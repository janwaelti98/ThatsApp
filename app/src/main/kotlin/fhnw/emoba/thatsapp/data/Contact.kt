package fhnw.emoba.thatsapp.data

import fhnw.emoba.R
import fhnw.emoba.thatsapp.model.ProfilePicture
import org.json.JSONObject

class Contact(
    var name: String, // corresponds to subscribed topic
    var profilePicture: ProfilePicture
) {
    constructor(json: JSONObject) : this(
        json.getString("name"),
        asProfilePicture(json.getString("profilePicture")),
    );

    fun asJsonString(): String {
        return """
        {
            "name": "$name",
            "profilePicture": "$profilePicture"
        }
         """.trimIndent()
    }
}

fun asProfilePicture(profilePicName: String): ProfilePicture {
    return ProfilePicture.valueOf(profilePicName)
}

fun toImageResource(profilePicture: ProfilePicture): Int {
    return when (profilePicture) {
        ProfilePicture.AGNES -> R.drawable.agnes
        ProfilePicture.BOB -> R.drawable.bob
        ProfilePicture.EDITH -> R.drawable.edith
        ProfilePicture.EDUARDO -> R.drawable.eduardo
        ProfilePicture.GRU -> R.drawable.gru
        ProfilePicture.KEVIN -> R.drawable.kevin
        ProfilePicture.MARGO -> R.drawable.margo
        ProfilePicture.MINION_AMERICA -> R.drawable.minion_america
        ProfilePicture.STUART -> R.drawable.stuart
        else -> {
            R.drawable.bob
        }
    }
}