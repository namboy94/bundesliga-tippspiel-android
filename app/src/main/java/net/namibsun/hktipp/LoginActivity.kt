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
import android.util.Log
import android.widget.CheckBox
import android.widget.EditText
import net.namibsun.hktipp.apiwrap.post

/**
 * The Login Screen that enables the user
 */
class LoginActivity : AppCompatActivity() {

    private var prefs: SharedPreferences? = null

    /**
     * Initializes the Login Activity
     * @param savedInstanceState: The Instance Information of the app.
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.login)

        this.prefs = this.getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE)

        // Set OnClickListener
        this.findViewById(R.id.login_screen_button).setOnClickListener { this.login() }

        if (this.prefs!!.getString("api_key", "") != "") {
            (this.findViewById(R.id.login_screen_password) as EditText).setText("******")
        }
        val username = this.prefs!!.getString("username", "")
        if (username != "") {
            (this.findViewById(R.id.login_screen_username) as EditText).setText(username)
        }
    }

    /**
     * Tries to log in the user
     */
    private fun login() {

        val usernameEdit = this.findViewById(R.id.login_screen_username) as EditText
        val passwordEdit = this.findViewById(R.id.login_screen_password) as EditText
        val username = usernameEdit.text.toString()
        val password = passwordEdit.text.toString()
        val api_key = this.prefs!!.getString("api_key", "")

        LoginTask().execute(username, password, api_key)

    }

    private fun switchToBetsActivity(bundle: Bundle) {
        val intent = Intent(this, BetActivity::class.java)
        intent.putExtras(bundle)
        this.startActivity(intent)
    }

    private fun showLoginErrorDialog() {

        val errorDialogBuilder = AlertDialog.Builder(this)
        errorDialogBuilder.setTitle(getString(R.string.login_error_title))
        errorDialogBuilder.setMessage(getString(R.string.login_error_body))
        errorDialogBuilder.setCancelable(true)
        errorDialogBuilder.setPositiveButton("Ok") { dialog, _ -> dialog!!.dismiss() }
        errorDialogBuilder.create()
        errorDialogBuilder.show()
    }

    inner class LoginTask: AsyncTask<String, Void, Void>() {

        override fun doInBackground(vararg params: String?): Void? {

            val username = params[0]
            val password = params[1]
            var api_key = params[2]

            val response = if (api_key != "" && password == "******") {
                val json = "{\"username\":\"$username\", \"api_key\":\"$api_key\"}"
                post("authorize", json)
            }
            else {
                val json = "{\"username\":\"$username\", \"password\":\"$password\"}"
                post("request_api_key", json)
            }

            Log.e("LOG", response.toString())

            if (response.get("status") == "success") {

                if (response.has("key")) {
                    api_key = response.getString("key")
                }

                if ((findViewById(R.id.login_screen_remember) as CheckBox).isChecked) {
                    val editor = prefs!!.edit()
                    editor.putString("api_key", response.get("key").toString())
                    editor.putString("username", username)
                    editor.apply()
                }
                val bundle = Bundle()
                bundle.putString("username", username)
                bundle.putString("api_key", api_key)

                runOnUiThread({ switchToBetsActivity(bundle) })
            }
            else {
                runOnUiThread({ showLoginErrorDialog() })
            }

            return null

        }
    }
}


