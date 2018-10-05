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
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import net.namibsun.hktipp.R
import net.namibsun.hktipp.models.Bet
import net.namibsun.hktipp.models.Goal
import net.namibsun.hktipp.models.Match
import net.namibsun.hktipp.singletons.Logos
import net.namibsun.hktipp.views.MatchBetView
import net.namibsun.hktipp.views.MatchGoalView
import org.jetbrains.anko.doAsync

/**
 * Activity that display information about a single match
 */
class MatchActivity : AuthorizedActivity() {

    /**
     * The match data to display
     */
    private lateinit var match: Match

    /**
     * Counts the currently updating async threads
     */
    private var updateCount = 0

    /**
     * Displays the match data
     * @param savedInstanceState: The saved instance state
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        this.setContentView(R.layout.single_match)
        super.onCreate(savedInstanceState)

        this.match = this.intent.extras.get("match") as Match

        this.findViewById<View>(R.id.single_match_update_button).setOnClickListener {
            this@MatchActivity.update()
        }

        this.loadLogos()
        this.displayCurrentScores()
        this.update()
    }

    /**
     * Starts the loading animation
     */
    override fun startLoadingAnimation() {
        this.updateCount = 2 // For goals and bets
        this.findViewById<View>(R.id.single_match_update_button).setOnClickListener {}
        this.findViewById<View>(R.id.single_match_goals_progress).visibility = View.VISIBLE
        this.findViewById<View>(R.id.single_match_bets_progress).visibility = View.VISIBLE
        this.findViewById<LinearLayout>(R.id.match_bets_list).removeAllViews()
        this.findViewById<LinearLayout>(R.id.match_goals_list).removeAllViews()
    }

    /**
     * Stops the loading animation
     */
    override fun stopLoadingAnimation() {
        this.updateCount--
        if (this.updateCount == 0) {
            this.findViewById<View>(R.id.single_match_update_button).setOnClickListener {
                this@MatchActivity.update()
            }
            this.findViewById<View>(R.id.single_match_goals_progress).visibility = View.INVISIBLE
            this.findViewById<View>(R.id.single_match_bets_progress).visibility = View.INVISIBLE
        }
    }

    /**
     * Updates the match data and displays the new information
     */
    private fun update() {

        this.startLoadingAnimation()

        this.doAsync {
            val matchQuery = Match.query(this@MatchActivity.apiConnection)
            matchQuery.addFilter("id", this@MatchActivity.match.id)
            this@MatchActivity.match = matchQuery.query()[0]
            this@MatchActivity.runOnUiThread {
                this@MatchActivity.displayCurrentScores()
                this@MatchActivity.loadGoals()
                this@MatchActivity.loadBets()
            }
        }
    }

    /**
     * Displays the current match score
     */
    private fun displayCurrentScores() {

        var homeScore = "-"
        var awayScore = "-"
        if (this.match.started) {
            homeScore = "${this.match.homeCurrentScore}"
            awayScore = "${this.match.awayCurrentScore}"
        }
        this.findViewById<TextView>(R.id.home_team_score).text = homeScore
        this.findViewById<TextView>(R.id.away_team_score).text = awayScore
    }

    /**
     * Loads the logo bitmaps into the image views reserved for them
     */
    private fun loadLogos() {
        this.doAsync {
            val homeLogo = this@MatchActivity.findViewById<ImageView>(R.id.home_team_logo)
            val awayLogo = this@MatchActivity.findViewById<ImageView>(R.id.away_team_logo)
            val homeBitmap = Logos.getLogo(this@MatchActivity.match.homeTeam)
            val awayBitmap = Logos.getLogo(this@MatchActivity.match.awayTeam)

            this@MatchActivity.runOnUiThread {
                homeLogo.setImageBitmap(homeBitmap)
                awayLogo.setImageBitmap(awayBitmap)
            }
        }
    }

    /**
     * Retrieves goals for the game using the API
     */
    private fun loadGoals() {

        val goalListView = this.findViewById<LinearLayout>(R.id.match_goals_list)

        this.doAsync {

            val goalQuery = Goal.query(this@MatchActivity.apiConnection)
            goalQuery.addFilter("match_id", this@MatchActivity.match.id)
            val goals = goalQuery.query()

            this@MatchActivity.runOnUiThread {
                this@MatchActivity.stopLoadingAnimation()
            }

            while (this@MatchActivity.updateCount > 0) {} // Wait for others to load

            this@MatchActivity.runOnUiThread {
                for (goal in goals) {
                    val goalView = MatchGoalView(this@MatchActivity, goal)
                    goalListView.addView(goalView)
                }
            }
        }
    }

    /**
     * Retrieves the bets for the game by other users using the API
     */
    private fun loadBets() {

        val betListView = this.findViewById<LinearLayout>(R.id.match_bets_list)

        this.doAsync {

            val betsQuery = Bet.query(this@MatchActivity.apiConnection)
            betsQuery.addFilter("match_id", this@MatchActivity.match.id)
            val bets = betsQuery.query()

            this@MatchActivity.runOnUiThread {
                this@MatchActivity.stopLoadingAnimation()
            }

            while (this@MatchActivity.updateCount > 0) {} // Wait for others to load

            this@MatchActivity.runOnUiThread {
                for (bet in bets) {
                    val betView = MatchBetView(this@MatchActivity, bet)
                    betListView.addView(betView)
                }
            }
        }
    }
}
