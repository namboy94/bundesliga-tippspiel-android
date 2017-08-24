/*
    Copyright 2017 Hermann Krumrey

    This file is part of bundesliga-tippspiel-android.

    bundesliga-tippspiel-android is an Android app that allows a user to
    manage their bets on the bundesliga-tippspiel website.

    bundesliga-tippspiel-android is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    bundesliga-tippspiel-android is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with bundesliga-tippspiel-android. If not, see <http://www.gnu.org/licenses/>.
*/

package net.namibsun.hktipp
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.CheckBox
import android.widget.EditText
import net.namibsun.hktipp.helper.getDefaultSharedPreferences
import net.namibsun.hktipp.helper.post
import net.namibsun.hktipp.helper.showErrorDialog
import org.jetbrains.anko.doAsync

/**
 * The Login Screen that enables the user to log in to the bundesliga-tippspiel
 * Service using an API key, which will be generated during the login process.
 * Credentials can be stored locally on the device, though the API key is stored
 * instead of a password
 */
class LoginActivity : AppCompatActivity() {

    /**
     * The shared preferences used to store the username and api key
     */
    private var prefs: SharedPreferences? = null

    /**
     * Initializes the Login Activity. Sets the OnClickListener of the
     * login button and sets the input fields with stored data if available
     * @param savedInstanceState: The Instance Information of the app.
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.login)

        this.prefs = getDefaultSharedPreferences(this)

        this.findViewById(R.id.login_screen_button).setOnClickListener { this.login() }

        // Set input elements with stored data
        if (this.prefs!!.getString("api_key", "") != "") {
            (this.findViewById(R.id.login_screen_password) as EditText).setText("******")
            // We set the password to "******" if the stored api key should be used
            // So basically, this WILL be problematic if a user has the password "******"
            // TODO Find another way of doing this without descriminating against "******"-passwords
        }
        val username = this.prefs!!.getString("username", "")
        if (username != "") {
            (this.findViewById(R.id.login_screen_username) as EditText).setText(username)
        }
    }

    /**
     * Attempts to log in the user. Will fetch the username, password/api key from either the
     * EditTexts or from the shared preferences, then asynchronously attempt to either log in
     * using a password or authorize and existing API Key
     */
    private fun login() {

        val username = (this.findViewById(R.id.login_screen_username) as EditText).text.toString()
        val password = (this.findViewById(R.id.login_screen_password) as EditText).text.toString()
        var apiKey = this.prefs!!.getString("api_key", "")
        Log.i("LoginActivity", "$username trying to log in.")

        this@LoginActivity.doAsync {

            // Log in or authorize the existing API Key
            val response = if (apiKey != "" && password == "******") {
                Log.i("LoginActivity", "Authorizing existing API key")
                val json = "{\"username\":\"$username\", \"api_key\":\"$apiKey\"}"
                post("authorize", json)
            }
            else {
                Log.i("LoginActivity", "Attempting to log in using password")
                val json = "{\"username\":\"$username\", \"password\":\"$password\"}"
                post("request_api_key", json)
            }

            // Update API Key if applicable
            if (response.has("key")) {
                apiKey = response.getString("key")
            }

            this@LoginActivity.handleLoginResponse(response.getString("status"), username, apiKey)

        }
    }

    /**
     * Handles the login response generated in the [login] method.
     * On success, this method stores the username and API key in the shared preferences, then
     * starts the Bet Activity
     * If the attempt was not successful, an error dialog will be shown
     * @param response: The 'status' of the JSON response of the login/authorization attempt
     * @param username: The username of the user tying to log in
     *
     */
    private fun handleLoginResponse(response: String, username: String, apiKey: String) {

        if (response == "success") { // Login successful

            Log.i("LoginActivity", "Login Successful")

            // Store the username and API key if remember box is checked
            val check = this@LoginActivity.findViewById(R.id.login_screen_remember) as CheckBox
            if (check.isChecked) {
                Log.i("LoginActivity", "Storing credentials in shared preferences")
                val editor = this@LoginActivity.prefs!!.edit()
                editor.putString("api_key", apiKey)
                editor.putString("username", username)
                editor.apply()
            }
            this@LoginActivity.runOnUiThread {
                net.namibsun.hktipp.helper.switchActivity(
                        this@LoginActivity, BetActivity::class.java, username, apiKey)
            }
        }
        else { // Login failed

            Log.i("LoginActivity", "Login Failed")
            this@LoginActivity.runOnUiThread {
                showErrorDialog(this@LoginActivity,
                        R.string.login_error_title, R.string.login_error_body)
            }
        }
    }
}
