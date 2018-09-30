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

package net.namibsun.hktipp.singletons

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import net.namibsun.hktipp.data.TeamData
import net.namibsun.hktipp.models.Team
import java.net.URL

/**
 * Singleton that stores logo bitmaps so that they do not
 * have to be re-downloaded every context switch
 */
object Logos {

    /**
     * A map that
     */
    private val logos = mutableMapOf<Int, Bitmap>()

    fun getLogo(team: Team): Bitmap {

        while (team.id !in logos) {
            this.downloadLogo(team)
        }
        return this.logos[team.id]!!
    }

    private fun downloadLogo(team: Team) {
        Log.e("LOGO", team.iconPng)
        val url = URL(team.iconPng)
        val bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
        this.logos[team.id] = bitmap
    }
}
