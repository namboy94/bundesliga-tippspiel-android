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
 * Data class that models a bet
 * @param data: The JSON to parse
 */
class BetData(data: JSONObject) : Serializable {

    /**
     * The ID of the bet
     */
    val id = data.getInt("id")

    /**
     * The score bet on the home team
     */
    val homeScore = data.getInt("home_score")

    /**
     * The score bet on the away team
     */
    val awayScore = data.getInt("away_score")

    /**
     * The bet's match data
     */
    val match = MatchData(data.getJSONObject("match"))

    /**
     * The points scored with this bet.
     */
    val points = data.getInt("points")
}