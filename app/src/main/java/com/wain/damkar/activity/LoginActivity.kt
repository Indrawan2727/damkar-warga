package com.wain.damkar.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wain.damkar.MainActivity
import com.wain.damkar.R
import com.wain.damkar.app.ApiConfig
import com.wain.damkar.halper.SharedPref
import com.wain.damkar.model.ResponModel
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {


    lateinit var s:SharedPref
    var backPressedTime = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        s = SharedPref(this)

        btn_register1.setOnClickListener {
            val intent = Intent( this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btn_login.setOnClickListener{
           login()
        }

    }
    override fun onBackPressed(){
        if(backPressedTime + 2000 > System.currentTimeMillis()){
            super.onBackPressed()
        }else {
            Toast.makeText(applicationContext, "Tekena kembali lagi untuk keluar aplikasi" , Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }

    fun login() {
        if  (edt_email.text.isEmpty()) {
            edt_email.error = "Kolom Email tidak boleh kosong"
            edt_email.requestFocus()
            return
        } else if (edt_password.text.isEmpty()) {
            edt_password.error = "Kolom Password tidak boleh kosong"
            edt_password.requestFocus()
            return
        }

        pb.visibility = View.VISIBLE
        ApiConfig.instanceRetrofit.login(
                edt_email.text.toString(),
                edt_password.text.toString()
        ).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                //handel ketika gagal
                pb.visibility = View.GONE
                Toast.makeText(applicationContext,"Error"+t.message, Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                pb.visibility = View.GONE
                val response = response.body()!!
                if(response.success == 1 && response.user?.level=="Aktif"){
                        s.setStatusLogin(true)
                        s.setUser(response.user)
                        val intent = Intent( applicationContext, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                        Toast.makeText(applicationContext,"Selamat Datang"+response.user.name, Toast.LENGTH_SHORT).show()
                }else if(response.user?.level=="Menunggu"){
                    Toast.makeText(applicationContext,"Belum Melakukan Verivikasi No HP", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(applicationContext,"Belum Memiliki Akun", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

}