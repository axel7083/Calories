package com.github.calories.utils

import android.app.Activity

object ThreadUtils {

    fun execute(activity: Activity, un: () -> Any, back: (Any) -> Unit) {
        Thread {
            val output = un()
            activity.runOnUiThread {
                back(output)
            }
        }.start()
    }

}