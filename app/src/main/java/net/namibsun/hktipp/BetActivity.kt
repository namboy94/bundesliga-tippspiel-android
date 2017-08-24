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

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import net.namibsun.hktipp.helper.getMatches
import net.namibsun.hktipp.helper.getBets
import net.namibsun.hktipp.helper.showErrorDialog
import net.namibsun.hktipp.helper.logout
import net.namibsun.hktipp.helper.placeBets
import net.namibsun.hktipp.views.BetView
import org.jetbrains.anko.doAsync
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

/**
 * This activity allows a user to place bets, as well as view already placed bets
 */
class BetActivity : AppCompatActivity() {

    /**
     * The Bet Views for the currently selected match day
     */
    private val betViews = mutableMapOf<Int, MutableList<BetView>>()

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

        this.username = intent.extras.getString("username")
        this.apiKey = intent.extras.getString("api_key")

        this.findViewById(R.id.bets_submit_button).setOnClickListener { this.placeBets() }

        // Set button actions to go to next or previous matchday
        this.findViewById(R.id.bets_prev_button).setOnClickListener { this.adjustMatchday(false) }
        this.findViewById(R.id.bets_next_button).setOnClickListener { this.adjustMatchday(true) }

        // Get Data for current matchday
        this.updateData()

    }

    /**
     * Switches the match day when either the `next` or `previous` buttons are pressed
     * @param incrementing: Specifies if the matchday is getting incremented or decremented
     */
    private fun adjustMatchday(incrementing: Boolean) {

        if ((this.matchDay in 1..33 && incrementing) || (this.matchDay in 2..34 && !incrementing)) {

            if (incrementing) {
                this.matchDay++
            } else {
                this.matchDay--
            }
            this.findViewById(R.id.bets_previous_button).isEnabled = this.matchDay != 1
            this.findViewById(R.id.bets_next_button).isEnabled = this.matchDay != 34
            this.updateData()
        }
    }

    /**
     * Updates the data fetched from the API. First, the previous bets are saved, provided they
     * exist at all. Then, the previous data is cleared and a loading animation is shown. The data
     * is fetched asynchronously, then displayed. If fetching the data failed, the user will be
     * logged out after being displayed an error message
     */
    private fun updateData() {

        val username = this.username!!
        val apiKey = this.apiKey!!
        val matchday = this.matchDay
        val previousBetViews = this.betViews[this.matchDay] // Remember previous bets

        // Clear previous Views and start Progress Spinner
        this.runOnUiThread {
            if (this.matchDay != -1) { // We don't have to clear if no data was fetched before
                this.betViews[this.matchDay] = mutableListOf()
                this.renderBetViews()
            }
            this.findViewById(R.id.bets_progress).visibility = View.VISIBLE
        }

        this.doAsync {

            try {

                val matches = getMatches(username, apiKey, matchday)
                val bets = getBets(username, apiKey, matchday)

                // Update Matchday (Only has an effect if matchday == -1)
                this@BetActivity.matchDay = matches.getJSONObject(0).getInt("matchday")

                this@BetActivity.runOnUiThread({
                    this@BetActivity.initializeBetViews(matches, bets, previousBetViews)
                    this@BetActivity.renderBetViews()
                    // Stop Progress Spinner
                    this@BetActivity.findViewById(R.id.bets_progress).visibility = View.INVISIBLE
                })

            } catch (e: IOException) { // If failed to fetch data, log out
                this@BetActivity.runOnUiThread({
                    showErrorDialog(this@BetActivity,
                            R.string.bets_fetching_error_title,
                            R.string.bets_fetching_error_body, false)
                    logout(this@BetActivity)
                })
            }
        }
    }

    /**
     * Initializes the bet views generated by the retrieved data in [updateData].
     * @param matches: The match data retrieved from the API
     * @param bets: The bets data retrieved from the API
     * @param previousBets: The previous bets on the current matchday, before they were wiped
     */
    private fun initializeBetViews(
            matches: JSONArray, bets: JSONArray, previousBets: List<BetView>?) {

        // Find some old bet data to extract logos
        var oldBetViews = listOf<BetView>()
        for (previous in listOf(
                previousBets, // Current Matchday
                this.betViews[this.matchDay - 1], // Previous Matchday
                this.betViews[this.matchDay + 1] // Next Matchday
        )) {
            if (previous != null && !previous.isEmpty()) {
                oldBetViews = previous
                break
            }
        }

        // Initialize the BetViews
        for (i in 0..(matches.length() - 1)) {

            val matchData = matches.getJSONObject(i)
            var betData: JSONObject? = null
            var logos = mutableMapOf<String, Bitmap?>("home" to null, "away" to null)

            // Check for existing bet data
            @Suppress("LoopToCallChain")
            for (j in 0..(bets.length() - 1)) {
                val bet = bets.getJSONObject(j)
                if (bet.getJSONObject("match").getInt("id") == bet.getInt("id")) {
                    betData = bet
                }
            }

            // Check for existing logos
            @Suppress("LoopToCallChain")
            for (oldBetView in oldBetViews) {
                if (matchData.getInt("id") == oldBetView.matchId) {
                    logos = oldBetView.getLogoBitmaps()
                }
            }

            // Add Bet Views
            val betView = BetView(this@BetActivity, matchData, betData, logos)
            this.betViews[this.matchDay]!!.add(betView)

        }
    }

    /**
     * Removes all current bets from the activity, then adds all of the BetViews
     * found in the [betViews] variable for the current matchday.
     * Also changes the matchday title to the current matchday
     */
    private fun renderBetViews() {

        val title = this.findViewById(R.id.bets_title) as TextView
        val list = this.findViewById(R.id.bets_list) as LinearLayout
        val bets = this.betViews[this.matchDay]

        title.text = this.resources.getString(R.string.bets_matchday_title, this.matchDay)

        if (bets != null) {
            list.removeAllViews()
            for (view in bets) {
                list.addView(view)
            }
        }
    }

    /**
     * Places the currently entered bets
     */
    private fun placeBets() {

        val json = JSONArray()

        // Turns the Bet Views into JSON objects that will be POSTED to the API
        this.betViews[this.matchDay]!!
                .mapNotNull { it.getBetJson() }
                .forEach { json.put(it) }

        this.doAsync {
            val result = placeBets(this@BetActivity.username!!, this@BetActivity.apiKey!!, json)
            this@BetActivity.runOnUiThread {
                if (!result) {
                    showErrorDialog(this@BetActivity,
                            R.string.bets_betting_error_title, R.string.bets_betting_error_body)
                } else {
                    this@BetActivity.updateData()
                }
            }
        }
    }
}
