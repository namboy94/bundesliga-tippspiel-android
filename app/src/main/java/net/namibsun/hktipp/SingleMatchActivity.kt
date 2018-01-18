/*
Copyright 2017-2018 Hermann Krumrey<hermann@krumreyh.com>

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
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import net.namibsun.hktipp.data.MatchData
import net.namibsun.hktipp.helper.getBetsForMatch
import net.namibsun.hktipp.helper.getGoalsForMatch
import net.namibsun.hktipp.singletons.Logos
import net.namibsun.hktipp.views.SingleMatchBetView
import net.namibsun.hktipp.views.SingleMatchGoalView
import org.jetbrains.anko.doAsync

/**
 * Activity that display information about a single match
 */
class SingleMatchActivity : AppCompatActivity() {

    /**
     * The match data to display
     */
    private var matchData: MatchData? = null

    /**
     * Variable that indicates if the activity is currently active or not
     */
    private var active = false

    /**
     * The username to use with authentication actions
     */
    private var username: String? = null

    /**
     * The API key to use with authentication actions
     */
    private var apiKey: String? = null

    /**
     * Displays the match data
     * @param savedInstanceState: The saved instance state
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        this.setContentView(R.layout.single_match)
        super.onCreate(savedInstanceState)

        this.username = this.intent.extras.getString("username")
        this.apiKey = this.intent.extras.getString("api_key")
        this.matchData = this.intent.extras.get("match_data") as MatchData

        this.update()

        this.findViewById(R.id.single_match_update_button).setOnClickListener {
            this@SingleMatchActivity.update(true)
        }

        // this.startUpdateChecker()
    }

    /**
     * Updates all of the UI elements
     * @param scoreUpdate: Forces an update of the score widgets with new data if set to true
     */
    private fun update(scoreUpdate: Boolean = false) {
        this@SingleMatchActivity.loadLogos()
        this@SingleMatchActivity.loadGoals()
        this@SingleMatchActivity.loadBets()
        this@SingleMatchActivity.displayCurrentScores(scoreUpdate)
    }

    /**
     * Displays the current match score
     * @param reload: Reloads new data from the API instead of using stored values
     */
    private fun displayCurrentScores(reload: Boolean = false) {

        if (reload) {
            // TODO fetch stuff
        }

        if (this.matchData!!.started) {
            val homeScore = "${this.matchData!!.homeFtScore}"
            val awayScore = "${this.matchData!!.awayFtScore}"
            (this.findViewById(R.id.home_team_score) as TextView).text = homeScore
            (this.findViewById(R.id.away_team_score) as TextView).text = awayScore
        } else {
            (this.findViewById(R.id.home_team_score) as TextView).text = "-"
            (this.findViewById(R.id.away_team_score) as TextView).text = "-"
        }
    }

    /**
     * Loads the logo bitmaps into the image views reserved for them
     */
    private fun loadLogos() {
        this.doAsync {
            val homeLogo = this@SingleMatchActivity.findViewById(R.id.home_team_logo) as ImageView
            val awayLogo = this@SingleMatchActivity.findViewById(R.id.away_team_logo) as ImageView
            val homeBitmap = Logos.getLogo(this@SingleMatchActivity.matchData!!.homeTeam)
            val awayBitmap = Logos.getLogo(this@SingleMatchActivity.matchData!!.awayTeam)

            this@SingleMatchActivity.runOnUiThread {
                homeLogo.setImageBitmap(homeBitmap)
                awayLogo.setImageBitmap(awayBitmap)
            }
        }
    }

    /**
     * Retrieves goals for the game using the API
     */
    private fun loadGoals() {
        val goalList = this.findViewById(R.id.match_goals_list) as LinearLayout
        goalList.removeAllViews()
        this.findViewById(R.id.single_match_goals_progress).visibility = View.VISIBLE
        this.doAsync {
            val goals = getGoalsForMatch(
                    this@SingleMatchActivity.username!!,
                    this@SingleMatchActivity.apiKey!!,
                    this@SingleMatchActivity.matchData!!.id
            )
            this@SingleMatchActivity.runOnUiThread {
                this@SingleMatchActivity.findViewById(R.id.single_match_goals_progress).visibility =
                        View.INVISIBLE
                goals.map {
                    SingleMatchGoalView(this@SingleMatchActivity, it)
                }.forEach { goalList.addView(it) }
            }
        }
    }

    /**
     * Retrieves the bets for the game by other users using the API
     */
    private fun loadBets() {
        val betList = this.findViewById(R.id.match_bets_list) as LinearLayout
        betList.removeAllViews()
        this.findViewById(R.id.single_match_bets_progress).visibility = View.VISIBLE
        this.doAsync {
            val bets = getBetsForMatch(
                    this@SingleMatchActivity.username!!,
                    this@SingleMatchActivity.apiKey!!,
                    this@SingleMatchActivity.matchData!!.id
            )
            this@SingleMatchActivity.runOnUiThread {
                this@SingleMatchActivity.findViewById(R.id.single_match_bets_progress).visibility =
                        View.INVISIBLE
                bets.keys.map {
                    SingleMatchBetView(this@SingleMatchActivity, it, bets[it])
                }.forEach { betList.addView(it) }
            }
        }
    }

    /**
     * Starts an Async Task that periodically checks the current score while this
     * activity is active
     */
    private fun startUpdateChecker() {
        this.active = true
        this.doAsync {
            while (this@SingleMatchActivity.active) {
                Thread.sleep(60000)
                this@SingleMatchActivity.update(true)
            }
        }
    }
}
