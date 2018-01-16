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

package net.namibsun.hktipp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import net.namibsun.hktipp.data.BetData
import net.namibsun.hktipp.data.MatchData
import net.namibsun.hktipp.singletons.Logos
import org.jetbrains.anko.doAsync

/**
 * Activity that display information about a single match
 */
class SingleMatchActivity : AppCompatActivity() {

    private var matchData: MatchData? = null
    private var betData: BetData? = null

    /**
     * Displays the match data
     * @param savedInstanceState: The saved instance state
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        this.setContentView(R.layout.single_match)
        super.onCreate(savedInstanceState)

        this.matchData = this.intent.extras.get("match_data") as MatchData
        this.betData = this.intent.extras.get("bet_data") as BetData

        val homeScore = if (this.matchData!!.started) {
            "${this.matchData!!.homeFtScore}"
        } else {
            "-"
        }
        val awayScore = if (this.matchData!!.started) {
            "${this.matchData!!.awayFtScore}"
        } else {
            "-"
        }

        (this.findViewById(R.id.home_team_score) as TextView).text = homeScore
        (this.findViewById(R.id.away_team_score) as TextView).text = awayScore

        this.doAsync {
            val homeLogo = this@SingleMatchActivity.findViewById(R.id.home_team_logo) as ImageView
            val awayLogo = this@SingleMatchActivity.findViewById(R.id.away_team_logo) as ImageView
            val homeBitmap = Logos.getLogo(this@SingleMatchActivity.matchData!!.homeTeam)
            val awayBitmap = Logos.getLogo(this@SingleMatchActivity.matchData!!.awayTeam)

            this@SingleMatchActivity.runOnUiThread {
                homeLogo.setImageBitmap(homeBitmap)
                awayLogo.setImageBitmap(awayBitmap)
            }
        }
    }
}