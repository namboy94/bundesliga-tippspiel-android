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
 * Model class that models a Match
 * @param id: The database ID of the match
 * @param homeTeamId: The ID of the home team
 * @param homeTeam: The home team
 * @param awayTeamId: The ID of the away team
 * @param awayTeam: The away team
 * @param matchday: The match day of the match
 * @param homeCurrentScore: The current score of the home team
 * @param awayCurrentScore: The current score of the away team
 * @param homeHtScore: The score of the home team at half time
 * @param awayHtScore: The score of the away team at half time
 * @param homeFtScore: The score of the home team at full time
 * @param awayFtScore: The score of the away team at full time
 * @param kickoff: The time at which the match starts as a UTC time string
 * @param started: Indicates whether or not the match has already started
 * @param finished: Indicates whether or not the match has finished
 */
data class Match(
    val id: Int,
    val homeTeamId: Int,
    val homeTeam: Team,
    val awayTeamId: Int,
    val awayTeam: Team,
    val matchday: Int,
    val homeCurrentScore: Int,
    val awayCurrentScore: Int,
    val homeHtScore: Int,
    val awayHtScore: Int,
    val homeFtScore: Int,
    val awayFtScore: Int,
    val kickoff: String,
    val started: Boolean,
    val finished: Boolean
) : Model {

    /**
     * Converts the model object into a JSON object
     * @return The generated JSON object
     */
    override fun toJson(): JSONObject {
        return JSONObject("""{
            "id": $id,
            "home_team_id": $homeTeamId,
            "home_team": ${homeTeam.toJson()},
            "away_team_id": $awayTeamId,
            "away_team": ${awayTeam.toJson()},
            "matchday": $matchday,
            "home_current_score": $homeCurrentScore,
            "away_current_score": $awayCurrentScore,
            "home_ht_score": $homeHtScore,
            "away_ht_score": $awayHtScore,
            "home_ft_score": $homeFtScore,
            "away_ft_score": $awayFtScore,
            "kickoff": "$kickoff",
            "started": $started,
            "finished": $finished
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
        override fun fromJson(data: JSONObject): Match {
            return Match(
                    id = data.getInt("id"),
                    homeTeamId = data.getInt("home_team_id"),
                    homeTeam = Team.fromJson(data.getJSONObject("home_team")),
                    awayTeamId = data.getInt("away_team_id"),
                    awayTeam = Team.fromJson(data.getJSONObject("away_team")),
                    matchday = data.getInt("matchday"),
                    homeCurrentScore = data.getInt("home_current_score"),
                    awayCurrentScore = data.getInt("away_current_score"),
                    homeHtScore = data.getInt("home_ht_score"),
                    awayHtScore = data.getInt("away_ht_score"),
                    homeFtScore = data.getInt("home_ft_score"),
                    awayFtScore = data.getInt("away_ft_score"),
                    kickoff = data.getString("kickoff"),
                    started = data.getBoolean("started"),
                    finished = data.getBoolean("finished")
            )
        }
    }
}
