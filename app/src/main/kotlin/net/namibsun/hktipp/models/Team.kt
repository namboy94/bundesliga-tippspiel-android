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
 * Model class that models a Team
 * @param id: The database ID of the team
 * @param name: The full name of the team
 * @param shortName: A shortened name of the team
 * @param abbreviation: A 3-letter abbreviation for the team
 * @param iconSvg: A URL to an SVG image of the team's logo
 * @param iconPng: A URL to a PNG image of the team's logo
 */
data class Team(
    val id: Int,
    val name: String,
    val shortName: String,
    val abbreviation: String,
    val iconSvg: String,
    val iconPng: String
) : Model {

    /**
     * Converts the model object into a JSON object
     * @return The generated JSON object
     */
    override fun toJson(): JSONObject {
        return JSONObject("""{
            "id": $id,
            "name": "$name",
            "short_name": "$shortName",
            "abbreviation": "$abbreviation",
            "icon_svg": "$iconSvg",
            "icon_png": "$iconPng",
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
        override fun fromJson(data: JSONObject): Team {
            return Team(
                    id = data.getInt("id"),
                    name = data.getString("name"),
                    shortName = data.getString("short_name"),
                    abbreviation = data.getString("abbreviation"),
                    iconSvg = data.getString("icon_svg"),
                    iconPng = data.getString("icon_png")
            )
        }
    }
}
