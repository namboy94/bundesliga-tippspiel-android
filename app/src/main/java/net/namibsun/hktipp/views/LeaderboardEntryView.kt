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
import android.widget.TextView
import net.namibsun.hktipp.R

/**
 * A custom View that displays a user's name and points for use in the
 * leaderboard.
 * @param context: The context/activity in which this view is created
 * @param position: The position of the user in the leaderboard
 * @param username: The username to display
 * @param points: The points to display
 */
@SuppressLint("ViewConstructor")
class LeaderboardEntryView(context: Context, position: String, username: String, points: String)
    : CardView(context, null) {

    /**
     * Initializes the leaderboard entry fields
     */
    init {
        View.inflate(context, R.layout.leaderboard_entry, this)
        this.findViewById<TextView>(R.id.leaderboard_entry_position).text = position
        this.findViewById<TextView>(R.id.leaderboard_entry_username).text = username
        this.findViewById<TextView>(R.id.leaderboard_entry_points).text = points
    }
}