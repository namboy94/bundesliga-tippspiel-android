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