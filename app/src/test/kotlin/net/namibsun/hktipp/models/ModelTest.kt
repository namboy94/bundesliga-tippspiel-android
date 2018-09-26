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
 * A Unittest class that tests the Model interface
 */
class ModelTest {

    data class A(val a: Int) : Model {
        override fun toJson(): JSONObject {
            val json = JSONObject()
            json.put("a", this.a)
            return json
        }
        companion object : ModelGenerator {
            override fun fromJson(data: JSONObject): Model {
                return A(data.getInt("a"))
            }
        }
    }

    data class B(val b: Int) : Model {
        override fun toJson(): JSONObject {
            val json = JSONObject()
            json.put("b", this.b)
            return json
        }
        companion object : ModelGenerator {
            override fun fromJson(data: JSONObject): Model {
                return B(data.getInt("b"))
            }
        }
    }

    /**
     * Tests creating two class that inherit from Model, then tests that they work as intended
     */
    @Test
    fun testInterface() {

        val json = JSONObject()
        json.put("a", 1)
        json.put("b", 2)

        val a = A.fromJson(json) as A
        val b = B.fromJson(json) as B

        assertEquals(A(1), a)
        assertNotEquals(A(2), a)
        assertEquals(B(2), b)
        assertNotEquals(B(1), b)

        assertEquals(a.a, 1)
        assertEquals(b.b, 2)

        assertEquals(JSONObject("{a: 1}").toString(), a.toJson().toString())
        assertEquals(JSONObject("{b: 2}").toString(), b.toJson().toString())
        assertNotEquals(a.toJson(), b.toJson())
    }
}
