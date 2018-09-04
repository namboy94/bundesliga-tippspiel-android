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

import net.namibsun.hktipp.data.BetData
import net.namibsun.hktipp.data.GoalData
import net.namibsun.hktipp.data.MatchData
import org.json.JSONArray

/**
 * Module that contains wrappers around bundesliga-tippspiel APIs
 */

/**
 * Fetches all the match data for the specified match day
 * @param apiKey: The API key of the user
 * @param matchDay: The match day for which to fetch the match data.
 *                  Defaults to -1, which will fetch the current match day
 * @return: The matches for the specified/current matchday
 */
fun getMatches(apiKey: String, matchDay: Int = -1): List<MatchData> {
    val json = mutableMapOf<String, Any>("matchday" to matchDay)
    val result = request(
            "match",
            HTTPMETHOD.GET,
            json,
            apiKey
    )
    val matchData = result.getJSONObject("data").getJSONArray("matches")

    return (0..(matchData.length() - 1)).map {
        MatchData(matchData.getJSONObject(it))
    }
}

/**
 * Retrieves a user's bet data for a specified match day
 * @param apiKey: The user's api key
 * @param matchDay: The match day for which to fetch the bet data.
 *                  Defaults to -1 which would fetch the bet data for the current match day
 * @return: The Bet Data
 */
fun getBets(apiKey: String, matchDay: Int = -1): List<BetData> {
    val json = mutableMapOf<String, Any>("matchday" to matchDay)
    val result = request(
            "bet",
            HTTPMETHOD.GET,
            json,
            apiKey
    ).getJSONObject("data").getJSONArray("bets")

    return (0..(result.length() - 1)).map {
        BetData(result.getJSONObject(it))
    }
}

/**
 * Places bets for a user
 * @param apiKey: The user's API key
 * @param bets: The bets to place. Must be a JsonArray of JsonObjects with the attributes
 *              `home_score`, `away_score` and `match_id`
 * @return: true if the bets where places successfully, false otherwise
 */
fun placeBets(apiKey: String, bets: JSONArray): Boolean {
    val betsJson = mutableMapOf<String, Any>()
    for (i in 0..(bets.length() - 1)) {
        val bet = bets.getJSONObject(i)
        val matchId = bet.getInt("match_id")
        val homeScore = bet.getInt("home_score")
        val awayScore = bet.getInt("away_score")
        betsJson["$matchId-home"] = homeScore
        betsJson["$matchId-away"] = awayScore
    }
    val response = request("bet", HTTPMETHOD.PUT, betsJson, apiKey)
    return response.getString("status") == "ok"
}

/**
 * Retrieves goal data from the API
 * @param apiKey: The API key used for authentication
 * @param matchId: The ID of the match for which to retrieve the goals
 * @return A List of GoalData objects for the specified match
 */
fun getGoalsForMatch(apiKey: String, matchId: Int): List<GoalData> {
    val goalsJson = mutableMapOf<String, Any>()
    goalsJson["match_id"] = matchId
    val response = request("goal", HTTPMETHOD.GET, goalsJson, apiKey)
    val goals = response.getJSONObject("data").getJSONArray("goals")
    return (0..(goals.length() - 1)).map {
        GoalData(goals.getJSONObject(it))
    }
}

/**
 * Retrieves bets for a match
 * @param apiKey: The API Key used for authentication
 * @param matchId: The ID of the match for which to retrieve the bets
 * @return A list of bets
 */
fun getBetsForMatch(apiKey: String, matchId: Int): Map<String, BetData?> {
    val betsJson = mutableMapOf<String, Any>()
    betsJson["match_id"] = matchId
    val response = request("bet", HTTPMETHOD.GET, betsJson, apiKey)
    val bets = response.getJSONObject("data").getJSONArray("bets")

    val betList = mutableMapOf<String, BetData?>()
    for (i in 0..(bets.length() - 1)) {
        val bet = bets.getJSONObject(i)
        val user = bet.getJSONObject("user").getString("username")
        betList[user] = BetData(bet)
    }
    return betList
}
