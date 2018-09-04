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

package net.namibsun.hktipp
import org.junit.Test
import kotlin.test.assertEquals
import org.json.JSONObject
import net.namibsun.hktipp.data.TeamData

/**
 * A Unittest class quickly thrown together to test coverage reporting
 */
class DummyTest {

    /**
     * Tests creating a TeamData object
     */
    @Test
    fun dummyTest() {

        val x = JSONObject()
        x.put("id", 1)
        x.put("name", "AAA")
        x.put("short_name", "AAA")
        x.put("abbreviation", "AAA")
        x.put("icon_png", "AAA")

        val y = TeamData(x)

        assertEquals(y.id, 1)
    }
}
