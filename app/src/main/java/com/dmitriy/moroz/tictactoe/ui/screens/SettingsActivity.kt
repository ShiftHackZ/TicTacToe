package com.dmitriy.moroz.tictactoe.ui.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.dmitriy.moroz.tictactoe.R
import com.dmitriy.moroz.tictactoe.databinding.ActivitySettingsBinding
import com.dmitriy.moroz.tictactoe.extensions.openInBrowser
import com.dmitriy.moroz.tictactoe.extensions.openInChromeTab
import com.dmitriy.moroz.tictactoe.extensions.share
import com.dmitriy.moroz.tictactoe.gamelogic.TicTacToeCellState
import com.dmitriy.moroz.tictactoe.gamelogic.TicTacToeParams

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupAppBar()
        updateSize()
        updateCharacter()
        binding.btnSizePlus.setOnClickListener {
            plus()
        }
        binding.btnSizeMinus.setOnClickListener {
            minus()
        }
        binding.ticCross.setOnClickListener {
            TicTacToeParams.playerType = TicTacToeCellState.CROSS
            TicTacToeParams.phoneType = TicTacToeCellState.ZERO
            updateCharacter()
        }
        binding.ticZero.setOnClickListener {
            TicTacToeParams.playerType = TicTacToeCellState.ZERO
            TicTacToeParams.phoneType = TicTacToeCellState.CROSS
            updateCharacter()
        }
        binding.titleGooglePlay.setOnClickListener {
            openInBrowser("https://play.google.com/store/apps/details?id=com.dmitriy.moroz.tictactoe")
        }
        binding.titleDev.setOnClickListener {
            openInChromeTab("https://moroz.cc")
        }
        binding.titleShare.setOnClickListener {
            share(getString(R.string.share_message), "https://play.google.com/store/apps/details?id=com.dmitriy.moroz.tictactoe")
        }
        binding.titleRules.setOnClickListener {
            startActivity(Intent(this, RulesActivity::class.java))
        }
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
        supportActionBar?.title = getString(R.string.title_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun plus() {
        if (TicTacToeParams.BOARD_SIZE < TicTacToeParams.MAX_BOARD_SIZE) {
            TicTacToeParams.BOARD_SIZE++
            updateSize()
        }
    }

    private fun minus() {
        if (TicTacToeParams.BOARD_SIZE > TicTacToeParams.MIN_BOARD_SIZE) {
            TicTacToeParams.BOARD_SIZE--
            updateSize()
        }
    }

    private fun updateSize() {
        binding.etSize.setText(TicTacToeParams.BOARD_SIZE.toString())
    }

    private fun updateCharacter() {
        if (TicTacToeParams.playerType == TicTacToeCellState.CROSS) {
            binding.ticCross.setState(TicTacToeCellState.CROSS)
            binding.ticZero.setState(TicTacToeCellState.UNDEFINED)
        } else {
            binding.ticCross.setState(TicTacToeCellState.UNDEFINED)
            binding.ticZero.setState(TicTacToeCellState.ZERO)
        }
    }
}
