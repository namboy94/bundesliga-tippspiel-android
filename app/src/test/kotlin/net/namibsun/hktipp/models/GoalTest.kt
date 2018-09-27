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

/**
 * Class that tests the Goal model class
 */
class GoalTest {

    /**
     * A sample JSON string for a goal
     */
    private val sampleJson = """{
        "id": 1,
        "player_id": 1,
        "player": ${PlayerTest().sampleJson},
        "match_id": 1,
        "match": ${MatchTest().sampleJson},
        "minute": 67,
        "minute_et": 0,
        "home_score": 1,
        "away_score": 0,
        "own_goal": false,
        "penalty": true
    }""".trimIndent()

    /**
     * Tests generating a model object using the sample JSON string
     */
    @Test
    fun testGenerating() {
        val match = Match.fromJson(JSONObject(MatchTest().sampleJson))
        val player = Player.fromJson(JSONObject(PlayerTest().sampleJson))
        val goal = Goal.fromJson(JSONObject(this.sampleJson))
        assertEquals(goal.id, 1)
        assertEquals(goal.playerId, 1)
        assertEquals(goal.player, player)
        assertEquals(goal.matchId, 1)
        assertEquals(goal.match, match)
        assertEquals(goal.minute, 67)
        assertEquals(goal.minuteEt, 0)
        assertEquals(goal.homeScore, 1)
        assertEquals(goal.awayScore, 0)
        assertEquals(goal.ownGoal, false)
        assertEquals(goal.penalty, true)
        assertEquals(goal.toJson().toString(), JSONObject(this.sampleJson).toString())
    }
}
