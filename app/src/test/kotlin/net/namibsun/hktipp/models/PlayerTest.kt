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
 * Class that tests the Player model class
 */
class PlayerTest : TestCase() {

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
                "https://develop.hk-tippspiel.com"
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
     * A sample JSON string for a player
     */
    val sampleJson = """{
        "id": 1,
        "name": "Thomas Müller",
        "team_id": 1,
        "team": ${TeamTest().sampleJson}
    }""".trimIndent()

    /**
     * Tests generating a model object using the sample JSON string
     */
    fun testGenerating() {
        val team = Team.fromJson(JSONObject(TeamTest().sampleJson))
        val player = Player.fromJson(JSONObject(this.sampleJson))
        assertEquals(player.id, 1)
        assertEquals(player.name, "Thomas Müller")
        assertEquals(player.teamId, 1)
        assertEquals(player.team, team)
        assertEquals(player.toJson().toString(), JSONObject(this.sampleJson).toString())
    }

    /**
     * Tests querying the model using the API
     */
    fun testQuerying() {
        val query = Player.query(this.apiConnection)

        val all = query.query()
        assertTrue(all.size > 10)

        query.addFilter("team_id", 40)
        val forTeam = query.query()
        assertTrue(all.size > forTeam.size)

        query.addFilter("id", 1478)
        val forId = query.query()[0]
        assertEquals(forId.name, "Lewandowski")
    }
}
