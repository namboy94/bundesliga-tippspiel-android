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
 * Data class that models a goal
 * @param data: The JSON data to parse
 */
class GoalData(data: JSONObject) : Serializable {

    /**
     * The ID of the goal
     */
    val id = data.getInt("id")

    /**
     * The player that scored the goal
     */
    val player = PlayerData(data.getJSONObject("player"))

    /**
     * The match in which the goal was scored
     */
    val match = MatchData(data.getJSONObject("match"))

    /**
     * The minute in which the goal was scored
     */
    val minute = data.getInt("minute")

    /**
     * The minute of extra time in which the goal was scored
     */
    val minuteEt = data.getInt("minute_et")

    /**
     * Indicates if the goal was an own goal or not
     */
    val ownGoal = data.getBoolean("own_goal")

    /**
     * Indicates if the goal was scored via penalty shot or not
     */
    val penalty = data.getBoolean("penalty")

    /**
     * The score of the home team after this goal
     */
    val homeScore = data.getInt("home_score")

    /**
     * The score of the away team after this goal
     */
    val awayScore = data.getInt("away_score")
}