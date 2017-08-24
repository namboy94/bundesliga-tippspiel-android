package net.namibsun.hktipp.helper

import android.content.Context
import android.content.SharedPreferences

fun getDefaultSharedPreferences(context: Context) : SharedPreferences =
        context.getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE)