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

import org.junit.Test
import kotlin.test.assertEquals
import org.json.JSONObject
import kotlin.test.assertNotEquals

/**
 * Class that tests the Match model class
 */
class MatchTest {

    /**
     * A sample JSON string for a match
     */
    val sampleJson = """{
        "id": 1,
        "home_team_id": 1,
        "home_team": ${TeamTest().sampleJson},
        "away_team_id": 2,
        "away_team": {
            "id": 2,
            "name": "FC Schalke 04",
            "short_name": "Schalke",
            "abbreviation": "S04",
            "icon_svg": "SVGSO4",
            "icon_png": "PNGSO4"
        },
        "matchday": 34,
        "home_current_score": 1,
        "away_current_score": 2,
        "home_ht_score": 3,
        "away_ht_score": 4,
        "home_ft_score": 5,
        "away_ft_score": 6,
        "kickoff": "2018-01-01:01-02-03",
        "started": true,
        "finished": true
    }""".trimIndent()

    /**
     * Tests generating a model object using the sample JSON string
     */
    @Test
    fun testGenerating() {
        val homeTeam = Team.fromJson(JSONObject(TeamTest().sampleJson))
        val match = Match.fromJson(JSONObject(this.sampleJson))
        assertEquals(match.id, 1)
        assertEquals(match.homeTeamId, 1)
        assertEquals(match.homeTeam, homeTeam)
        assertEquals(match.awayTeamId, 2)
        assertNotEquals(match.awayTeam, homeTeam)
        assertEquals(match.matchday, 34)
        assertEquals(match.homeCurrentScore, 1)
        assertEquals(match.awayCurrentScore, 2)
        assertEquals(match.homeHtScore, 3)
        assertEquals(match.awayHtScore, 4)
        assertEquals(match.homeFtScore, 5)
        assertEquals(match.awayFtScore, 6)
        assertEquals(match.kickoff, "2018-01-01:01-02-03")
        assertEquals(match.started, true)
        assertEquals(match.finished, true)
        assertEquals(match.toJson().toString(), JSONObject(this.sampleJson).toString())
    }
}
