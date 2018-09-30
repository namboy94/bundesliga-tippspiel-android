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

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import net.namibsun.hktipp.api.ApiConnection
import org.jetbrains.anko.doAsync

/**
 * Base class that implements some common methods
 */
abstract class BaseActivity : AppCompatActivity() {

    /**
     * Shows an error dialog.
     * @param titleResource: The resource of the error message title
     * @param bodyResource: The resource of the error message body
     */
    protected fun showErrorDialog(titleResource: Int, bodyResource: Int) {

        val errorTitle = this.getString(titleResource)
        val errorBody = this.getString(bodyResource)
        val errorDialogBuilder = AlertDialog.Builder(this)

        errorDialogBuilder.setTitle(errorTitle)
        errorDialogBuilder.setMessage(errorBody)
        errorDialogBuilder.setCancelable(true)
        errorDialogBuilder.setPositiveButton("Ok") {
            dialog, _ -> dialog!!.dismiss()
        }
        errorDialogBuilder.create()
        errorDialogBuilder.show()
    }

    /**
     * Starts a new activity
     * @param target: The activity to start
     * @param finish: Specifies whether or not the activity will be finished
     * @param bundle: Optional bundle object to pass on to the new activity
     */
    fun startActivity(target: Class<*>, finish: Boolean = true, bundle: Bundle? = null) {
        val intent = Intent(this, target)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        if (finish) {
            this.finish()
        }
        this.startActivity(intent)
    }

    /**
     * Starts the loading animation
     */
    protected abstract fun startLoadingAnimation()

    /**
     * Stops the loading animation
     */
    protected abstract fun stopLoadingAnimation()
}

/**
 * Activity class that should be used by activities that require an authorized API connection
 */
abstract class AuthorizedActivity : BaseActivity() {

    /**
     * The API connection to use for API calls
     */
    protected lateinit var apiConnection: ApiConnection

    /**
     * Initializes the Activity's apiConnection
     * @param savedInstanceState: The bundle provided by a previous activity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val apiConnection = ApiConnection.loadStored(this)

        if (apiConnection == null) {
            this.logout()
        } else {
            this.apiConnection = apiConnection
        }
    }

    /**
     * Logs out and returns to the Login Activity
     */
    protected fun logout() {

        val apiConnection = ApiConnection.loadStored(this)
        if (apiConnection != null) {
            this.doAsync {
                if (apiConnection.isAuthorized()) {
                    apiConnection.logout()
                }
            }
        }

        this.startActivity(LoginActivity::class.java, true)
    }
}
