/*
Copyright 2017 Hermann Krumrey <hermann@krumreyh.com>

This file is part of bundesliga-tippspiel-android.

bundesliga-tippspiel-android is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

bundesliga-tippspiel-android is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with bundesliga-tippspiel-android.  If not, see <http://www.gnu.org/licenses/>.
*/

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
 * @param extras: Additional, optional bundle that will be passed to the next activity
 */
fun switchActivity(
    context: Context,
    target: Class<*>,
    username: String? = null,
    apiKey: String? = null,
    extras: Bundle? = null
) {

    val intent = Intent(context, target)

    val bundle = if (extras == null) {
        Bundle()
    } else {
        extras
    }

    // Set username and api key in bundle if provided
    if (username != null) {
        bundle.putString("username", username)
    }
    if (apiKey != null) {
        bundle.putString("api_key", apiKey)
    }
    intent.putExtras(bundle)
    context.startActivity(intent)
}

/**
 * Logs the user out of the current activity and returns to the LoginActivity
 * @param context: The context from which the user wants to log out
 * @param deleteCredentials: Can be set to true to delete the stored credentials
 */
fun logout(context: Context, deleteCredentials: Boolean = false) {
    if (deleteCredentials) {
        getDefaultSharedPreferences(context).edit().clear().apply()
    }
    context.startActivity(Intent(context, LoginActivity::class.java))
}