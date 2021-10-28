package com.github.calories.models

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class Exercise(var id: Long? = null,
               var name: String? = null,
               var categories: List<Category>? = null) {

    var image: Bitmap? = null
    var doneToday = false
    var lastTime : String? = null

    fun checkDate() {
        if(lastTime == null)
            return

        val format = SimpleDateFormat("yyyy-MM-dd")
        val dateString: String = format.format(Date())
        doneToday = dateString == lastTime
    }

    fun getDesc(): String {
        val myFormat = SimpleDateFormat("yyyy-MM-dd")
        if(lastTime == null)
            return "You have never done this exercise."

        if(doneToday)
            return "You have already done this exercise today."

        try {
            val date1 = myFormat.parse(lastTime)
            val now = Date()
            val diff = now.time - date1.time

            return "Last time done was " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + " days ago."
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return "No description available."
    }

    fun addCategory(category: Category) {
        categories = categories!!.plus(category)
    }

    fun loadBitmap(context: Context) {
        try {
            val f = File(context.filesDir, "exercise_$id.jpg")
            image = BitmapFactory.decodeStream(FileInputStream(f))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Exercise

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    init {
        if(categories == null)
            categories = ArrayList()
    }
}