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
import java.io.Serializable

/**
 * Interface that models common operations an API Model class should have
 */
interface Model : Serializable {

    /**
     * Method that generates a JSON Object from the model data
     */
    fun toJson(): JSONObject
}

/**
 * Interface that makes it possible to generate a Model object using JSON data
 * This class should be inherited by the companion object of each Model class.
 */
interface ModelGenerator {

    /**
     * This method must be implemented by child classes. It generates a
     * new Model object from a JSONObject object.
     * @param data: The JSON data to parse
     * @return The generated Model object
     */
    fun fromJson(data: JSONObject): Model
}
