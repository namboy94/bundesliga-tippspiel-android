package net.namibsun.hktipp.apiwrap

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject

fun getMatches(username: String, apiKey: String, matchday: Int = -1): JSONArray {
    val json = if (matchday == -1) "{}" else "{\"matchday\":\"$matchday\"}"
    val res = post("get_matches_for_matchday", json, username, apiKey)
    Log.e("LOG", res.toString())
    return res.getJSONArray("data")
}

fun getBets(username: String, apiKey: String, matchday: Int = -1): JSONArray {
    val json = if (matchday == -1) "{}" else "{\"matchday\":\"$matchday\"}"
    return post("get_user_bets_for_matchday", json, username, apiKey).getJSONArray("data")
}

fun placeBets(username: String, apiKey: String, bets: JSONArray): JSONObject {
    val betsJson = JSONObject()
    betsJson.put("bets", bets)
    return post("place_bets", betsJson.toString(), username, apiKey)
}