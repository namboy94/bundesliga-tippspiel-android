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

import org.json.JSONArray
import org.json.JSONObject

/**
 * Module that contains wrappers around bundesliga-tippspiel APIs
 */

/**
 * Fetches all the match data for the specified match day
 * @param username: The requesting user's username
 * @param apiKey: The API key of the user
 * @param matchday: The match day for which to fetch the match data.
 *                  Defaults to -1, which will fetch the current match day
 * @return: The match data for the matchday
 */
fun getMatches(username: String, apiKey: String, matchday: Int = -1): JSONArray {
    val json = if (matchday == -1) "{}" else "{\"matchday\":\"$matchday\"}"
    val res = post("get_matches_for_matchday", json, username, apiKey)
    return res.getJSONArray("data")
}

/**
 * Retrieves a user's bet data for a specified match day
 * @param username: The user's username
 * @param apiKey: The user's api key
 * @param matchday: The match day for which to fetch the bet data.
 *                  Defaults to -1 which would fetch the bet data for the current match day
 * @return: The Bet Data
 */
fun getBets(username: String, apiKey: String, matchday: Int = -1): JSONArray {
    val json = if (matchday == -1) "{}" else "{\"matchday\":\"$matchday\"}"
    return post("get_user_bets_for_matchday", json, username, apiKey).getJSONArray("data")
}

/**
 * Places bets for a user
 * @param username: The user's username
 * @param apiKey: The user's API key
 * @param bets: The bets to place. Must be a JsonArray of JsonObjects with the attributes
 *              `home_score`, `away_score` and `match_id`
 * @return: true if the bets where places successfully, false otherwise
 */
fun placeBets(username: String, apiKey: String, bets: JSONArray): Boolean {
    val betsJson = JSONObject()
    betsJson.put("bets", bets)
    val response = post("place_bets", betsJson.toString(), username, apiKey)
    val status = response.getString("status")
    return status.startsWith("success")
}