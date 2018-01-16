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
 * Data class that models a match
 * @param data: The JSON data to parse
 */
data class MatchData(val data: JSONObject) : Serializable {

    /**
     * The ID of the match
     */
    val id = this.data.getInt("id")

    /**
     * The home team's data
     */
    val homeTeam = TeamData(this.data.getJSONObject("home_team")!!)

    /**
     * The away team's data
     */
    val awayTeam = TeamData(this.data.getJSONObject("away_team")!!)

    /**
     * The home team's half-time score
     */
    val homeHtScore = this.data.getInt("home_ht_score")

    /**
     * The away team's half-time score
     */
    val awayHtScore = this.data.getInt("away_ht_score")

    /**
     * The home team's full-time score
     */
    val homeFtScore = this.data.getInt("home_ft_score")

    /**
     * The away team's full-time score
     */
    val awayFtScore = this.data.getInt("away_ft_score")

    /**
     * The match day of the match
     */
    val matchDay = this.data.getInt("matchday")

    /**
     * The kickoff time of the match
     */
    val kickoff = this.data.getString("kickoff")

    /**
     * Indicates if the match has started already
     */
    val started = this.data.getBoolean("started")

    /**
     * Indicates if the match is finished or not
     */
    val finished = this.data.getBoolean("finished")
}