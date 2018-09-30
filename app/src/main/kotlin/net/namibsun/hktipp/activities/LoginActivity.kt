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

package net.namibsun.hktipp.activities
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import net.namibsun.hktipp.R
import net.namibsun.hktipp.api.ApiConnection
import net.namibsun.hktipp.models.User
import org.jetbrains.anko.doAsync
import org.json.JSONObject

/**
 * The Login Screen that enables the user to log in to the bundesliga-tippspiel
 * Service using an API key, which will be generated during the login process.
 * Credentials can be stored locally on the device, though the API key is stored
 * instead of a password
 */
class LoginActivity : BaseActivity() {

    /**
     * Initializes the Login Activity. Sets the OnClickListener of the
     * login button and sets the input fields with stored data if available
     * @param savedInstanceState: The Instance Information of the app.
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val existingApiConnection = ApiConnection.loadStored(this)
        if (existingApiConnection != null) {
            this.startActivity(BetActivity::class.java, true)
        }

        this.setContentView(R.layout.login)

        this.findViewById<View>(R.id.login_screen_button).setOnClickListener { this.login() }
        this.findViewById<View>(R.id.login_screen_logo).setOnClickListener { this.login() }
        this.findViewById<View>(R.id.login_screen_register_button).setOnClickListener {
            // TODO Register Activity
            val uri = Uri.parse("https://hk-tippspiel.com/register")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            this.startActivity(intent)
        }

        val prefs = this.getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE)
        val user = prefs.getString("user", null)

        if (user != null) {
            val userData = User.fromJson(JSONObject(user))
            this.findViewById<EditText>(R.id.login_screen_username).setText(userData.username)
        }
    }

    /**
     * Attempts to log in the user. Will fetch the username, password/api key from either the
     * EditTexts or from the shared preferences, then asynchronously attempt to either log in
     * using a password or authorize and existing API Key
     */
    private fun login() {

        val username = this.findViewById<EditText>(R.id.login_screen_username).text.toString()
        val password = this.findViewById<EditText>(R.id.login_screen_password).text.toString()

        Log.i("LoginActivity", "$username trying to log in.")
        this.startLoadingAnimation()

        this.doAsync {

            val apiConnection = ApiConnection.login(username, password)

            this@LoginActivity.runOnUiThread {
                this@LoginActivity.stopLoadingAnimation()
            }

            if (apiConnection == null) {
                Log.i("LoginActivity", "Failed to log in")
                this@LoginActivity.runOnUiThread {
                    this@LoginActivity.showErrorDialog(
                            R.string.login_error_title,
                            R.string.login_error_body
                    )
                }
            } else {
                Log.i("LoginActivity", "Successfully logged in")
                apiConnection.store(this@LoginActivity)
                this@LoginActivity.runOnUiThread {
                    this@LoginActivity.startActivity(
                            Intent(this@LoginActivity, BetActivity::class.java)
                    )
                }
            }
        }
    }

    /**
     * Starts the login animation
     */
    override fun startLoadingAnimation() {
        this.findViewById<View>(R.id.login_screen_logo).isEnabled = false
        this.findViewById<View>(R.id.login_screen_button).isEnabled = false
        this.findViewById<View>(R.id.login_screen_username).isEnabled = false
        this.findViewById<View>(R.id.login_screen_password).isEnabled = false
        val animation = AnimationUtils.loadAnimation(this, R.anim.rotate)
        this.findViewById<View>(R.id.login_screen_logo).startAnimation(animation)
    }

    /**
     * Ends the login animation
     */
    override fun stopLoadingAnimation() {
        this.findViewById<View>(R.id.login_screen_logo).isEnabled = true
        this.findViewById<View>(R.id.login_screen_button).isEnabled = true
        this.findViewById<View>(R.id.login_screen_username).isEnabled = true
        this.findViewById<View>(R.id.login_screen_password).isEnabled = true
        this.findViewById<View>(R.id.login_screen_logo).clearAnimation()
    }
}
