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
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import net.namibsun.hktipp.apiwrap.getBets
import net.namibsun.hktipp.apiwrap.getMatches
import net.namibsun.hktipp.apiwrap.placeBets
import net.namibsun.hktipp.views.BetView
import org.json.JSONArray
import java.io.IOException

/**
 * This activity allows a user to place bets, as well as view already placed bets
 */
class BetActivity : AppCompatActivity() {

    /**
     * The Bet Views for the currently selected match day
     */
    //private var betViews = mutableListOf<BetView>()
    private var betViews =mutableMapOf<Int, List<BetView>>()

    /**
     * The username of the logged in user
     */
    private var username: String? = null

    /**
     * The API Key of the logged in user
     */
    private var apiKey: String? = null

    /**
     * The Match Day to be displayed. -1 indicates that the current match day should be used
     */
    private var matchDay: Int = -1

    /**
     * Initializes the Activity. Sets the OnClickListeners for the buttons and starts
     * fetching bet and match data asynchronously.
     * Sets the username and apiKey instance variables
     * @param savedInstanceState: The Instance Information of the app.
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        this.setContentView(R.layout.bets)
        super.onCreate(savedInstanceState)

        val bundle = intent.extras
        this.username = bundle.getString("username")
        this.apiKey = bundle.getString("api_key")

        this.findViewById(R.id.bets_submit_button).setOnClickListener {
            BetPlacer().execute()
        }

        // Set button actions to go to next or previous matchday
        this.findViewById(R.id.bets_previous_button).setOnClickListener {
            if (this.matchDay > 1) {
                this.matchDay--
                DataGetter().execute()
            }
            this.findViewById(R.id.bets_previous_button).isEnabled = this.matchDay != 1
            this.findViewById(R.id.bets_next_button).isEnabled = this.matchDay != 34
        }
        this.findViewById(R.id.bets_next_button).setOnClickListener {
            if (this.matchDay < 34) {
                this.matchDay++
                DataGetter().execute()

            }
            this.findViewById(R.id.bets_previous_button).isEnabled = this.matchDay != 1
            this.findViewById(R.id.bets_next_button).isEnabled = this.matchDay != 34
        }

        // Get Data for current matchday
        DataGetter().execute()

    }

    /**
     * Returns to the LoginActivity and optionally deletes any stored credentials
     * @param deleteStoredCredentials: Will delete all stored credentials if set to true
     */
    private fun logOut(deleteStoredCredentials: Boolean = false) {

        if (deleteStoredCredentials) {
            val editor = this.getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE).edit()
            editor.clear()
            editor.apply()
        }

        this.startActivity(Intent(this, LoginActivity::class.java))
    }

    /**
     * Removes all current bets from the activity, then adds all of the BetViews
     * found in the [betViews] variable for the current matchday
     */
    private fun renderBetViews() {

        val list = this.findViewById(R.id.bets_list) as LinearLayout
        val bets = this.betViews[this.matchDay]
        if (bets != null) {
            list.removeAllViews()
            for (view in bets) {
                list.addView(view)
            }
        }
    }

    /**
     * Shows an error dialog indicating that fetching the data has failed.
     * Once the user dismisses the message, they will be returned to the login screen
     */
    private fun showDataFetchingErrorDialogAndLogout() {

        val errorDialogBuilder = AlertDialog.Builder(this)
        errorDialogBuilder.setTitle(getString(R.string.bets_fetching_error_title))
        errorDialogBuilder.setMessage(getString(R.string.bets_fetching_error_body))
        errorDialogBuilder.setCancelable(false)
        errorDialogBuilder.setPositiveButton("Ok") { dialog, _ -> dialog!!.dismiss() }
        errorDialogBuilder.create()
        errorDialogBuilder.show()
        this.logOut()
    }

    /**
     * Shows an error dialog indicating that betting failed
     */
    private fun showBetErrorDialog() {

        val errorDialogBuilder = AlertDialog.Builder(this)
        errorDialogBuilder.setTitle(getString(R.string.bets_betting_error_title))
        errorDialogBuilder.setMessage(getString(R.string.bets_betting_error_body))
        errorDialogBuilder.setCancelable(true)
        errorDialogBuilder.setPositiveButton("Ok") { dialog, _ -> dialog!!.dismiss() }
        errorDialogBuilder.create()
        errorDialogBuilder.show()
        this.logOut()
    }

    /**
     * AsyncTask which fetches the match and bet data for the currently selected matchday
     */
    inner class DataGetter: AsyncTask<Void, Void, Void>() {

        /**
         * First fetches the match data, then the bets data. Downloads the Team Logos, then
         * initializes the Bet Views for each match and renders them on the UI thread afterwards
         */
        override fun doInBackground(vararg params: Void?): Void? {

            // Clear previous Views and start Progress Spinner
            this@BetActivity.runOnUiThread({
                if (this@BetActivity.matchDay != -1) {
                    this@BetActivity.betViews[this@BetActivity.matchDay] = mutableListOf()
                    this@BetActivity.renderBetViews()
                }
                this@BetActivity.findViewById(R.id.bets_progress).visibility = View.VISIBLE
            })

            try {

                val previousBetViews = betViews[this@BetActivity.matchDay]
                val newBetViews = mutableListOf<BetView>()

                val username = this@BetActivity.username!!
                val apiKey = this@BetActivity.apiKey!!
                val matchday = this@BetActivity.matchDay

                val matches = getMatches(username, apiKey, matchday)
                val bets = getBets(username, apiKey, matchday)

                // Update Matchday
                this@BetActivity.matchDay = matches.getJSONObject(0).getInt("matchday")

                // Initialize the BetViews
                for (i in 0..(matches.length() -1)) {
                    val match = matches.getJSONObject(i)
                    val matchId = match.getInt("id")
                    val homeTeam = match.getJSONObject("home_team")
                    val awayTeam = match.getJSONObject("away_team")
                    val homeTeamLogo = homeTeam.getString("icon")
                    val awayTeamLogo = awayTeam.getString("icon")

                    val matchView = BetView(this@BetActivity)
                    matchView.setMatchData(
                            matchId,
                            homeTeam.getString("shortname"),
                            awayTeam.getString("shortname"),
                            match.getBoolean("started"),
                            homeTeamLogo,
                            awayTeamLogo)

                    // Search for bet that is associated with match and set the bet data
                    (0..(bets.length() - 1))
                            .map { bets.getJSONObject(it) }
                            .filter { it.getJSONObject("match").getInt("id") == matchId }
                            .forEach {
                                Log.e("SE", "Setting")
                                matchView.setBetData(
                                    it.getInt("home_score"),
                                    it.getInt("away_score")
                                )
                            }
                    newBetViews.add(matchView)
                }
                betViews[this@BetActivity.matchDay] = newBetViews

                this@BetActivity.runOnUiThread({
                    this@BetActivity.renderBetViews()
                    LogoFetcher().execute(previousBetViews)

                    // Stop Progress Spinner
                    this@BetActivity.findViewById(R.id.bets_progress).visibility = View.INVISIBLE
                })

            } catch (e: IOException) {  // If failed to fetch data, log out
                this@BetActivity.runOnUiThread({
                    this@BetActivity.showDataFetchingErrorDialogAndLogout()
                })
            }

            return null
        }
    }

    /**
     * Class that downloads the Logos of the BetViews
     */
    inner class LogoFetcher: AsyncTask<List<BetView>, Void, Void>() {

        /**
         * Downloads the BetView's logos in sequence and applies them as soon as they are available
         */
        override fun doInBackground(vararg params: List<BetView>?): Void? {

            val oldBetViews = params[0]
            for (betView in this@BetActivity.betViews[this@BetActivity.matchDay]!!) {

                oldBetViews?.
                        filter { it.matchId == betView.matchId }?.
                        map { it.getLogoBitmaps() }?.
                        forEach { betView.setLogoBitmaps(it) }

                betView.downloadLogoBitmaps()
                this@BetActivity.runOnUiThread { betView.applyLogoBitmaps() }
            }

            return null
        }
    }

    /**
     * AsyncTask that places bets for the currently entered bet values
     */
    inner class BetPlacer: AsyncTask<Void, Void, Void>() {

        /**
         * Places all eligible bets
         */
        override fun doInBackground(vararg params: Void?): Void? {

            val json = JSONArray()
            this@BetActivity.betViews[this@BetActivity.matchDay]!!
                    .mapNotNull { it.getBetJson() }
                    .forEach { json.put(it) }

            val result = placeBets(this@BetActivity.username!!, this@BetActivity.apiKey!!, json)
            if (!result) {
                this@BetActivity.runOnUiThread { this@BetActivity.showBetErrorDialog() }
            } else {
                DataGetter().execute()
            }
            return null
        }
    }
}
