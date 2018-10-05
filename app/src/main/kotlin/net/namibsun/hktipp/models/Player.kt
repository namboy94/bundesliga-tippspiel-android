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
 * Model class that models a Player
 * @param id: The database ID of the player
 * @param name: The name of the player
 * @param teamId: The ID of the player's team
 * @param team: The player's team
 */
data class Player(
    val id: Int,
    val name: String,
    val teamId: Int,
    val team: Team
) : Model {

    /**
     * Converts the model object into a JSON object
     * @return The generated JSON object
     */
    override fun toJson(): JSONObject {
        return JSONObject("""{
            "id": $id,
            "name": "$name",
            "team_id": $teamId,
            "team": ${team.toJson()}
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
        override fun fromJson(data: JSONObject): Player {
            return Player(
                    id = data.getInt("id"),
                    name = data.getString("name"),
                    teamId = data.getInt("team_id"),
                    team = Team.fromJson(data.getJSONObject("team"))
            )
        }

        /**
         * Generates a Query object with which the model may be queried
         * @param apiConnection: The API connection to use with the query
         * @return: The query object
         */
        override fun query(apiConnection: ApiConnection): PlayerQuery {
            return PlayerQuery(apiConnection)
        }
    }
}

/**
 * Extends the Query class to generate Player model objects.
 * @param apiConnection: The API connection to use
 */
class PlayerQuery(
    apiConnection: ApiConnection
) : Query(
        apiConnection,
        "player",
        { json: JSONObject -> Player.fromJson(json) },
        listOf("id", "team_id")
) {

    /**
     * Executes the query, then converts the model objects to the Match model class
     * @return A list of model objects that are the result of the query
     */
    override fun query(): List<Player> {
        val models = super.query()
        val converted = mutableListOf<Player>()
        for (model in models) {
            converted.add(model as Player)
        }
        return converted
    }
}
