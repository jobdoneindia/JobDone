package com.jobdoneindia.jobdone.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jobdoneindia.jobdone.R

class EditProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // Add back button in Action Bar
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        // Done fab button
        val doneButton: FloatingActionButton = findViewById(R.id.done_button)
        doneButton.setOnClickListener {
            startActivity(Intent(applicationContext, AccountsActivity::class.java))
        }


    }
    // Set up function for back button in Action Bar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                android.R.id.home -> {
                    this.finish()
                    return true
                }
            }
            return super.onOptionsItemSelected(item)
    }
}