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

package net.namibsun.hktipp.api
import android.util.Base64
import android.util.Log
import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * Class that allows interaction with the hk-tippspiel API
 * @param apiKey: The API key to use for authentication
 * @param expiration: The expiration time of the API key
 */
class ApiConnection(val apiKey: String, val expiration: Int) {

    /**
     * Contains static methods of the class
     */
    companion object {

        /**
         * Allows initializing an ApiConnection object using username and password
         * @param username: The username to use
         * @param password: The password to use
         * @return The generated ApiConnection object or null if the login failed
         */
        fun login(username: String, password: String): ApiConnection? {
            val resp = this.request(
                    HTTP_METHOD.POST,
                    "api_key",
                    mapOf("username" to username, "password" to password)
            )
            return if (resp.getString("status") == "ok") {
                val data = resp.getJSONObject("data")
                ApiConnection(
                        data.getString("api_key"),
                        data.getInt("expiration")
                )
            } else {
                Log.i("Login Failure", "Failed to log in user $username.")
                null
            }
        }

        /**
         * Executes a HTTP request on an API endpoint
         * @param method: The HTTP method to use
         * @param endpoint: The endpoint to use
         * @param params: The parameters to send
         * @param apiKey: Optional API key for authentication
         * @return The response JSON object
         */
        private fun request(
            method: HTTP_METHOD,
            endpoint: String,
            params: Map<String, Any>,
            apiKey: String? = null
        ): JSONObject {

            val client = OkHttpClient()
            var builder = Request.Builder()

            val endpointUrl = this.prepareEndpointUrl(method, endpoint, params)
            val body = this.prepareBody(params)

            builder = builder.url(endpointUrl)
            if (apiKey != null) {
                val encoded = Base64.encodeToString(apiKey.toByteArray(), 0)
                val headers = Headers.of(mutableMapOf("Authorization" to "Basic $encoded"))
                builder = builder.headers(headers)
            }

            builder = when (method) {
                HTTP_METHOD.POST -> builder.post(body)
                HTTP_METHOD.GET -> builder.get()
                HTTP_METHOD.PUT -> builder.put(body)
                HTTP_METHOD.DELETE -> builder.delete(body)
            }

            val request = builder.build()

            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body()!!.string()
                val result = JSONObject(responseBody)
                if (result.getString("status") == "error") {
                    Log.e("Error", result.getString("reason"))
                }
                return result
            } catch (e: SocketTimeoutException) {
                throw IOException("timeout")
            }
        }

        /**
         * Prepares the endpoint URL.
         * If the method is GET, appends the parameters to the URL
         * @param method: The HTTP method to use
         * @param endpoint: The endpoint to use
         * @param params: The parameters to use
         * @return the generated endpoint URL
         */
        private fun prepareEndpointUrl(
                method: HTTP_METHOD,
                endpoint: String,
                params: Map<String, Any>
        ): String {
            var endpointUrl = "https://hk-tippspiel.com/api/v2/$endpoint"
            if (method == HTTP_METHOD.GET && params.isNotEmpty()) {
                endpointUrl += "?"
                for ((key, value) in params) {
                    endpointUrl += "$key=$value&"
                }
                endpointUrl = endpointUrl.substring(0, endpointUrl.length - 1)
            }
            return endpointUrl
        }

        /**
         * Prepares the request body
         * @param params: The body parameters
         * @return The Request Body
         */
        private fun prepareBody(params: Map<String, Any>): RequestBody {

            val jsonMediaType = MediaType.parse("application/json; charset=utf-8")

            val jsonData = JSONObject()
            for ((key, value) in params) {
                jsonData.put(key, value)
            }
            return RequestBody.create(jsonMediaType, jsonData.toString())
        }
    }
}
