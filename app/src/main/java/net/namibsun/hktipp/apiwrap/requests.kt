package net.namibsun.hktipp.apiwrap

import android.util.Log
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.IOException

@Throws(IOException::class)
fun post(endpoint: String, data: JSONObject): JSONObject = post(endpoint, data.toString())

@Throws(IOException::class)
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
