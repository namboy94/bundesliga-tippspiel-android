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