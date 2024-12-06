package com.wain.damkar.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.wain.damkar.MainActivity
import com.wain.damkar.R
import com.wain.damkar.app.ApiConfig
import com.wain.damkar.model.ResponModel
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class RegisterActivity : AppCompatActivity() {
        // this stores the phone number of the user
        var number : String =""
        val code = "+62"
        // create instance of firebase auth
        lateinit var auth: FirebaseAuth
        // we will use this to match the sent otp from firebase
        lateinit var storedVerificationId:String
        lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
        private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_register)

            auth=FirebaseAuth.getInstance()

            // start verification on click of the button
            findViewById<Button>(R.id.button_otp).setOnClickListener {
                login()
                register()
            }

            // Callback function for Phone Auth
            callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                // This method is called when the verification is completed
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                    Log.d("GFG" , "onVerificationCompleted Success")
                }

                // Called when verification is failed add log statement to see the exception
                override fun onVerificationFailed(e: FirebaseException) {
                    Log.d("GFG" , "onVerificationFailed  $e")
                }

                // On code is sent by the firebase this method is called
                // in here we start a new activity where user can enter the OTP
                override fun onCodeSent(
                        verificationId: String,
                        token: PhoneAuthProvider.ForceResendingToken
                ) {
                    Log.d("GFG","onCodeSent: $verificationId")
                    storedVerificationId = verificationId
                    resendToken = token
                    // Start a new activity using intent
                    // also send the storedVerificationId using intent
                    // we will use this id to send the otp back to firebase
                    val intent = Intent(applicationContext,OtpActivity::class.java)
                    intent.putExtra("storedVerificationId",storedVerificationId)
                    intent.putExtra("number", number)
                    startActivity(intent)
                    finish()
                }
            }
        }

        private fun login() {
            number = findViewById<EditText>(R.id.et_phone_number).text.trim().toString()
            // get the phone number from edit text and append the country cde with it
            if (number.isNotEmpty()){
                val phonenumber = "$code$number"
                sendVerificationCode(phonenumber)
            }else{
                Toast.makeText(this,"Enter mobile number", Toast.LENGTH_SHORT).show()
            }
        }

        // this method sends the verification code and starts the callback of verification
        // which is implemented above in onCreate
        private fun sendVerificationCode(number: String) {
            val phonenumber = "$code$number"
            val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(number) // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(this) // Activity (for callback binding)
                    .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                    .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
            Log.d("GFG" , "Auth started")
        }

        fun register() {
            edt_level.setText("Menunggu")
            if (edt_nama.text.isEmpty()) {
                edt_nama.error = "Kolom Nama tidak boleh kosong"
                edt_nama.requestFocus()
                return
            } else if (edt_nik.text.isEmpty()) {
                edt_nik.error = "Kolom NIK tidak boleh kosong"
                edt_nik.requestFocus()
                return
            } else if (edt_email.text.isEmpty()) {
                edt_email.error = "Kolom Email tidak boleh kosong"
                edt_email.requestFocus()
                return
            } else if (et_phone_number.text.isEmpty()) {
                et_phone_number.error = "Kolom Phone tidak boleh kosong"
                et_phone_number.requestFocus()
                return
            } else if (edt_password.text.isEmpty()) {
                edt_password.error = "Kolom Password tidak boleh kosong"
                edt_password.requestFocus()
                return
            }

            pb.visibility = View.VISIBLE
            ApiConfig.instanceRetrofit.register(
                    edt_nama.text.toString(),
                    edt_nik.text.toString(),
                    edt_email.text.toString(),
                    et_phone_number.text.toString(),
                    edt_level.text.toString(),
                    edt_password.text.toString()
            ).enqueue(object : Callback<ResponModel> {
                override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                    pb.visibility = View.GONE
                    //handel ketika gagal
                    Toast.makeText(applicationContext,"Error"+t.message, Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                    pb.visibility = View.VISIBLE
                    val response = response.body()!!
                    if(response.success == 1){
                    }else{
                        Toast.makeText(applicationContext,"Error"+response, Toast.LENGTH_SHORT).show()
                    }
                }

            })
        }
}