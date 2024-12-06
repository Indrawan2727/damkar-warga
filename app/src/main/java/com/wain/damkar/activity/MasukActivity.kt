package com.wain.damkar.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wain.damkar.R
import com.wain.damkar.halper.SharedPref
import kotlinx.android.synthetic.main.activity_masuk.*

class MasukActivity : AppCompatActivity() {


    lateinit var s:SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_masuk)
        s = SharedPref(this)

        btn_prosesLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        btn_register.setOnClickListener {
        val intent = Intent( this, RegisterActivity::class.java)
        startActivity(intent)
        //startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}