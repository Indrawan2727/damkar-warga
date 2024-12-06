package com.wain.damkar.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.wain.damkar.R
import com.wain.damkar.activity.LaporanActivity
import com.wain.damkar.halper.SharedPref

class HomeFragment : Fragment() {

    lateinit var btn_call : Button
    lateinit var lapor : ImageButton
    lateinit var s: SharedPref


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)

        init(view)

        s = SharedPref(requireActivity())
        mainButton()

        return view
    }
    fun mainButton() {
        btn_call.setOnClickListener {
            val phonenumber = "(0283)325429"
            val callNumber =  Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phonenumber"))
            startActivity(callNumber)
        }
        lapor.setOnClickListener {
            s.setStatusLogin(false)
            val intent = Intent (this@HomeFragment.context, LaporanActivity::class.java)
            startActivity(intent)
        }


    }
    private fun init(view: View) {
        btn_call = view.findViewById(R.id.btn_call)
        lapor = view.findViewById(R.id.lapor)
    }

}


