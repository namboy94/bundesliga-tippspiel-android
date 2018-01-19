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

package net.namibsun.hktipp.helper

import android.app.AlertDialog
import android.content.Context

/**
 * Shows an error dialog with a custom title and body for a given context
 * @param context: The context in which the dialog is shown
 * @param titleResource: The android string resource for the dialog's title
 * @param bodyResource: The android string resource for the dialog's body
 * @param cancelable: Specifies if the dialog should be cancelable or not. Defaults to true.
 */
fun showErrorDialog(context: Context, titleResource: Int, bodyResource: Int,
                            cancelable: Boolean = true) {

    val errorDialogBuilder = AlertDialog.Builder(context)
    errorDialogBuilder.setTitle(context.getString(titleResource))
    errorDialogBuilder.setMessage(context.getString(bodyResource))
    errorDialogBuilder.setCancelable(cancelable)
    errorDialogBuilder.setPositiveButton("Ok") { dialog, _ -> dialog!!.dismiss() }
    errorDialogBuilder.create()
    errorDialogBuilder.show()
}
