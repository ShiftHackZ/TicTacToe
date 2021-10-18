package com.dmitriy.moroz.tictactoe.ui.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.dmitriy.moroz.tictactoe.R

class RulesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rules)
        setupAppBar()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return true
    }

    private fun setupAppBar() {
        supportActionBar?.title = getString(R.string.rules)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
