package net.namibsun.hktipp.apiwrap

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.IOException
import java.net.URL

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

@Throws(IOException::class)
fun post(endpoint: String, data: String, username: String, apiKey: String): JSONObject {

    val json = JSONObject(data)
    json.put("username", username)
    json.put("api_key", apiKey)
    Log.e("LOG", json.toString())
    val result = post(endpoint, json.toString())

    if (result.get("status") == "success") {
        return result
    }
    else {
        throw IOException("unauthorized")
    }

}

fun downloadImage(url: String) : Bitmap =
        BitmapFactory.decodeStream(URL(url).openConnection().getInputStream())