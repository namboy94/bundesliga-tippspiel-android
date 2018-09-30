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

package net.namibsun.hktipp.api

import java.time.Instant
import junit.framework.TestCase

/**
 * Class that tests the ApiConnection class
 */
class ApiConnectionTest : TestCase() {

    /**
     * Executes the default login
     * @return The generated ApiConnection
     */
    private fun login(): ApiConnection {
        return ApiConnection.login(
                System.getenv("API_USER"),
                System.getenv("API_PASS"),
                "https://develop.hk-tippspiel.com"
        )!!
    }

    /**
     * Tests a failed login attempt
     */
    fun testFailedLogin() {
        val failed = ApiConnection.login("a", "b")
        assertEquals(failed, null)
    }

    /**
     * Tests logging in, then logging out
     */
    fun testLoggingInAndLoggingOut() {
        val connection = this.login()
        assertTrue(connection.isAuthorized())
        assertEquals(System.getenv("API_USER"), connection.user.username)
        val now = Instant.now().epochSecond
        assertTrue(connection.expiration > now + 1728000)
        connection.logout()
        assertFalse(connection.isAuthorized())
    }
}
