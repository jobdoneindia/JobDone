package com.jobdoneindia.jobdone

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class DashboardActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarToggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView
    private lateinit var imgMenuToggle: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        supportActionBar?.hide()

        // defining Drawer Layout, Action Bar Toggle, Navigation View and toggle button in header
        drawerLayout = findViewById(R.id.drawer)
        actionBarToggle = ActionBarDrawerToggle(this, drawerLayout, 0, 0)
        drawerLayout.addDrawerListener(actionBarToggle)
        navView = findViewById(R.id.navdrawermenu)
        imgMenuToggle = findViewById(R.id.imageMenu)


        // Click listener for items of Navigation Drawer items
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.profile_nav -> {
                    Toast.makeText(this, "My Profile", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.rewards_nav -> {
                    Toast.makeText(this, "Rewards", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.settings_nav -> {
                    Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.logout_nav -> {
                    Toast.makeText(this, "Logging Out.....you'll regret this", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> {
                    false
                }
            }
        }

        // Toggle button Navigation Drawer (hamburger icon)
        imgMenuToggle.setOnClickListener {
            if(!this.drawerLayout.isDrawerOpen(GravityCompat.END)) {
                this.drawerLayout.openDrawer(GravityCompat.END)
            } else {
                super.onBackPressed()
            }
        }

    }

    private fun Int.dpToPixels(context: Context): Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,this.toString().toFloat(),context.resources.displayMetrics
    )

    override fun onSupportNavigateUp(): Boolean {
        drawerLayout.openDrawer(navView)
        return true
    }
}