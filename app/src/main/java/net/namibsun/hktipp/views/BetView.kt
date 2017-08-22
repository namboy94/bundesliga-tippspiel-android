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

package net.namibsun.hktipp.views

import android.content.Context
import android.graphics.Bitmap
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import net.namibsun.hktipp.R
import org.json.JSONObject


/**
 * A custom view that displays a single matchup and editable fields for bets
 */
class BetView: CardView {

    /**
     * The Match ID for the bet's match
     */
    private var matchId: Int? = null

    /**
     * Constructors of the View that inflate the bet.xml layout
     * Once the view is created, to really make use of the functionality of the BetView,
     * [setMatchData] and [setBetData] should be called afterwards
     * @param context: The Context/Activity in which the view will be displayed
     */
    constructor(context: Context): this(context, null)

    /**
     * Constructors of the View that inflate the bet.xml layout
     * Once the view is created, to really make use of the functionality of the BetView,
     * [setMatchData] and [setBetData] should be called afterwards
     * @param context: The Context/Activity in which the view will be displayed
     * @param attrs: The XML Attribute Set provided for the View
     */
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs) {
        View.inflate(this.context, R.layout.bet, this)
    }

    /**
     * Sets the Match Data. Will populate all match-related elements of the view
     * @param id: The Match ID
     * @param homeTeam: The Name of the Home Team which will be displayed
     * @param awayTeam: The Name of the Away Team which will be displayed
     * @param homeLogo: A Bitmap of the Home Team Logo
     * @param awayLogo: A Bitmap of the Home Team Logo
     * @param hasStarted: Indicates if the match hs already started or not
     */
    fun setMatchData(id: Int, homeTeam: String, awayTeam: String,
                     homeLogo: Bitmap, awayLogo: Bitmap, hasStarted: Boolean) {
        (this.findViewById(R.id.bet_home_team_title) as TextView).text = homeTeam
        (this.findViewById(R.id.bet_away_team_title) as TextView).text = awayTeam
        (this.findViewById(R.id.bet_home_team_logo) as ImageView).setImageBitmap(homeLogo)
        (this.findViewById(R.id.bet_away_team_logo) as ImageView).setImageBitmap(awayLogo)
        this.matchId = id

        // Disable editing if match has started
        if (hasStarted) {
            (this.findViewById(R.id.bet_home_team_edit) as EditText).isEnabled = false
            (this.findViewById(R.id.bet_away_team_edit) as EditText).isEnabled = false
        }
    }

    /**
     * Sets the current values of the user's bet data
     * @param homeTeamScore: The score of the home team
     * @param awayTeamScore: The score of the away team
     */
    fun setBetData(homeTeamScore: Int, awayTeamScore: Int) {
        (this.findViewById(R.id.bet_home_team_edit) as EditText).setText(homeTeamScore.toString())
        (this.findViewById(R.id.bet_away_team_edit) as EditText).setText(awayTeamScore.toString())
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
            json  // return

        } catch (e: NumberFormatException) {
            null
        }
    }

}
