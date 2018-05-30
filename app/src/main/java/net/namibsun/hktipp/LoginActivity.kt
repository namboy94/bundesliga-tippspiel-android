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
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.CheckBox
import android.widget.EditText
import net.namibsun.hktipp.helper.showErrorDialog
import net.namibsun.hktipp.helper.getApiKeyFromSharedPreferences
import net.namibsun.hktipp.helper.getUsernameFromPreferences
import net.namibsun.hktipp.helper.storeApiKeyInSharedPreferences
import net.namibsun.hktipp.helper.storeUsernameInSharedPreferences
import net.namibsun.hktipp.helper.post
import org.jetbrains.anko.doAsync
import org.json.JSONObject

/**
 * The Login Screen that enables the user to log in to the bundesliga-tippspiel
 * Service using an API key, which will be generated during the login process.
 * Credentials can be stored locally on the device, though the API key is stored
 * instead of a password
 */
class LoginActivity : AppCompatActivity() {

    /**
     * Initializes the Login Activity. Sets the OnClickListener of the
     * login button and sets the input fields with stored data if available
     * @param savedInstanceState: The Instance Information of the app.
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.login)

        this.findViewById(R.id.login_screen_button).setOnClickListener { this.login() }
        this.findViewById(R.id.login_screen_logo).setOnClickListener { this.login() }

        // Set input elements with stored data
        if (getApiKeyFromSharedPreferences(this) != null) {
            (this.findViewById(R.id.login_screen_password) as EditText).setText("******")
            // We set the password to "******" if the stored api key should be used
            // So basically, this WILL be problematic if a user has the password "******"
            // TODO Find another way of doing this without discriminating against "******"-passwords
        }
        val username = getUsernameFromPreferences(this)
        if (username != null) {
            (this.findViewById(R.id.login_screen_username) as EditText).setText(username)
        }

        this.findViewById(R.id.login_screen_register_button).setOnClickListener {
            val uri = Uri.parse("https://hk-tippspiel.com/signup.php")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            this.startActivity(intent)
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
        val apiKey = getApiKeyFromSharedPreferences(this@LoginActivity)
        Log.i("LoginActivity", "$username trying to log in.")

        this.setUiElementEnabledState(false)
        val animation = AnimationUtils.loadAnimation(this, R.anim.rotate)
        this.findViewById(R.id.login_screen_logo).startAnimation(animation)

        this@LoginActivity.doAsync {

            // Log in or authorize the existing API Key
            val response = if (apiKey != "" && password == "******") {
                Log.i("LoginActivity", "Authorizing existing API key")
                val json = "{\"username\":\"$username\", \"api_key\":\"$apiKey\"}"
                post("authorize", json)
            } else {
                Log.i("LoginActivity", "Attempting to log in using password")
                val json = "{\"username\":\"$username\", \"password\":\"$password\"}"
                post("request_api_key", json)
            }

            this@LoginActivity.runOnUiThread {
                this@LoginActivity.setUiElementEnabledState(true)
                this@LoginActivity.findViewById(R.id.login_screen_logo).clearAnimation()
            }
            this@LoginActivity.handleLoginResponse(response, username, apiKey)
        }
    }

    /**
     * Handles the login response generated in the [login] method.
     * On success, this method stores the username and API key in the shared preferences, then
     * starts the Bet Activity
     * If the attempt was not successful, an error dialog will be shown
     * @param response: The JSON response of the login/authorization attempt
     * @param username: The username of the user tying to log in
     * @param apiKey: The stored API key, which can be null if no API key was stored
     */
    private fun handleLoginResponse(response: JSONObject, username: String, apiKey: String?) {

        if (response.getString("status") == "success") { // Login successful

            Log.i("LoginActivity", "Login Successful")

            // Check for valid API key
            val validApiKey = if (response.has("key")) {
                response.getString("key") // Login API Action
            } else {
                apiKey!! // Authorize API Action
            }

            // Store the username and API key if remember box is checked
            val check = this@LoginActivity.findViewById(R.id.login_screen_remember) as CheckBox
            if (check.isChecked) {
                Log.i("LoginActivity", "Storing credentials in shared preferences")
                storeUsernameInSharedPreferences(this@LoginActivity, username)
                storeApiKeyInSharedPreferences(this@LoginActivity, validApiKey)
            }
            this@LoginActivity.runOnUiThread {
                net.namibsun.hktipp.helper.switchActivity(
                        this@LoginActivity, BetActivity::class.java, username, validApiKey)
            }
        } else { // Login failed

            Log.i("LoginActivity", "Login Failed")

            this@LoginActivity.runOnUiThread {
                showErrorDialog(this@LoginActivity,
                        R.string.login_error_title, R.string.login_error_body)
            }
        }
    }

    /**
     * Enables or disables all user-editable UI elements
     * @param state: Sets the enabled state of the elements
     */
    private fun setUiElementEnabledState(state: Boolean) {
        this.findViewById(R.id.login_screen_logo).isEnabled = state
        this.findViewById(R.id.login_screen_button).isEnabled = state
        this.findViewById(R.id.login_screen_username).isEnabled = state
        this.findViewById(R.id.login_screen_password).isEnabled = state
        this.findViewById(R.id.login_screen_remember).isEnabled = state
    }
}
