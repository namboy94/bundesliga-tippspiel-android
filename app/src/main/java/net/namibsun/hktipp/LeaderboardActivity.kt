package net.namibsun.hktipp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import org.jetbrains.anko.doAsync
import net.namibsun.hktipp.helper.post
import net.namibsun.hktipp.views.LeaderboardEntryView
import org.json.JSONObject

/**
 * Activity that displays the current leadeboard of the hk-tippspiel website
 */
class LeaderboardActivity : AppCompatActivity() {

    /**
     * The username of the logged in user
     */
    private var username: String? = null

    /**
     * Initializes the Activity. Populates the leaderboard.
     * @param savedInstanceState: The Instance Information of the app.
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        this.setContentView(R.layout.leaderboard)
        super.onCreate(savedInstanceState)

        this.username = intent.extras.getString("username")

        (this.findViewById(R.id.leaderboard_progress) as ProgressBar).visibility = View.VISIBLE
        this.doAsync {
            val rankings = post("get_rankings", "").getJSONObject("data")
            this@LeaderboardActivity.runOnUiThread {
                this@LeaderboardActivity.populateList(rankings)
            }
        }
    }

    /**
     * Populates the leaderboard with custom leaderboard entry views
     * @param rankings: The JSON ranking data
     */
    private fun populateList(rankings: JSONObject) {
        (this.findViewById(R.id.leaderboard_progress) as ProgressBar).visibility = View.INVISIBLE

        val list = this.findViewById(R.id.leaderboard_list) as LinearLayout
        list.removeAllViews()

        for (position in rankings.keys()) {
            val userData = rankings.getJSONObject(position)
            val name = userData.getString("username")
            val points = userData.getInt("points")
            val view = LeaderboardEntryView(this, position, name, "$points")
            list.addView(view)
        }
    }
}