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
 * Class that tests the ApiKey model class
 */
class ApiKeyTest {

    /**
     * A sample JSON string for an API key
     */
    private val sampleJson = """{
        "id": 1,
        "user_id": 1,
        "user": ${UserTest().sampleJson},
        "creation_time": 60
    }""".trimIndent()

    /**
     * Tests generating a model object using the sample JSON string
     */
    @Test
    fun testGenerating() {
        val user = User.fromJson(JSONObject(UserTest().sampleJson))
        val apiKey = ApiKey.fromJson(JSONObject(this.sampleJson))
        assertEquals(apiKey.id, 1)
        assertEquals(apiKey.userId, 1)
        assertEquals(apiKey.user, user)
        assertEquals(apiKey.creationTime, 60)
        assertEquals(apiKey.toJson().toString(), JSONObject(this.sampleJson).toString())
    }
}
