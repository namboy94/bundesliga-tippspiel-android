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
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import net.namibsun.hktipp.api.ApiConnection
import org.jetbrains.anko.doAsync

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

        val existingApiConnection = ApiConnection.loadStored(this)
        if (existingApiConnection != null) {
            this.startActivity(Intent(this, BetActivity::class.java))
        }

        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.login)

        this.findViewById<View>(R.id.login_screen_button).setOnClickListener { this.login() }
        this.findViewById<View>(R.id.login_screen_logo).setOnClickListener { this.login() }
        this.findViewById<View>(R.id.login_screen_register_button).setOnClickListener {
            // this.startActivity(Intent(this, RegisterActivity::class.java))
        }

        val prefs = this.getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE)
        val username = prefs.getString("username", null)

        if (username != null) {
            this.findViewById<EditText>(R.id.login_screen_username).setText(username)
        }

        this.findViewById<View>(R.id.login_screen_register_button).setOnClickListener {
            val uri = Uri.parse("https://hk-tippspiel.com/register")
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

        val username = this.findViewById<EditText>(R.id.login_screen_username).text.toString()
        val password = this.findViewById<EditText>(R.id.login_screen_password).text.toString()

        Log.i("LoginActivity", "$username trying to log in.")
        this.startLoginAnimation()

        this.doAsync {

            val apiConnection = ApiConnection.login(username, password)

            this@LoginActivity.runOnUiThread {
                this@LoginActivity.endLoginAnimation()
            }

            if (apiConnection == null) {

                Log.i("LoginActivity", "Failed to log in")

                val errorTitle = this@LoginActivity.getString(R.string.login_error_title)
                val errorBody = this@LoginActivity.getString(R.string.login_error_body)
                val errorDialogBuilder = AlertDialog.Builder(this@LoginActivity)
                errorDialogBuilder.setTitle(errorTitle)
                errorDialogBuilder.setMessage(errorBody)
                errorDialogBuilder.setCancelable(true)
                errorDialogBuilder.setPositiveButton("Ok") {
                    dialog, _ -> dialog!!.dismiss()
                }
                errorDialogBuilder.create()

                this@LoginActivity.runOnUiThread {
                    errorDialogBuilder.show()
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
    private fun startLoginAnimation() {
        this.setUiElementEnabledState(false)
        val animation = AnimationUtils.loadAnimation(this, R.anim.rotate)
        this.findViewById<View>(R.id.login_screen_logo).startAnimation(animation)
    }

    /**
     * Ends the login animation
     */
    private fun endLoginAnimation() {
        this.setUiElementEnabledState(true)
        this.findViewById<View>(R.id.login_screen_logo).clearAnimation()
    }

    /**
     * Enables or disables all user-editable UI elements
     * @param state: Sets the enabled state of the elements
     */
    private fun setUiElementEnabledState(state: Boolean) {
        this.findViewById<View>(R.id.login_screen_logo).isEnabled = state
        this.findViewById<View>(R.id.login_screen_button).isEnabled = state
        this.findViewById<View>(R.id.login_screen_username).isEnabled = state
        this.findViewById<View>(R.id.login_screen_password).isEnabled = state
    }
}
