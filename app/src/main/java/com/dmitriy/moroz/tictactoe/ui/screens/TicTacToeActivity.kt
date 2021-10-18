package com.dmitriy.moroz.tictactoe.ui.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.view.children
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.dmitriy.moroz.tictactoe.*
import com.dmitriy.moroz.tictactoe.gamelogic.*
import com.dmitriy.moroz.tictactoe.ui.views.TicTacToeCell
import com.dmitriy.moroz.tictactoe.databinding.ActivityTicTacToeBinding
import kotlin.random.Random

class TicTacToeActivity : AppCompatActivity() {

    var winDialogShowing = false

    private var previousStep = TicTacToeStep.PLAYER

    private lateinit var binding: ActivityTicTacToeBinding

    private val currentStep = MutableLiveData<TicTacToeStep>()

    private val wins = MutableLiveData<Int>()
    private val loses = MutableLiveData<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTicTacToeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        generateLayout()
        checkForWinCases()
        wins.postValue(0)
        loses.postValue(0)
        currentStep.postValue(TicTacToeStep.PLAYER)
        currentStep.observe(this, Observer {
            if (it == TicTacToeStep.PHONE) {
                binding.tvTurn.text = getString(R.string.step_phone)
                Handler().postDelayed({
                    if (!winDialogShowing) {
                        checkFreeAndPutRandom()
                    }
                }, 1000L)
            } else {
                if (!winDialogShowing) {
                    binding.tvTurn.text = getString(R.string.step_your)
                }
            }
            previousStep = it
        })
        wins.observe(this, Observer {
            binding.tvWins.text = getString(R.string.wins, it.toString())
        })
        loses.observe(this, Observer {
            binding.tvLoses.text = getString(R.string.loses, it.toString())
        })
        binding.btnRestart.setOnClickListener {
            winDialogShowing = false
            currentStep.postValue(TicTacToeStep.PLAYER)
            generateLayout()
        }
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        winDialogShowing = false
        currentStep.postValue(TicTacToeStep.PLAYER)
        generateLayout()
        checkForWinCases()
        setupAppBar()
    }

    private fun setupAppBar() {
        supportActionBar?.title = getString(R.string.title_play)
        when (TicTacToeParams.playerType) {
            TicTacToeCellState.CROSS -> {
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_tool_home)
            }
            TicTacToeCellState.ZERO -> {
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_tool_home_alt)
            }
            else -> Unit
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun generateLayout() {
        binding.container.removeAllViews()
        winDialogShowing = false
        for (i in 0 until TicTacToeParams.BOARD_SIZE) {
            val linear = LinearLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setGravity(Gravity.CENTER)
                }
                gravity = Gravity.CENTER
                orientation = LinearLayout.HORIZONTAL
            }
            for (j in 0 until TicTacToeParams.BOARD_SIZE) {
                linear.addView(
                    TicTacToeCell(this).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1.0f
                        ).apply {
                            gravity = Gravity.CENTER
                            setMargins(6,6,6,6)
                        }

                        background = getDrawable(R.drawable.background_cell)

                        setOnClickListener {
                            if (currentStep.value == TicTacToeStep.PLAYER) {
                                if ((it as TicTacToeCell).getState().isFree()) {
                                    setState(TicTacToeParams.playerType)
                                    checkForWinCases()
                                    currentStep.postValue(TicTacToeStep.PHONE)
                                }
                            }
                        }
                        setCoordinates(i, j)
                        setState(TicTacToeCellState.UNDEFINED)
                    }
                )
            }
            binding.container.addView(linear)
        }
    }

    private fun checkFreeAndPutRandom() {
        if (!winDialogShowing) {
            // Creating temp array for free cells
            val free: ArrayList<TicTacToeCell> = arrayListOf()
            // Loading free cells to array
            binding.container.children.forEach {
                if (it is LinearLayout) {
                    it.children.forEach {
                        val cell = it as TicTacToeCell
                        if (cell.getState().isFree()) {
                            free.add(it)
                        }
                    }
                }
            }

            if (free.isNotEmpty()) {
                // If there are empty items pick one and fill with ${phoneType} cell
                val randomIndex = Random.nextInt(0, free.size)

                binding.container.children.forEach {
                    (it as LinearLayout).children.forEach {
                        if (free[randomIndex].getCoordinates() == (it as TicTacToeCell).getCoordinates()) {
                            it.setState(TicTacToeParams.phoneType)
                        }
                    }
                }

                if (!winDialogShowing) {
                    checkForWinCases()
                    currentStep.postValue(TicTacToeStep.PLAYER)
                }
            } else {
                // If there are no empty items left, then the draw case happened
                showNoWinnerDialog()
            }
        }
    }

    private fun checkForWinCases() {
        // Variables for remembering vertical states
        val verticalStates: ArrayList<TicTacToeCellState> = arrayListOf()
        val verticalSameCount: ArrayList<Int> = arrayListOf()

        // Filling vertical cases parameters based on board size
        for (i in 0 until TicTacToeParams.BOARD_SIZE) {
            verticalStates.add(TicTacToeCellState.UNDEFINED)
            verticalSameCount.add(0)
        }

        // Variables for remembering first diagonal state
        var firstDiagonalState: TicTacToeCellState =
            TicTacToeCellState.UNDEFINED
        var firstDiagonalSameCount: Int = 0

        // Variables for remembering second diagonal state
        var secondDiagonalState: TicTacToeCellState =
            TicTacToeCellState.UNDEFINED
        var secondDiagonalSameCount = 0

        binding.container.children.forEachIndexed { rootIndex, linear ->
            verticalSameCount[rootIndex]++
            if (linear is LinearLayout) {

                // Variables for remembering horizontal states
                var horizontalState: TicTacToeCellState =
                    TicTacToeCellState.UNDEFINED
                var horizontalSameCount = 0

                linear.children.forEachIndexed { index, view ->

                    // Vertical check
                    if (rootIndex == 0) {
                        verticalStates[index] = (view as TicTacToeCell).getState()
                    } else {
                        if (verticalStates[index] == (view as TicTacToeCell).getState() && view.getState().isValid()) {
                            verticalSameCount[index]++
                        }
                    }

                    // First diagonal check
                    if (rootIndex == 0 && view.hasSameCoordinates()) {
                        firstDiagonalState = view.getState()
                        firstDiagonalSameCount++
                    } else if (view.hasSameCoordinates() && view.getState().isValid()) {
                        if (firstDiagonalState == view.getState()) {
                            firstDiagonalSameCount++
                        }
                    }

                    // Second diagonal check
                    if (rootIndex == 0 && view.isSecondDiagonalItem()) {
                        secondDiagonalState = view.getState()
                        secondDiagonalSameCount++
                    } else if (view.isSecondDiagonalItem() && view.getState().isValid()) {
                        if (secondDiagonalState == view.getState()) {
                            secondDiagonalSameCount++
                        }
                    }

                    // Horizontal check
                    if (index == 0) {
                        horizontalState = (view as TicTacToeCell).getState()
                        horizontalSameCount++
                    } else {
                        if (horizontalState == (view as TicTacToeCell).getState() && view.getState().isValid()) {
                            horizontalSameCount++
                        }
                    }

                    // Remember current cell state
                    horizontalState = view.getState()
                    verticalStates[index] = view.getState()
                    if (view.hasSameCoordinates()) {
                        firstDiagonalState = view.getState()
                    }
                    if (view.isSecondDiagonalItem()) {
                        secondDiagonalState = view.getState()
                    }
                }
                // Check for win cases
                checkForHorizontalWin(horizontalSameCount, horizontalState)
                checkForVerticalWin(verticalSameCount, verticalStates)
                checkForFirstDiagonalWin(firstDiagonalSameCount, firstDiagonalState)
                checkForSecondDiagonalWin(secondDiagonalSameCount, secondDiagonalState)
                checkForDraw()
            }
        }
    }

    private fun checkForDraw() {
        if (!winDialogShowing) {
            val free: ArrayList<TicTacToeCell> = arrayListOf()
            binding.container.children.forEach {
                if (it is LinearLayout) {
                    it.children.forEach {
                        val cell = it as TicTacToeCell
                        if (cell.getState().isFree()) {
                            free.add(it)
                        }
                    }
                }
            }
            if (free.isEmpty() && !winDialogShowing) {
                showNoWinnerDialog()
            }
        }
    }

    private fun checkForSecondDiagonalWin(sameItems: Int, state: TicTacToeCellState) {
        if (sameItems == TicTacToeParams.BOARD_SIZE) {
            if (state == TicTacToeParams.playerType) {
                showWinDialog()
            } else {
                showLoseDialog()
            }
        }
    }

    private fun checkForFirstDiagonalWin(sameItems: Int, state: TicTacToeCellState) {
        if (sameItems == TicTacToeParams.BOARD_SIZE) {
            if (state == TicTacToeParams.playerType) {
                showWinDialog()
            } else {
                showLoseDialog()
            }
        }
    }

    private fun checkForVerticalWin(sameItems: ArrayList<Int>, states: ArrayList<TicTacToeCellState>) {
        sameItems.forEachIndexed { index, it ->
            if (it == TicTacToeParams.BOARD_SIZE) {
                if (states[index] == TicTacToeParams.playerType) {
                    showWinDialog()
                } else {
                    showLoseDialog()
                }
            }
        }
    }

    private fun checkForHorizontalWin(sameItems: Int, state: TicTacToeCellState) {
        if (sameItems == TicTacToeParams.BOARD_SIZE) {
            if (state == TicTacToeParams.playerType) {
                showWinDialog()
            } else {
                showLoseDialog()
            }
        }
    }

    private fun showNoWinnerDialog() {
        currentStep.postValue(TicTacToeStep.PLAYER)
        winDialogShowing = true
        AlertDialog.Builder(this)
            .setTitle(R.string.draw)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .setOnDismissListener {
                winDialogShowing = false
                currentStep.postValue(TicTacToeStep.PLAYER)
                generateLayout()
                checkForWinCases()
                currentStep.postValue(TicTacToeStep.PHONE)
            }
            .show()
    }

    private fun showWinDialog() {
        currentStep.postValue(TicTacToeStep.PLAYER)
        winDialogShowing = true
        wins.postValue(wins.value!! + 1)
        AlertDialog.Builder(this)
            .setTitle(R.string.won)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .setOnDismissListener {
                winDialogShowing = false
                //currentStep.postValue(TicTacToeStep.PHONE)
                generateLayout()
                checkForWinCases()
                currentStep.postValue(TicTacToeStep.PHONE)
                //currentStep.postValue(TicTacToeStep.PHONE)
            }
            .show()
    }

    private fun showLoseDialog() {
        //currentStep.postValue(TicTacToeStep.PLAYER)
        winDialogShowing = true
        loses.postValue(loses.value!! + 1)
        AlertDialog.Builder(this)
            .setTitle(R.string.lose)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .setOnDismissListener {
                winDialogShowing = false
                currentStep.postValue(TicTacToeStep.PLAYER)
                generateLayout()
                checkForWinCases()
            }
            .show()
    }
}
