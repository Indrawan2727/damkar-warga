package com.wain.damkar.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.wain.damkar.MainActivity
import com.wain.damkar.R
import com.wain.damkar.app.ApiConfig
import com.wain.damkar.halper.SharedPref
import com.wain.damkar.model.ResponModel
import kotlinx.android.synthetic.main.activity_otp.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OtpActivity : AppCompatActivity() {
    // get reference of the firebase auth
    lateinit var auth: FirebaseAuth
    lateinit var s: SharedPref


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        auth=FirebaseAuth.getInstance()
        s = SharedPref(this)



        // get storedVerificationId from the intent
        val storedVerificationId= intent.getStringExtra("storedVerificationId")

        // fill otp and call the on click on button
        findViewById<Button>(R.id.login).setOnClickListener {
            aktifinn()
            val otp = findViewById<EditText>(R.id.et_otp).text.trim().toString()
            if(otp.isNotEmpty()){
                val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                        storedVerificationId.toString(), otp)
                signInWithPhoneAuthCredential(credential)
            }else{
                Toast.makeText(this,"Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // verifies if the code matches sent by firebase
    // if success start the new activity in our case it is main Activity
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                       // s.setStatusLogin(true)
                        val intent = Intent(this , MainActivity::class.java)
                        startActivity(intent)
                        finish()
                       Toast.makeText(applicationContext,"Silahkan Login", Toast.LENGTH_SHORT).show()
                    } else {
                        // Sign in failed, display a message and update the UI
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            Toast.makeText(this,"Invalid OTP", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
    }

    fun aktifinn(){
        up_level.setText("Aktif")
        val phonenumber1 = intent.getStringExtra("number")
        t_phone.setText(phonenumber1)
        s = SharedPref(this)
        pb.visibility = View.VISIBLE
        ApiConfig.instanceRetrofit.statusasa(
                t_phone.text.toString(),
                up_level.text.toString()
        ).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                //handel ketika gagal
                pb.visibility = View.GONE
                //Toast.makeText(applicationContext,"Error"+t.message, Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                pb.visibility = View.GONE
                val response = response.body()!!
                if(response.success == 1){
                    //s.setStatusLogin(true)
                    //s.setUser(response.user)
                  // Toast.makeText(applicationContext,"Silahkan login", Toast.LENGTH_SHORT).show()
                   //val intent = Intent( applicationContext, MainActivity::class.java)
                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    //startActivity(intent)
                    //finish()
                }else{
                   // Toast.makeText(applicationContext,"Error"+response, Toast.LENGTH_SHORT).show()
                }
            }

        })
    }
}