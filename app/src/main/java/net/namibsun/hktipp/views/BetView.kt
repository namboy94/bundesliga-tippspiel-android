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
import android.content.Context
import android.support.v7.widget.CardView
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import net.namibsun.hktipp.R
import net.namibsun.hktipp.data.BetData
import net.namibsun.hktipp.data.MatchData
import net.namibsun.hktipp.data.TeamData
import net.namibsun.hktipp.singletons.Logos
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.json.JSONObject

@SuppressLint("ViewConstructor")
/**
 * A custom view that displays a single matchup and editable fields for bets.
 * @param context: The context/activity for which to display this bet
 * @param matchData: The match data to display. A JSONObject fetched from the API
 * @param betData: The existing bet data. Can be null if not bet data exists yet
 */
class BetView(context: Context, private val matchData: MatchData, private val betData: BetData?)
    : CardView(context, null) {

    /**
     * Retrieves the team data from this BetView
     * @return a tuple of the home team's data and the away team's data
     */
    fun getTeamData(): Map<String, TeamData> =
            mapOf(
                    "home" to this.matchData.homeTeam,
                    "away" to this.matchData.awayTeam
            )

    /**
     * Initializes the Bet View. Initializes all the text data and downloads/displays the
     * Logos of the teams
     */
    init {

        View.inflate(context, R.layout.bet, this)

        // Display Team names
        val homeTeamTitle = this.findViewById<TextView>(R.id.bet_home_team_title)
        val awayTeamTitle = this.findViewById<TextView>(R.id.bet_away_team_title)
        homeTeamTitle.text = this.matchData.homeTeam.shortName
        awayTeamTitle.text = this.matchData.awayTeam.shortName

        // Set existing bet data
        if (this.betData != null) {
            val homeTeamEdit = this.findViewById<EditText>(R.id.bet_home_team_edit)
            val awayTeamEdit = this.findViewById<EditText>(R.id.bet_away_team_edit)
            homeTeamEdit.setText("${this.betData.homeScore}")
            awayTeamEdit.setText("${this.betData.awayScore}")
        }

        // Disable editing if match has started
        if (this.matchData.started) {
            this.findViewById<EditText>(R.id.bet_home_team_edit).isEnabled = false
            this.findViewById<EditText>(R.id.bet_away_team_edit).isEnabled = false
        }

        // Download/Display the Logos
        val homeImage = this.findViewById<ImageView>(R.id.bet_home_team_logo)
        val awayImage = this.findViewById<ImageView>(R.id.bet_away_team_logo)

        context.doAsync {
            val homeTeamLogoBitmap = Logos.getLogo(this@BetView.matchData.homeTeam)
            val awayTeamLogoBitmap = Logos.getLogo(this@BetView.matchData.awayTeam)

            this@BetView.context.runOnUiThread {
                homeImage.setImageBitmap(homeTeamLogoBitmap)
                awayImage.setImageBitmap(awayTeamLogoBitmap)
            }
        }
    }

    /**
     * Generates a JSON bet for use when placing a bet using the API
     * @return: The JSONObject representation of the bet, or null if the bet is invalid
     */
    fun getBetJson(): JSONObject? {

        return try {

            val homeTeamEdit = this.findViewById<EditText>(R.id.bet_home_team_edit)
            val awayTeamEdit = this.findViewById<EditText>(R.id.bet_away_team_edit)

            val homeScore = (homeTeamEdit).text.toString().toInt()
            val awayScore = (awayTeamEdit).text.toString().toInt()

            // Range Check
            if (homeScore < 0 || homeScore >= 100 || awayScore < 0 || awayScore >= 100) {
                throw NumberFormatException("Out of range")
            }

            val json = JSONObject("{}")
            json.put("home_score", homeScore)
            json.put("away_score", awayScore)
            json.put("match_id", this.matchData.id)
            json // return
        } catch (e: NumberFormatException) {
            null
        }
    }
}
