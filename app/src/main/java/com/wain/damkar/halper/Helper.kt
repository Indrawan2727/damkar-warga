package com.inyongtisto.tokoonline.helper

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.text.SimpleDateFormat

class Helper {
    fun setToolbar(activity: Activity, toolbar: Toolbar, title: String) {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        activity.supportActionBar!!.title = title
        activity.supportActionBar!!.setDisplayShowHomeEnabled(true)
        activity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    fun convertTanggal(tgl: String, formatBaru: String, fromatLama: String = "yyyy-MM-dd kk:mm:ss") :String{
        val dateFormat = SimpleDateFormat(fromatLama)
        val confert = dateFormat.parse(tgl)
        dateFormat.applyPattern(formatBaru)
        return dateFormat.format(confert)
    }
}