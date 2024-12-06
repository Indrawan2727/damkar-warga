package com.wain.damkar

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.wain.damkar.activity.LoginActivity
import com.wain.damkar.fragments.HistoryFragment
import com.wain.damkar.fragments.HomeFragment
import com.wain.damkar.fragments.InformasiFragment
import com.wain.damkar.fragments.ProfileFragment
import com.wain.damkar.halper.SharedPref



class MainActivity : AppCompatActivity() {

    private val fragmentHome: Fragment = HomeFragment()
    private val fragmentHistory: Fragment = HistoryFragment()
    private var fragmentProfile: Fragment = ProfileFragment()
    private var fragmentInformasi: Fragment = InformasiFragment()
    private val fm: FragmentManager = supportFragmentManager
    private var active: Fragment = fragmentHome

    private lateinit var menu: Menu
    private lateinit var menuItem: MenuItem
    private lateinit var bottomNavigationView: BottomNavigationView

    private var statusLogin = false
    private lateinit var s:SharedPref


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        s = SharedPref(this)

        if (s.getStatusLogin()) {
            setUpBottomNav()
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }

    }
        fun setUpBottomNav() {
            fm.beginTransaction().add(R.id.container, fragmentHome).show(fragmentHome).commit()
            fm.beginTransaction().add(R.id.container, fragmentHistory).hide(fragmentHistory).commit()
            fm.beginTransaction().add(R.id.container, fragmentInformasi).hide(fragmentInformasi).commit()
            fm.beginTransaction().add(R.id.container, fragmentProfile).hide(fragmentProfile).commit()

            bottomNavigationView = findViewById(R.id.bottom_navigation_view)
            menu = bottomNavigationView.menu
            menuItem = menu.getItem(0)
            menuItem.isChecked = true

            bottomNavigationView.setOnNavigationItemSelectedListener { item ->

                when (item.itemId) {
                    R.id.homeFragment -> {
                        callFargment(0, fragmentHome)
                    }
                    R.id.historyFragment -> {
                        callFargment(1, fragmentHistory)
                    }
                    R.id.informasiFragment -> {
                        callFargment(2, fragmentInformasi)
                    }
                    R.id.profileFragment -> {
                        callFargment(3, fragmentProfile)
                    }
                }

                false
            }
        }
        fun callFargment(int: Int, fragment: Fragment) {
            menuItem = menu.getItem(int)
            menuItem.isChecked = true
            fm.beginTransaction().hide(active).show(fragment).commit()
            active = fragment
        }
}