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

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import net.namibsun.hktipp.R
import net.namibsun.hktipp.models.Bet
import net.namibsun.hktipp.models.Match
import net.namibsun.hktipp.models.MinimalBet
import net.namibsun.hktipp.views.BetView
import org.jetbrains.anko.doAsync
import java.io.IOException

/**
 * This activity allows a user to place bets, as well as view already placed bets
 */
class BetActivity : AuthorizedActivity() {

    /**
     * The Bet Views for the currently selected match day
     */
    private val betViews = mutableMapOf<Int, MutableList<BetView>>()

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

        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.bets)

        this.findViewById<View>(R.id.bets_submit_button).setOnClickListener { this.placeBets() }
        this.findViewById<View>(R.id.bets_prev_button).setOnClickListener {
            this.adjustMatchday(false)
        }
        this.findViewById<View>(R.id.bets_next_button).setOnClickListener {
            this.adjustMatchday(true)
        }

        val menuButton = this.findViewById<View>(R.id.menu_button)
        menuButton.setOnClickListener {
            val popup = PopupMenu(this@BetActivity, menuButton)
            popup.menuInflater.inflate(R.menu.main_menu, popup.menu)
            popup.setOnMenuItemClickListener {
                if (it.itemId == R.id.leaderboard_menu_option) {
                    this@BetActivity.startActivity(LeaderboardActivity::class.java, false)
                } else if (it.itemId == R.id.logout_menu_option) {
                    Log.i("BetActivity", "Logging out.")
                    this.logout()
                }
                true
            }
            popup.show()
        }
        // onResume will be called, so we don't need to call updateData here
    }

    /**
     * Starts the loading animation
     */
    override fun startLoadingAnimation() {

        this.findViewById<Button>(R.id.bets_submit_button).isEnabled = false
        this.findViewById<View>(R.id.bets_progress).visibility = View.VISIBLE

        this.findViewById<Button>(R.id.bets_next_button).setOnClickListener { }
        this.findViewById<Button>(R.id.bets_prev_button).setOnClickListener { }

        Log.d("BetActivity", "Clearing old views")
        this.betViews[this.matchDay] = mutableListOf()
        this.renderBetViews()
    }

    /**
     * Stops the loading animation
     */
    override fun stopLoadingAnimation() {

        this.findViewById<Button>(R.id.bets_submit_button).isEnabled = true
        this.findViewById<View>(R.id.bets_progress).visibility = View.VISIBLE

        this.findViewById<Button>(R.id.bets_prev_button).setOnClickListener {
            this.adjustMatchday(false)
        }
        this.findViewById<Button>(R.id.bets_next_button).setOnClickListener {
            this.adjustMatchday(true)
        }
    }

    /**
     * Switches the match day when either the `next` or `previous` buttons are pressed
     * @param incrementing: Specifies if the matchday is getting incremented or decremented
     */
    private fun adjustMatchday(incrementing: Boolean) {

        Log.i("BetActivity", "Switching matchday")

        // Only switch matchday if in valid range
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

        this.startLoadingAnimation()

        this.doAsync {

            try {

                val matchQuery = Match.query(this@BetActivity.apiConnection)
                matchQuery.addFilter("matchday", this@BetActivity.matchDay)
                val matches = matchQuery.query()

                val betQuery = Bet.query(this@BetActivity.apiConnection)
                betQuery.addFilter("matchday", this@BetActivity.matchDay)
                betQuery.addFilter("user_id", this@BetActivity.apiConnection.user.id)
                val bets = betQuery.query()

                Log.i("BetActivity", "Data successfully fetched")

                this@BetActivity.matchDay = matches[0].matchday
                this@BetActivity.betViews[this@BetActivity.matchDay] = mutableListOf()

                this@BetActivity.runOnUiThread {
                    this@BetActivity.initializeBetViews(matches, bets)
                    this@BetActivity.renderBetViews()
                    this@BetActivity.stopLoadingAnimation()
                }
            } catch (e: IOException) { // If failed to fetch data, log out
                this@BetActivity.runOnUiThread {

                    this@BetActivity.showErrorDialog(
                            R.string.bets_fetching_error_title,
                            R.string.bets_fetching_error_body
                    )
                    this@BetActivity.logout()
                }
            }
        }
    }

    /**
     * Initializes the bet views generated by the retrieved data in [updateData].
     * @param matches: The match data retrieved from the API
     * @param bets: The bets data retrieved from the API
     */
    private fun initializeBetViews(matches: List<Match>, bets: List<Bet>) {

        Log.i("BetActivity", "Initializing bet views")

        for (match in matches) {

            var betData: Bet? = null

            // Check for existing bet data
            for (bet in bets) {
                if (bet.match.id == match.id) {
                    betData = bet
                    Log.d("BetActivity", "Bet Data Found for match ${match.id}")
                }
            }

            val betView = BetView(this@BetActivity, match, betData)
            this.betViews[this.matchDay]!!.add(betView)
        }
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

        if (this.matchDay != -1) {
            title.text = this.resources.getString(R.string.bets_matchday_title, this.matchDay)
        }

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

        val minimalBets = mutableListOf<MinimalBet>()
        for (betView in this.betViews[this.matchDay]!!) {
            val minimalBet = betView.getMinimalBet()
            if (minimalBet != null) {
                minimalBets.add(minimalBet)
            }
            Log.d("BetActivity", "Placing bet $minimalBet")
        }

        this.startLoadingAnimation()

        this.doAsync {
            Bet.place(this@BetActivity.apiConnection, minimalBets)
            this@BetActivity.runOnUiThread {
                this@BetActivity.updateData()
            }
        }
    }
}
