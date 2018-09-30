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
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.view.View
import android.widget.TextView
import net.namibsun.hktipp.R
import net.namibsun.hktipp.models.Bet

/**
 * A custom View that displays a bet for a user on a match
 * @param context: The context/activity in which this view is created
 * @param bet: The bet to display
 */
@SuppressLint("ViewConstructor")
class MatchBetView(context: Context, bet: Bet)
    : CardView(context, null) {

    /**
     * Initializes the bet view
     */
    init {
        View.inflate(context, R.layout.single_match_bet, this)
        this.findViewById<TextView>(R.id.single_match_bet_username).text = bet.user.username

        this.findViewById<TextView>(R.id.single_match_bet_home).text = "${bet.homeScore}"
        this.findViewById<TextView>(R.id.single_match_bet_away).text = "${bet.awayScore}"

        val pointsView = this.findViewById<TextView>(R.id.single_match_bet_points)
        pointsView.text = "${bet.points}"
        ContextCompat.getDrawable(context, R.drawable.goal_owngoal)

        val background = when (bet.points) {
            0 -> R.drawable.bet_points_0
            3 -> R.drawable.bet_points_1
            7 -> R.drawable.bet_points_2
            10 -> R.drawable.bet_points_3
            12 -> R.drawable.bet_points_4
            15 -> R.drawable.bet_points_5
            else -> R.drawable.bet_points_before
        }

        pointsView.background = ContextCompat.getDrawable(context, background)
    }
}