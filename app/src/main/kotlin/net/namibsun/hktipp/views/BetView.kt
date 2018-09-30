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

package net.namibsun.hktipp.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.widget.CardView
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import net.namibsun.hktipp.R
import net.namibsun.hktipp.activities.BaseActivity
import net.namibsun.hktipp.activities.MatchActivity
import net.namibsun.hktipp.singletons.Logos
import net.namibsun.hktipp.models.Match
import net.namibsun.hktipp.models.Bet
import net.namibsun.hktipp.models.MinimalBet
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import java.io.Serializable

@SuppressLint("ViewConstructor")
/**
 * A custom view that displays a single matchup and editable fields for bets.
 * @param context: The context/activity for which to display this bet
 * @param match: The match data to display. A JSONObject fetched from the API
 * @param bet: The existing bet data. Can be null if not bet data exists yet
 */
class BetView(context: BaseActivity, private val match: Match, private val bet: Bet?)
    : CardView(context, null) {

    /**
     * Initializes the Bet View. Initializes all the text data and downloads/displays the
     * Logos of the teams
     */
    init {

        View.inflate(context, R.layout.bet, this)

        // Display Team names
        val homeTeamTitle = this.findViewById<TextView>(R.id.bet_home_team_title)
        val awayTeamTitle = this.findViewById<TextView>(R.id.bet_away_team_title)
        homeTeamTitle.text = this.match.homeTeam.shortName
        awayTeamTitle.text = this.match.awayTeam.shortName

        // Set existing bet data
        if (this.bet != null) {
            val homeTeamEdit = this.findViewById<EditText>(R.id.bet_home_team_edit)
            val awayTeamEdit = this.findViewById<EditText>(R.id.bet_away_team_edit)
            homeTeamEdit.setText("${this.bet.homeScore}")
            awayTeamEdit.setText("${this.bet.awayScore}")
        }

        // Disable editing if match has started
        this.findViewById<EditText>(R.id.bet_home_team_edit).isEnabled = !this.match.started
        this.findViewById<EditText>(R.id.bet_away_team_edit).isEnabled = !this.match.started

        // Download/Display the Logos
        val homeImage = this.findViewById<ImageView>(R.id.bet_home_team_logo)
        val awayImage = this.findViewById<ImageView>(R.id.bet_away_team_logo)
        context.doAsync {
            val homeTeamLogoBitmap = Logos.getLogo(this@BetView.match.homeTeam)
            val awayTeamLogoBitmap = Logos.getLogo(this@BetView.match.awayTeam)

            this@BetView.context.runOnUiThread {
                homeImage.setImageBitmap(homeTeamLogoBitmap)
                awayImage.setImageBitmap(awayTeamLogoBitmap)
            }
        }

        // Make view clickable
        this.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("match", match as Serializable)
            context.startActivity(MatchActivity::class.java, false, bundle)
        }
    }

    /**
     * Generates a MinimalBet object for use for placing bets
     * @return The minimal bet object
     */
    fun getMinimalBet(): MinimalBet? {
        return try {

            val homeTeamEdit = this.findViewById<EditText>(R.id.bet_home_team_edit)
            val awayTeamEdit = this.findViewById<EditText>(R.id.bet_away_team_edit)

            val homeScore = (homeTeamEdit).text.toString().toInt()
            val awayScore = (awayTeamEdit).text.toString().toInt()

            // Range Check
            if (homeScore < 0 || homeScore >= 100 || awayScore < 0 || awayScore >= 100) {
                throw NumberFormatException("Out of range")
            }

            MinimalBet(this.match.id, homeScore, awayScore)
        } catch (e: NumberFormatException) {
            null
        }
    }
}
