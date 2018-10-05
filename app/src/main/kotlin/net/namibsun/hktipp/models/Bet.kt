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

package net.namibsun.hktipp.models

import net.namibsun.hktipp.api.ApiConnection
import org.json.JSONObject

/**
 * Model class that models a Bet
 * @param id: The database ID of the bet
 * @param userId: The ID of the user that bet this bet
 * @param user: The user that bet this bet
 * @param matchId: The ID of the match this bet was bet for
 * @param match: The match that this bet was bet for
 * @param homeScore: The score bet on the home team
 * @param awayScore: The score bet on the away team
 * @param points: The amount of points scored for this bet
 */
data class Bet(
    val id: Int,
    val userId: Int,
    val user: User,
    val matchId: Int,
    val match: Match,
    val homeScore: Int,
    val awayScore: Int,
    val points: Int
) : Model {

    /**
     * Converts the model object into a JSON object
     * @return The generated JSON object
     */
    override fun toJson(): JSONObject {
        return JSONObject("""{
            "id": $id,
            "user_id": $userId,
            "user": ${user.toJson()},
            "match_id": $matchId,
            "match": ${match.toJson()},
            "home_score": $homeScore,
            "away_score": $awayScore,
            "points": $points
        }""".trimIndent())
    }

    /**
     * Companion object that implements static methods to generate the model using a
     * JSONObject.
     */
    companion object : ModelGenerator, QueryAble {

        /**
         * Places bets using the API
         * @param apiConnection: The API connection to use
         * @param bets: A list of bets to place
         */
        fun place(apiConnection: ApiConnection, bets: List<MinimalBet>) {
            val params = mutableMapOf<String, Int>()
            for (bet in bets) {
                params["${bet.matchId}-home"] = bet.homeScore
                params["${bet.matchId}-away"] = bet.awayScore
            }
            apiConnection.put("bet", params)
        }

        /**
         * Generates the model using a JSONObject
         * @param data: The JSON to parse
         * @return: The generated model
         */
        override fun fromJson(data: JSONObject): Bet {
            return Bet(
                    id = data.getInt("id"),
                    userId = data.getInt("user_id"),
                    user = User.fromJson(data.getJSONObject("user")),
                    matchId = data.getInt("match_id"),
                    match = Match.fromJson(data.getJSONObject("match")),
                    homeScore = data.getInt("home_score"),
                    awayScore = data.getInt("away_score"),
                    points = data.getInt("points")
            )
        }

        /**
         * Generates a Query object with which the model may be queried
         * @param apiConnection: The API connection to use with the query
         * @return: The query object
         */
        override fun query(apiConnection: ApiConnection): BetQuery {
            return BetQuery(apiConnection)
        }
    }
}

/**
 * Class that contains the minimal details for a bet
 * @param matchId: The ID of the match
 * @param homeScore: The score bet on the home team
 * @param awayScore: The score bet on the away team
 */
data class MinimalBet(val matchId: Int, val homeScore: Int, val awayScore: Int)

/**
 * Extends the Query class to generate Bet model objects.
 * @param apiConnection: The API connection to use
 */
class BetQuery(
    apiConnection: ApiConnection
) : Query(
        apiConnection,
        "bet",
        { json: JSONObject -> Bet.fromJson(json) },
        listOf("id", "user_id", "match_id", "matchday")
) {

    /**
     * Executes the query, then converts the model objects to the Bet model class
     * @return A list of model objects that are the result of the query
     */
    override fun query(): List<Bet> {
        val models = super.query()
        val converted = mutableListOf<Bet>()
        for (model in models) {
            converted.add(model as Bet)
        }
        return converted
    }
}
