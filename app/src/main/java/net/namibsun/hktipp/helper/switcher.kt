package net.namibsun.hktipp.helper

import android.content.Context
import android.content.Intent
import android.os.Bundle
import net.namibsun.hktipp.LoginActivity

/**
 * Switches to another activity, while providing the option to provide the new activity with a
 * username and API key
 * @param context: The activity to switch from
 * @param target: The activity to switch to's class
 * @param username: The (optional) username to provide to the new activity
 * @param apiKey: The (optional) API key to provide to the new activity
 */
fun switchActivity(context: Context, target: Class<*>,
                   username: String? = null, apiKey: String? = null) {

    val intent = Intent(context, target)

    // Set username and api key in bundle if provided
    if (username != null && apiKey != null) {
        val bundle = Bundle()
        bundle.putString("username", username)
        bundle.putString("api_key", apiKey)
        intent.putExtras(bundle)
    }
    context.startActivity(intent)
}

/**
 * Logs the user out of the current activity and returns to the LoginActivity
 * @param context: The context from which the user wants to log out
 * @param deleteCredentials: Can be set to true to delete the stored credentials
 */
fun logout(context: Context, deleteCredentials: Boolean = false) {
    if (deleteCredentials) {
        val editor = getDefaultSharedPreferences(context).edit().clear().apply()
    }

    context.startActivity(Intent(context, LoginActivity::class.java))
}