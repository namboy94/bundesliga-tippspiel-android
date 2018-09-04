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

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import net.namibsun.hktipp.data.BetData
import net.namibsun.hktipp.data.MatchData
import net.namibsun.hktipp.helper.getMatches
import net.namibsun.hktipp.helper.switchActivity
import net.namibsun.hktipp.helper.getBets
import net.namibsun.hktipp.helper.showErrorDialog
import net.namibsun.hktipp.helper.logout
import net.namibsun.hktipp.helper.placeBets
import net.namibsun.hktipp.views.BetView
import org.jetbrains.anko.doAsync
import org.json.JSONArray
import java.io.IOException
import java.io.Serializable

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

        this.username = this.intent.extras.getString("username")
        this.apiKey = this.intent.extras.getString("api_key")

        this.findViewById<View>(R.id.bets_submit_button).setOnClickListener { this.placeBets() }

        // Set button actions to go to next or previous matchday
        this.findViewById<View>(R.id.bets_prev_button).setOnClickListener {
            this.adjustMatchday(false)
        }
        this.findViewById<View>(R.id.bets_next_button).setOnClickListener {
            this.adjustMatchday(true)
        }

        // Set listener for Leaderboard Activity button
        this.findViewById<View>(R.id.leaderboard_button).setOnClickListener {
            net.namibsun.hktipp.helper.switchActivity(
                    this, LeaderboardActivity::class.java, this.username, this.apiKey)
        }

        // Get Data for current matchday
        this.updateData()
    }

    /**
     * Switches the match day when either the `next` or `previous` buttons are pressed
     * @param incrementing: Specifies if the matchday is getting incremented or decremented
     */
    private fun adjustMatchday(incrementing: Boolean) {

        this.setUiElementEnabledState(false)
        Log.i("BetActivity", "Switching matchday")

        if ((this.matchDay in 1..33 && incrementing) || (this.matchDay in 2..34 && !incrementing)) {

            if (incrementing) {
                this.matchDay++
            } else {
                this.matchDay--
            }
            this.findViewById<Button>(R.id.bets_prev_button).isEnabled = this.matchDay != 1
            this.findViewById<Button>(R.id.bets_next_button).isEnabled = this.matchDay != 34
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

        Log.i("BetActivity", "Updating Data")

        val apiKey = this.apiKey!!
        val matchday = this.matchDay

        // Clear previous Views and start Progress Spinner
        this.runOnUiThread {
            Log.d("BetActivity", "Clearing old views")
            if (this.matchDay != -1) { // We don't have to clear if no data was fetched before
                this.betViews[this.matchDay] = mutableListOf()
                this.renderBetViews()
            }
            this.findViewById<View>(R.id.bets_progress).visibility = View.VISIBLE
        }

        this.doAsync {

            try {

                val matches = getMatches(apiKey, matchday)
                val bets = getBets(apiKey, matchday)
                Log.i("BetActivity", "Data successfully fetched")

                // Update Matchday (Only has an effect if matchday == -1). Also reset BetViews
                this@BetActivity.matchDay = matches[0].matchDay
                this@BetActivity.betViews[this@BetActivity.matchDay] = mutableListOf()

                this@BetActivity.runOnUiThread {
                    this@BetActivity.initializeBetViews(matches, bets)
                    this@BetActivity.renderBetViews()
                    // Stop Progress Spinner and re-enable UI elements
                    this@BetActivity.findViewById<View>(R.id.bets_progress).visibility =
                            View.INVISIBLE
                    this@BetActivity.setUiElementEnabledState(true)
                }
            } catch (e: IOException) { // If failed to fetch data, log out
                this@BetActivity.runOnUiThread {
                    showErrorDialog(this@BetActivity,
                            R.string.bets_fetching_error_title,
                            R.string.bets_fetching_error_body, false)
                    logout(this@BetActivity)
                }
            }
        }
    }

    /**
     * Initializes the bet views generated by the retrieved data in [updateData].
     * @param matches: The match data retrieved from the API
     * @param bets: The bets data retrieved from the API
     */
    private fun initializeBetViews(matches: List<MatchData>, bets: List<BetData>) {

        Log.i("BetActivity", "Initializing bet views")

        // Initialize the BetViews
        for (match in matches) {

            var betData: BetData? = null

            // Check for existing bet data
            for (bet in bets) {
                if (bet.match.id == match.id) {
                    betData = bet
                    Log.d("BetActivity", "Bet Data Found for match ${match.id}")
                }
            }

            // Add Bet Views
            val betView = BetView(this@BetActivity, match, betData)
            betView.setOnClickListener {
                val bundle = Bundle()
                bundle.putSerializable("match_data", match as Serializable)
                switchActivity(this, SingleMatchActivity::class.java,
                        this.username, this.apiKey, bundle)
            }

            this.betViews[this.matchDay]!!.add(betView)
        }
        this.betViews[-1] = this.betViews[this.matchDay]!! // Store betViews for logo purposes
    }

    /**
     * Removes all current bets from the activity, then adds all of the BetViews
     * found in the [betViews] variable for the current matchday.
     * Also changes the matchday title to the current matchday
     */
    private fun renderBetViews() {

        Log.d("BetActivity", "Rendering Views")

        val title = this.findViewById<TextView>(R.id.bets_title)
        val list = this.findViewById<LinearLayout>(R.id.bets_list)
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

        Log.i("BetActivity", "Placing Bets")

        this.setUiElementEnabledState(false)
        val json = JSONArray()

        // Turns the Bet Views into JSON objects that will be POSTED to the API
        this.betViews[this.matchDay]!!
                .mapNotNull { it.getBetJson() }
                .forEach { json.put(it) }

        this.betViews[this.matchDay] = mutableListOf()
        this.renderBetViews()
        this.findViewById<View>(R.id.bets_progress).visibility = View.VISIBLE

        this.doAsync {
            val result = placeBets(this@BetActivity.apiKey!!, json)
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

    /**
     * Enables or disables all user-editable UI elements
     * @param state: Sets the enabled state of the elements
     */
    private fun setUiElementEnabledState(state: Boolean) {
        this.findViewById<Button>(R.id.bets_submit_button).isEnabled = state
        if (state) {
            this.findViewById<Button>(R.id.bets_prev_button).setOnClickListener {
                this.adjustMatchday(false) }
            this.findViewById<Button>(R.id.bets_next_button).setOnClickListener {
                this.adjustMatchday(true) }
        } else {
            this.findViewById<Button>(R.id.bets_next_button).setOnClickListener { }
            this.findViewById<Button>(R.id.bets_prev_button).setOnClickListener { }
        }
    }
}
