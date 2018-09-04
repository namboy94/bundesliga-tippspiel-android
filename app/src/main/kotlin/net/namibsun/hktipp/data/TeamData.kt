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

package net.namibsun.hktipp.data

import org.json.JSONObject
import java.io.Serializable

/**
 * Data class that models a team's data
 * @param data: The JSON data to parse
 */
class TeamData(data: JSONObject) : Serializable {

    /**
     * The ID of the team in the database
     */
    val id = data.getInt("id")

    /**
     * The name of the team
     */
    val name = data.getString("name")!!

    /**
     * The shortform version of the team's name
     */
    val shortName = data.getString("short_name")!!

    /**
     * The team's 3-letter abbreviation
     */
    val abbreviation = data.getString("abbreviation")!!

    /**
     * The path to the team's icon
     */
    val iconPath = data.getString("icon_png")!!
}