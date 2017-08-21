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
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.CheckBox
import android.widget.EditText
import net.namibsun.hktipp.apiwrap.post

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

        this.prefs = this.getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE)

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
     * EditTexts or from the shared preferences, then start the LoginTask AsyncTask.
     */
    private fun login() {

        val usernameEdit = this.findViewById(R.id.login_screen_username) as EditText
        val passwordEdit = this.findViewById(R.id.login_screen_password) as EditText
        val username = usernameEdit.text.toString()
        val password = passwordEdit.text.toString()
        val api_key = this.prefs!!.getString("api_key", "")

        LoginTask().execute(username, password, api_key)
    }

    /**
     * Switches to the Bets Activity.
     * @param username: The username with which the user logged in
     * @param apiKey: The API key used for authentication
     */
    private fun switchToBetsActivity(username: String, apiKey: String) {

        val bundle = Bundle()
        bundle.putString("username", username)
        bundle.putString("api_key", apiKey)

        val intent = Intent(this, BetActivity::class.java)
        intent.putExtras(bundle)
        this.startActivity(intent)
    }

    /**
     * Shows an error dialog indicating that the login process failed
     */
    private fun showLoginErrorDialog() {

        val errorDialogBuilder = AlertDialog.Builder(this)
        errorDialogBuilder.setTitle(getString(R.string.login_error_title))
        errorDialogBuilder.setMessage(getString(R.string.login_error_body))
        errorDialogBuilder.setCancelable(true)
        errorDialogBuilder.setPositiveButton("Ok") { dialog, _ -> dialog!!.dismiss() }
        errorDialogBuilder.create()
        errorDialogBuilder.show()
    }

    /**
     * Async Task that tries to log in to the bundesliga-tippspiel website
     */
    inner class LoginTask: AsyncTask<String, Void, Void>() {

        /**
         * Attempts to log in
         * @param params: The parameters provided by the [login] method
         */
        override fun doInBackground(vararg params: String?): Void? {

            // Get the parameters
            val username = params[0]!!
            val password = params[1]!!
            var apiKey = params[2]!!

            // Log in or authorize the existing API Key
            val response = if (apiKey != "" && password == "******") {
                val json = "{\"username\":\"$username\", \"api_key\":\"$apiKey\"}"
                post("authorize", json)
            }
            else {
                val json = "{\"username\":\"$username\", \"password\":\"$password\"}"
                post("request_api_key", json)
            }

            if (response.get("status") == "success") {  // Login successful

                // Update API Key if applicable
                if (response.has("key")) {
                    apiKey = response.getString("key")
                }

                // Store the username and API key if remember box is checked
                if ((findViewById(R.id.login_screen_remember) as CheckBox).isChecked) {
                    val editor = prefs!!.edit()
                    editor.putString("api_key", apiKey)
                    editor.putString("username", username)
                    editor.apply()
                }
                runOnUiThread({ switchToBetsActivity(username, apiKey) })
            }
            else {  // Login failed
                runOnUiThread({ showLoginErrorDialog() })
            }
            return null
        }
    }
}


