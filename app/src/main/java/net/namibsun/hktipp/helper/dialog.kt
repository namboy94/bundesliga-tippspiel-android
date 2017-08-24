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
