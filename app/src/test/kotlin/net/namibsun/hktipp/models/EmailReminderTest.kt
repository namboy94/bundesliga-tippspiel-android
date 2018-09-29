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
 * Class that tests the EmailReminder model class
 */
class EmailReminderTest : TestCase() {

    /**
     * A sample JSON string for an email reminder
     */
    private val sampleJson = """{
        "id": 1,
        "user_id": 1,
        "user": ${UserTest().sampleJson},
        "reminder_time": 60,
        "last_reminder": "2017-01-01:01-02-03"
    }""".trimIndent()

    /**
     * Tests generating a model object using the sample JSON string
     */
    fun testGenerating() {
        val user = User.fromJson(JSONObject(UserTest().sampleJson))
        val reminder = EmailReminder.fromJson(JSONObject(this.sampleJson))
        assertEquals(reminder.id, 1)
        assertEquals(reminder.userId, 1)
        assertEquals(reminder.user, user)
        assertEquals(reminder.reminderTime, 60)
        assertEquals(reminder.lastReminder, "2017-01-01:01-02-03")
        assertEquals(reminder.toJson().toString(), JSONObject(this.sampleJson).toString())
    }
}
