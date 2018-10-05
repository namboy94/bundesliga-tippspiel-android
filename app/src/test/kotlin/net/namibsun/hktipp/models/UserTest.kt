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
import org.json.JSONObject

/**
 * Class that tests the User model class
 */
class UserTest : TestCase() {

    /**
     * A sample JSON string for a user
     */
    val sampleJson = """{
        "id": 1,
        "username": "Tester",
        "email": "test@hk-tippspiel.com",
        "confirmed": true
    }""".trimIndent()

    /**
     * Tests generating a model object using the sample JSON string
     */
    fun testGenerating() {
        val user = User.fromJson(JSONObject(this.sampleJson))
        assertEquals(user.id, 1)
        assertEquals(user.username, "Tester")
        assertEquals(user.email, "test@hk-tippspiel.com")
        assertEquals(user.confirmed, true)
        assertEquals(user.toJson().toString(), JSONObject(this.sampleJson).toString())
    }
}
