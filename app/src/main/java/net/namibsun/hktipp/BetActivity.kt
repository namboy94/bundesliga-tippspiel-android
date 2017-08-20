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
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * The Main Activity of the Application.
 */
class BetActivity : AppCompatActivity() {

    /**
     * Initializes the App's Main Activity View.
     * @param savedInstanceState: The Instance Information of the app.
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.bets)
    }


}
