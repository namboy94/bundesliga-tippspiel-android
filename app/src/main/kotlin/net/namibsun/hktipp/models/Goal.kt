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
 * Model class that models a Goal
 * @param id: The database ID of the goal
 * @param playerId: The ID of the player that scored this goal
 * @param player: The player that scored this goal
 * @param matchId: The ID of the match in which the goal was scored
 * @param match: The match in which the goal was scored
 * @param minute: The minute of the match in which the goal was scored
 * @param minuteEt: The minute of extra time in which the goal was scored
 * @param homeScore: The score of the home team after this goal
 * @param awayScore: The score of the away team after this goal
 * @param ownGoal: Indicates whether or not this goal was an own goal
 * @param penalty: Indicates whether or not this goal was a penalty
 */
data class Goal(
    val id: Int,
    val playerId: Int,
    val player: Player,
    val matchId: Int,
    val match: Match,
    val minute: Int,
    val minuteEt: Int,
    val homeScore: Int,
    val awayScore: Int,
    val ownGoal: Boolean,
    val penalty: Boolean
) : Model {

    /**
     * Converts the model object into a JSON object
     * @return The generated JSON object
     */
    override fun toJson(): JSONObject {
        return JSONObject("""{
            "id": $id,
            "player_id": $playerId,
            "player": ${player.toJson()},
            "match_id": $matchId,
            "match": ${match.toJson()},
            "minute": $minute,
            "minute_et": $minuteEt,
            "home_score": $homeScore,
            "away_score": $awayScore,
            "own_goal": $ownGoal,
            "penalty": $penalty
        }""".trimIndent())
    }

    /**
     * Companion object that implements static methods to generate the model using a
     * JSONObject.
     */
    companion object : ModelGenerator, QueryAble {

        /**
         * Generates the model using a JSONObject
         * @param data: The JSON to parse
         * @return: The generated model
         */
        override fun fromJson(data: JSONObject): Goal {
            return Goal(
                    id = data.getInt("id"),
                    playerId = data.getInt("player_id"),
                    player = Player.fromJson(data.getJSONObject("player")),
                    matchId = data.getInt("match_id"),
                    match = Match.fromJson(data.getJSONObject("match")),
                    minute = data.getInt("minute"),
                    minuteEt = data.getInt("minute_et"),
                    homeScore = data.getInt("home_score"),
                    awayScore = data.getInt("away_score"),
                    ownGoal = data.getBoolean("own_goal"),
                    penalty = data.getBoolean("penalty")
            )
        }

        /**
         * Generates a Query object with which the model may be queried
         * @param apiConnection: The API connection to use with the query
         * @return: The query object
         */
        override fun query(apiConnection: ApiConnection): GoalQuery {
            return GoalQuery(apiConnection)
        }
    }
}

/**
 * Extends the Query class to generate Goal model objects.
 * @param apiConnection: The API connection to use
 */
class GoalQuery(
    apiConnection: ApiConnection
) : Query(
        apiConnection,
        "goal",
        { json: JSONObject -> Goal.fromJson(json) },
        listOf("id", "matchday", "match_id", "player_id", "team_id")
) {

    /**
     * Executes the query, then converts the model objects to the Goal model class
     * @return A list of model objects that are the result of the query
     */
    override fun query(): List<Goal> {
        val models = super.query()
        val converted = mutableListOf<Goal>()
        for (model in models) {
            converted.add(model as Goal)
        }
        return converted
    }
}
