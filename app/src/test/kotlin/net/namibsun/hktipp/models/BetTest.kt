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
 * Class that tests the Bet model class
 */
class BetTest : TestCase() {

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
     * A sample JSON string for a bet
     */
    private val sampleJson = """{
        "id": 1,
        "user_id": 1,
        "user": ${UserTest().sampleJson},
        "match_id": 1,
        "match": ${MatchTest().sampleJson},
        "home_score": 2,
        "away_score": 3,
        "points": 15
    }""".trimIndent()

    /**
     * Tests generating a model object using the sample JSON string
     */
    fun testGenerating() {
        val user = User.fromJson(JSONObject(UserTest().sampleJson))
        val match = Match.fromJson(JSONObject(MatchTest().sampleJson))
        val bet = Bet.fromJson(JSONObject(this.sampleJson))
        assertEquals(bet.id, 1)
        assertEquals(bet.userId, 1)
        assertEquals(bet.user, user)
        assertEquals(bet.matchId, 1)
        assertEquals(bet.match, match)
        assertEquals(bet.homeScore, 2)
        assertEquals(bet.awayScore, 3)
        assertEquals(bet.points, 15)
        assertEquals(bet.toJson().toString(), JSONObject(this.sampleJson).toString())
    }

    /**
     * Tests querying the model using the API
     */
    fun testQuerying() {

        Bet.place(this.apiConnection, listOf(
                MinimalBet(58877, 2, 1),
                MinimalBet(58878, 0, 1)
        ))
        val query = Bet.query(this.apiConnection)

        val all = query.query()
        assertTrue(all.size >= 2)

        query.addFilter("user_id", this.apiConnection.user.id)
        val allForUser = query.query()
        assertEquals(allForUser.size, 2)

        query.addFilter("match_id", 58877)
        val byMatch = query.query()[0]
        assertEquals(byMatch.homeScore, 2)
        assertEquals(byMatch.awayScore, 1)

        query.addFilter("id", byMatch.id)
        val byId = query.query()[0]
        assertEquals(byMatch, byId)
    }
}
