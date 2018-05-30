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
import android.widget.ImageView
import android.widget.TextView
import net.namibsun.hktipp.R
import net.namibsun.hktipp.data.GoalData

/**
 * A custom View that displays a goal for a match
 * @param context: The context/activity in which this view is created
 * @param goal: The goal to display
 */
@SuppressLint("ViewConstructor")
class SingleMatchGoalView(context: Context, goal: GoalData)
    : CardView(context, null) {

    /**
     * Initializes the leaderboard entry fields
     */
    init {
        View.inflate(context, R.layout.single_match_goal, this)
        this.findViewById<TextView>(R.id.single_match_goal_minute).text = "${goal.minute}"
        this.findViewById<TextView>(R.id.single_match_goal_player).text = goal.player.name
        this.findViewById<TextView>(R.id.single_match_goal_home).text = "${goal.homeScore}"
        this.findViewById<TextView>(R.id.single_match_goal_away).text = "${goal.awayScore}"

        val icon = this.findViewById<ImageView>(R.id.single_match_goal_icon)
        if (goal.ownGoal) {
            icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.goal_owngoal))
        } else if (goal.penalty) {
            icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.goal_penalty))
        }
    }
}