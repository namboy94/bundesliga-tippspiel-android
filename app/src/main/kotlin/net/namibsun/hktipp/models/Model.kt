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
import android.util.Log
import net.namibsun.hktipp.api.ApiConnection
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

/**
 * Interface to be implemented by all model classes that can be queried
 */
interface QueryAble {

    /**
     * Generates a Query object with which the model may be queried
     * @param apiConnection: The API connection to use with the query
     * @return: The query object
     */
    fun query(apiConnection: ApiConnection): Query
}

/**
 * Class that enables the querying of models using the API
 * @param apiConnection: The API connection to use
 * @param endpoint: The API endpoint to use for querying
 * @param generatorFunc: A function that generates a model from a JSON object
 * @param validFilterKeys: The filter keys that are enabled for this query object
 */
open class Query(
    private val apiConnection: ApiConnection,
    private val endpoint: String,
    private val generatorFunc: (json: JSONObject) -> Model,
    private val validFilterKeys: List<String>
) {

    /**
     * The parameters to send with the request
     */
    private val params = mutableMapOf<String, Any>()

    /**
     * Adds a filter to the parameter map
     * @param key: The filter name
     * @param value: The filter value
     */
    fun addFilter(key: String, value: Any) {

        @Suppress("CascadeIf")
        if (key !in this.validFilterKeys) {
            Log.e("Invalid Filter Key", "Filter key $key not valid.")
        } else {
            this.params[key] = value
        }
    }

    /**
     * Executes the query
     * @return A list of model objects that are the result of the query
     */
    open fun query(): List<Model> {

        val resultKey = if (this.endpoint == "match") {
            this.endpoint + "es"
        } else {
            this.endpoint + "s"
        }

        val resp = if ("id" in this.params) {
            this.apiConnection.get("${this.endpoint}/${this.params["id"]}", mapOf())
        } else {
            this.apiConnection.get(this.endpoint, this.params)
        }

        val objects = resp.getJSONObject("data").getJSONArray(resultKey)
        val models = mutableListOf<Model>()

        for (i in 0 until objects.length()) {
            val json = objects.getJSONObject(i)
            models.add(this.generatorFunc(json))
        }

        return models
    }
}
