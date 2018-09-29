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

import org.json.JSONObject

/**
 * Model class that models an email reminder
 * @param id: The database ID of the email reminder
 * @param userId: The ID of the user whose email reminder settings this object represents
 * @param user: The user whose email reminder settings this object represents
 * @param reminderTime: The amount of seconds before a match that the reminder is triggered for
 * @param lastReminder: The last time the reminder was triggered
 */
data class EmailReminder(
    val id: Int,
    val userId: Int,
    val user: User,
    val reminderTime: Int,
    val lastReminder: String
) : Model {

    /**
     * Converts the model object into a JSON object
     * @return The generated JSON object
     */
    override fun toJson(): JSONObject {
        return JSONObject("""{
            "id": $id,
            "user_id": $userId,
            "user": ${user.toJson()},
            "reminder_time": $reminderTime,
            "last_reminder": "$lastReminder"
        }""".trimIndent())
    }

    /**
     * Companion object that implements static methods to generate the model using a
     * JSONObject.
     */
    companion object : ModelGenerator {

        /**
         * Generates the model using a JSONObject
         * @param data: The JSON to parse
         * @return: The generated model
         */
        override fun fromJson(data: JSONObject): EmailReminder {
            return EmailReminder(
                    id = data.getInt("id"),
                    userId = data.getInt("user_id"),
                    user = User.fromJson(data.getJSONObject("user")),
                    reminderTime = data.getInt("reminder_time"),
                    lastReminder = data.getString("last_reminder")
            )
        }
    }
}
