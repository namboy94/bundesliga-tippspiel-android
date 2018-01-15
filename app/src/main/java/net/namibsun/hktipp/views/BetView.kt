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

package net.namibsun.hktipp.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.widget.CardView
import android.view.View

import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import net.namibsun.hktipp.R
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.json.JSONObject
import java.net.URL

/**
 * A custom view that displays a single matchup and editable fields for bets.
 * @param context: The context/activity for which to display this bet
 * @param matchData: The match data to display. A JSONObject fetched from the API
 * @param betData: The existing bet data. Can be null if not bet data exists yet
 * @param logoBitmaps: A Map of Bitmaps that contain the team logos for this map.
 *                     Can be used to reduce loading times
 */
class BetView(context: Context,
              private val matchData: JSONObject,
              private val betData: JSONObject?,
              private val logoBitmaps:
              MutableMap<String, Bitmap?> = mutableMapOf("home" to null, "away" to null))
    : CardView(context, null) {

    /**
     * The Match ID of this BetView
     */
    val matchId = matchData.getInt("id")

    /**
     * Getter method for retrieving the view's logo bitmaps
     * @return: A Map of logo bitmaps with the keys `home` and `away`
     */
    @Suppress("RedundantVisibilityModifier")
    public fun getLogoBitmaps(): MutableMap<String, Bitmap?> = this.logoBitmaps

    /**
     * Retrieves the team data from this BetView
     * @return a tuple of the home team's data and the away team's data
     */
    @Suppress("RedundantVisibilityModifier")
    public fun getTeamData(): Map<String, JSONObject> =
            mapOf(
                    "home" to this.matchData.getJSONObject("home_team"),
                    "away" to this.matchData.getJSONObject("away_team")
            )

    /**
     * Initializes the Bet View. Initializes all the text data and downloads/displays the
     * Logos of the teams
     */
    init {

        View.inflate(context, R.layout.bet, this)

        val homeTeam = this.matchData.getJSONObject("home_team")
        val awayTeam = this.matchData.getJSONObject("away_team")

        // Display Team names
        val homeTeamTitle = (this.findViewById(R.id.bet_home_team_title) as TextView)
        val awayTeamTitle = (this.findViewById(R.id.bet_away_team_title) as TextView)
        homeTeamTitle.text = homeTeam.getString("shortname")
        awayTeamTitle.text = awayTeam.getString("shortname")

        // Set existing bet data
        if (this.betData != null) {
            val homeTeamEdit = (this.findViewById(R.id.bet_home_team_edit) as EditText)
            val awayTeamEdit = (this.findViewById(R.id.bet_away_team_edit) as EditText)
            homeTeamEdit.setText(this.betData.getInt("home_score").toString())
            awayTeamEdit.setText(this.betData.getInt("away_score").toString())
        }

        // Disable editing if match has started
        if (this.matchData.getBoolean("started")) {
            (this.findViewById(R.id.bet_home_team_edit) as EditText).isEnabled = false
            (this.findViewById(R.id.bet_away_team_edit) as EditText).isEnabled = false
        }

        // Download/Display the Logos
        val homeLogoUrl = homeTeam.getString("icon")
        val awayLogoUrl = awayTeam.getString("icon")
        val homeImage = this.findViewById(R.id.bet_home_team_logo) as ImageView
        val awayImage = this.findViewById(R.id.bet_away_team_logo) as ImageView

        context.doAsync {
            if (this@BetView.logoBitmaps["home"] == null) {

                this@BetView.logoBitmaps["home"] = BitmapFactory.decodeStream(
                        URL(homeLogoUrl).openConnection().getInputStream())
                this@BetView.logoBitmaps["away"] = BitmapFactory.decodeStream(
                        URL(awayLogoUrl).openConnection().getInputStream())
            }

            this@BetView.context.runOnUiThread {
                homeImage.setImageBitmap(this@BetView.logoBitmaps["home"])
                awayImage.setImageBitmap(this@BetView.logoBitmaps["away"])
            }
        }
    }

    /**
     * Generates a JSON bet for use when placing a bet using the API
     * @return: The JSONObject representation of the bet, or null if the bet is invalid
     */
    fun getBetJson(): JSONObject? {

        return try {

            val homeTeamEdit = this.findViewById(R.id.bet_home_team_edit) as EditText
            val awayTeamEdit = this.findViewById(R.id.bet_away_team_edit) as EditText

            val homeScore = (homeTeamEdit).text.toString().toInt()
            val awayScore = (awayTeamEdit).text.toString().toInt()

            // Range Check
            if (homeScore < 0 || homeScore >= 100 || awayScore < 0 || awayScore >= 100) {
                throw NumberFormatException("Out of range")
            }

            val json = JSONObject("{}")
            json.put("home_score", homeScore)
            json.put("away_score", awayScore)
            json.put("match_id", this.matchId)
            json // return
        } catch (e: NumberFormatException) {
            null
        }
    }
}
