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

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Headers
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * Class that defines different HTTP methods
 */
enum class HTTPMETHOD {
    POST, GET, PUT, DELETE
}

/**
 * Module that contains generic functions for creating requests for the bundesliga-tippspiel API
 */

/**
 * Sends a HTTP request to the hk-tippspiel site
 * @param endpoint: The endpoint to send the request to
 * @param method: The HTTP method to use
 * @param data: The data to send to the API
 * @param apiKey: An API key used for authentication
 * @return: The resulting JSON response
 */
fun request(
    endpoint: String,
    method: HTTPMETHOD,
    data: MutableMap<String, Any>,
    apiKey: String? = null
): JSONObject {

    val client = OkHttpClient()
    var builder = Request.Builder()
    var body: RequestBody? = null

    var endpointPath = endpoint
    if (method == HTTPMETHOD.GET && data.isNotEmpty()) {
        endpointPath += "?"
        for ((key, value) in data) {
            endpointPath += "$key=$value&"
        }
        endpointPath = endpointPath.substring(0, endpointPath.length - 1)
    } else {
        val jsonMediaType = MediaType.parse("application/json; charset=utf-8")
        val jsonData = JSONObject()
        for ((key, value) in data) {
            jsonData.put(key, value)
        }
        body = RequestBody.create(jsonMediaType, jsonData.toString())
    }

    builder = builder.url("https://hk-tippspiel.com/api/v2/$endpointPath")
    if (apiKey != null) {
        val headers = Headers.of(mutableMapOf("Authorization" to "Basic $apiKey"))
        builder = builder.headers(headers)
    }

    builder = when (method) {
        HTTPMETHOD.POST -> builder.post(body!!)
        HTTPMETHOD.GET -> builder.get()
        HTTPMETHOD.PUT -> builder.put(body!!)
        HTTPMETHOD.DELETE -> builder.delete(body!!)
    }

    val request = builder.build()

    try {
        val response = client.newCall(request).execute()
        val responseBody = response.body()!!.string()
        return JSONObject(responseBody)

        /*
        val result = JSONObject(responseBody)
        if (result.get("status") == "ok") {
            return result
        } else {
            val unauthorized = mutableListOf(
                    "User does not exist",
                    "User is not confirmed",
                    "Invalid Password"
            )
            for (reason in unauthorized) {
                if (reason.toLowerCase() == result.getString("reason").toLowerCase()) {
                    throw IOException("unauthorized")
                }
            }
            return result
            //throw IOException(result.get("reason").toString().toLowerCase())
        }
        */
    } catch (e: SocketTimeoutException) {
        throw IOException("timeout")
    }
}
