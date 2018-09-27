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
    companion object : ModelGenerator {

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
    }
}
