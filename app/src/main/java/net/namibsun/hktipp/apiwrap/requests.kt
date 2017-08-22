/*
    Copyright 2017 Hermann Krumrey

    This file is part of bundesliga-tippspiel-android.

    bundesliga-tippspiel-android is an Android app that allows a user to
    manage their bets on the bundesliga-tippspiel website.

    bundesliga-tippspiel-android is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    bundesliga-tippspiel-android is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with bundesliga-tippspiel-android. If not, see <http://www.gnu.org/licenses/>.
*/

package net.namibsun.hktipp.apiwrap

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * Module that contains generic functions for creating requests for the bundesliga-tippspiel API
 */

/**
 * Post a JSON body to an API endpoint of bundesliga-tippspiel and returns the result
 * @param endpoint: The endpoint to POST to
 * @param data: The JSON Data to POST
 * @return: The resulting JSON response
 */
fun post(endpoint: String, data: String): JSONObject {

    val jsonMediaType = MediaType.parse("application/json; charset=utf-8")
    val client = OkHttpClient()

    val body = RequestBody.create(jsonMediaType, data)
    val request = Request.Builder()
            .url("https://hk-tippspiel.com/api/v1/$endpoint.php")
            .post(body)
            .build()

    val response = client.newCall(request).execute()
    val responseBody = response.body()!!.string()

    return JSONObject(responseBody)
}

/**
 * Extends the [post] method by adding parameters for a username and API key.
 * @param endpoint: The endpoint to POST to
 * @param data: The JSON Data to POST
 * @param username: The user's username
 * @param apiKey: The user's API key
 * @return: The resulting JSON response
 */
fun post(endpoint: String, data: String, username: String, apiKey: String): JSONObject {

    val json = JSONObject(data)
    json.put("username", username)
    json.put("api_key", apiKey)

    try {

        val result = post(endpoint, json.toString())

        if (result.get("status") == "success" || result.get("status") == "success_with_errors") {
            return result
        } else {
            throw IOException("unauthorized")
        }
    } catch (e: SocketTimeoutException) {
        throw IOException("timeout")
    }

}
