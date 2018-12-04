package com.example.garcia76.hotelavaya

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.example.garcia76.hotelavaya.Fragments.Dashboard
import com.example.garcia76.hotelavaya.Fragments.Home
import com.example.garcia76.hotelavaya.Fragments.SettingsFragment
import com.example.garcia76.hotelavaya.Utils.useInsecureSSL
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {

                val homeamv_fg = Home.newInstance()
                openFragment(homeamv_fg)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                val dashboard_fg = Dashboard.newInstance()
                openFragment(dashboard_fg)
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_notifications -> {
                val settings_fg = SettingsFragment.newInstance()

                openFragment(settings_fg)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        var myPreferences = "myPrefs"
        var sharedPreferences = getSharedPreferences(myPreferences, Context.MODE_PRIVATE)
        var nombre = sharedPreferences.getString("nombre", "0")
        Toast.makeText(this@HomeActivity, "Bienvenido Sr. $nombre", Toast.LENGTH_SHORT).show()
        val homeamv_fg = Home.newInstance()
        openFragment(homeamv_fg)
    }


    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


}
