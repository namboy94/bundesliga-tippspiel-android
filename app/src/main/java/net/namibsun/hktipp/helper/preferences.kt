package net.namibsun.hktipp.helper

import android.content.Context
import android.content.SharedPreferences

/**
 * Fetches the default shared preferences for the application
 * @param context: The context for which to fetch the shared preferences
 * @return: The shared preferences
 */
fun getDefaultSharedPreferences(context: Context) : SharedPreferences =
        context.getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE)

/**
 * Fetches the username from the shared preferences
 * @param context: The context for which to fetch the username
 * @return: The username stored in the shared preferences, or null if no username was store
 */
fun getUsernameFromPreferences(context: Context) : String? =
        getDefaultSharedPreferences(context).getString("username", null)

/**
 * Fetches the api key from the shared preferences
 * @param context: The context for which to fetch the API key
 * @return: The API Key store in the shared preferences, or null if none was stored
 */
fun getApiKeyFromSharedPreferences(context: Context) : String? =
        getDefaultSharedPreferences(context).getString("api_key", null)

/**
 * Stores the username in the shared preferences
 * @param context: The context for which to store the username
 * @param username: The username to store
 */
fun storeUsernameInSharedPreferences(context: Context, username: String) {
    val editor = getDefaultSharedPreferences(context).edit()
    editor.putString("username", username)
    editor.apply()
}

/**
 * Stores the API key in the shared preferences
 * @param context: The context for which to store the API key
 * @param apiKey: The API key to store
 */
fun storeApiKeyInSharedPreferences(context: Context, apiKey: String) {
    val editor = getDefaultSharedPreferences(context).edit()
    editor.putString("api_key", apiKey)
    editor.apply()

}