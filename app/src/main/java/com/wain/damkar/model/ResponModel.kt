package com.wain.damkar.model

class ResponModel {
    var  success = 0
    lateinit var message:String
    var user = User()

    var laporans : ArrayList<Laporan> = ArrayList()
}