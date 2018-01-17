/*
Copyright 2017-2018 Hermann Krumrey<hermann@krumreyh.com>

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
 * Class that models a player
 * @param data: The JSON data to parse
 */
class PlayerData(data: JSONObject) : Serializable {

    /**
     * The ID of the player
     */
    val id = data.getInt("id")

    /**
     * The name of the player
     */
    val name = data.getString("name")!!

    /**
     * The team this player is associated with
     */
    val team = TeamData(data.getJSONObject("team"))
}