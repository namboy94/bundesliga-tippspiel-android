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


class BetView: CardView {

    private var matchId: Int? = null

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs) {
        View.inflate(this.context, R.layout.bet, this)
    }

    fun setMatchData(
            id: Int, homeTeam: String, awayTeam: String, homeTeamLogo: Bitmap, awayTeamLogo: Bitmap)
    {
        (this.findViewById(R.id.bet_home_team_title) as TextView).text = homeTeam
        (this.findViewById(R.id.bet_away_team_title) as TextView).text = awayTeam
        (this.findViewById(R.id.bet_home_team_logo) as ImageView).setImageBitmap(homeTeamLogo)
        (this.findViewById(R.id.bet_away_team_logo) as ImageView).setImageBitmap(awayTeamLogo)
        this.matchId = id
    }

    fun setBetData(homeTeamScore: Int, awayTeamScore: Int) {
        (this.findViewById(R.id.bet_home_team_edit) as EditText).setText(homeTeamScore.toString())
        (this.findViewById(R.id.bet_away_team_edit) as EditText).setText(awayTeamScore.toString())
    }

    fun getBetJson(): JSONObject? {

        return try {
            val home_score =
                    (this.findViewById(R.id.bet_home_team_title) as TextView).text.toString().toInt()
            val away_score =
                    (this.findViewById(R.id.bet_away_team_title) as TextView).text.toString().toInt()

            val json = JSONObject("{}")
            json.put("home_score", home_score)
            json.put("away_score", away_score)
            json.put("match_id", this.matchId)
            json

        } catch (e: NumberFormatException) {
            null
        }
    }

}
