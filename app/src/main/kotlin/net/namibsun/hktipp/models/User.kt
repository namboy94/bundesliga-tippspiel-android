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
 * Model class that models a User
 * @param id: The database ID of the user
 * @param username: The user's username
 * @param email: The email address of the user
 * @param confirmed: Indicates whether or not the user is confirmed
 */
data class User(
    val id: Int,
    val username: String,
    val email: String,
    val confirmed: Boolean
) : Model {

    /**
     * Converts the model object into a JSON object
     * @return The generated JSON object
     */
    override fun toJson(): JSONObject {
        return JSONObject("""{
            "id": $id,
            "username": "$username",
            "email": $email,
            "confirmed": $confirmed
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
        override fun fromJson(data: JSONObject): User {
            return User(
                    id = data.getInt("id"),
                    username = data.getString("username"),
                    email = data.getString("email"),
                    confirmed = data.getBoolean("confirmed")
            )
        }
    }
}
