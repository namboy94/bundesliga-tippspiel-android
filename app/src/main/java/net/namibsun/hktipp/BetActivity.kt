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

package net.namibsun.hktipp
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.LinearLayout
import net.namibsun.hktipp.apiwrap.downloadImage
import net.namibsun.hktipp.apiwrap.getBets
import net.namibsun.hktipp.apiwrap.getMatches
import net.namibsun.hktipp.apiwrap.placeBets
import net.namibsun.hktipp.views.BetView
import org.json.JSONArray
import java.io.IOException

/**
 * The Main Activity of the Application.
 */
class BetActivity : AppCompatActivity() {

    private var betViews = mutableListOf<BetView>()
    private var username: String? = null
    private var apiKey: String? = null

    /**
     * Initializes the App's Main Activity View.
     * @param savedInstanceState: The Instance Information of the app.
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        this.setContentView(R.layout.bets)
        super.onCreate(savedInstanceState)

        val bundle = intent.extras
        this.username = bundle.getString("username")
        this.apiKey = bundle.getString("api_key")

        this.findViewById(R.id.bets_submit_button).setOnClickListener {
            BetPlacer().execute()
        }

        DataGetter().execute()

    }


    fun renderBetViews() {

        val list = this.findViewById(R.id.bets_list) as LinearLayout
        list.removeAllViews()
        for (view in this.betViews) {
            list.addView(view)
        }

    }

    inner class DataGetter: AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg params: Void?): Void? {

            try {

                val matches = getMatches(this@BetActivity.username!!, this@BetActivity.apiKey!!)
                val bets = getBets(this@BetActivity.username!!, this@BetActivity.apiKey!!)

                for (i in 0..(matches.length() -1)) {
                    val match = matches.getJSONObject(i)
                    val matchId = match.getInt("id")
                    val homeTeam = match.getJSONObject("home_team")
                    val awayTeam = match.getJSONObject("away_team")
                    val homeTeamLogo = downloadImage(homeTeam.getString("icon"))
                    val awayTeamLogo = downloadImage(awayTeam.getString("icon"))

                    val matchView = BetView(this@BetActivity)
                    matchView.setMatchData(
                            matchId,
                            homeTeam.getString("shortname"),
                            awayTeam.getString("shortname"),
                            homeTeamLogo, awayTeamLogo)

                    @Suppress("LoopToCallChain")
                    for (j in 0..(bets.length() - 1)) {
                        val bet = bets.getJSONObject(j)
                        if (bet.getInt("id") == matchId) {
                            matchView.setBetData(bet.getInt("home_score"), bet.getInt("away_score"))
                        }
                    }
                    this@BetActivity.betViews.add(matchView)
                }

                runOnUiThread({
                    this@BetActivity.renderBetViews()
                })

            } catch (e: IOException) {
                Log.e("A", e.message)
            }

            return null
        }
    }

    inner class BetPlacer: AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {

            val json = JSONArray()
            this@BetActivity.betViews
                    .mapNotNull { it.getBetJson() }
                    .forEach { json.put(it) }

            placeBets(this@BetActivity.username!!, this@BetActivity.apiKey!!, json)

            // TODO REMOVE
            betViews = mutableListOf()
            runOnUiThread({})


            DataGetter().execute()
            return null
        }

    }

}
