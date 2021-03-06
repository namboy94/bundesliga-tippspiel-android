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

import junit.framework.TestCase
import net.namibsun.hktipp.api.ApiConnection
import org.json.JSONObject

/**
 * Class that tests the Goal model class
 */
class GoalTest : TestCase() {

    /**
     * The API connection to use
     */
    private lateinit var apiConnection: ApiConnection

    /**
     * Sets up the API connection
     */
    override fun setUp() {
        super.setUp()
        this.apiConnection = ApiConnection.login(
                System.getenv("API_USER"),
                System.getenv("API_PASS"),
                "https://hk-tippspiel.com"
        )!!
    }

    /**
     * Logs out the API connection
     */
    override fun tearDown() {
        super.tearDown()
        this.apiConnection.logout()
    }

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

//    /**
//     * Tests querying the model using the API
//     */
//    fun testQuerying() {
//        val query = Goal.query(this.apiConnection)
//
//        val all = query.query()
//        assertTrue(all.size > 10)
//
//        query.addFilter("match_id", 55277)
//        val forMatch = query.query()
//        assertEquals(forMatch.size, 4)
//
//        query.addFilter("id", 80234)
//        val forId = query.query()[0]
//        assertEquals(forId.homeScore, 2)
//        assertEquals(forId.awayScore, 2)
//    }
}
