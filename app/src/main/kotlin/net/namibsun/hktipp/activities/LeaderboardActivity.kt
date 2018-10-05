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
import android.widget.LinearLayout
import android.widget.ProgressBar
import net.namibsun.hktipp.R
import org.jetbrains.anko.doAsync
import net.namibsun.hktipp.views.LeaderboardEntryView
import org.json.JSONArray

/**
 * Activity that displays the current leadeboard of the hk-tippspiel website
 */
class LeaderboardActivity : AuthorizedActivity() {

    /**
     * Initializes the Activity. Populates the leaderboard.
     * @param savedInstanceState: The Instance Information of the app.
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.leaderboard)
        this.loadLeaderboard()
    }

    /**
     * Starts the loading animation
     */
    override fun startLoadingAnimation() {
        this.findViewById<ProgressBar>(R.id.leaderboard_progress).visibility = View.VISIBLE
    }

    /**
     * Stops the loading animation
     */
    override fun stopLoadingAnimation() {
        this.findViewById<ProgressBar>(R.id.leaderboard_progress).visibility = View.INVISIBLE
    }

    /**
     * Loads the leaderboard via the API
     */
    private fun loadLeaderboard() {
        this.startLoadingAnimation()
        this.doAsync {
            val resp = this@LeaderboardActivity.apiConnection.get("leaderboard", mapOf())
            val leaderboard = resp.getJSONObject("data").getJSONArray("leaderboard")
            this@LeaderboardActivity.stopLoadingAnimation()
            this@LeaderboardActivity.runOnUiThread {
                this@LeaderboardActivity.displayLeaderboard(leaderboard)
            }
        }
    }

    /**
     * Displays the leaderboard using custom leaderboard entry views
     * @param leaderboard: The JSON ranking data
     */
    private fun displayLeaderboard(leaderboard: JSONArray) {

        val list = this.findViewById<LinearLayout>(R.id.leaderboard_list)
        list.removeAllViews()

        for (i in 0..(leaderboard.length() - 1)) {

            val rankData = leaderboard.getJSONArray(i)
            val userData = rankData.getJSONObject(0)
            val name = userData.getString("username")
            val points = rankData.getInt(1)
            val rank = i + 1

            val view = LeaderboardEntryView(this, "$rank", name, "$points")
            list.addView(view)
        }
    }
}
