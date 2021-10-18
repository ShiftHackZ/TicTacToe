package com.dmitriy.moroz.tictactoe.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.dmitriy.moroz.tictactoe.*
import com.dmitriy.moroz.tictactoe.databinding.ViewTicTacToeCellBinding
import com.dmitriy.moroz.tictactoe.extensions.hide
import com.dmitriy.moroz.tictactoe.extensions.show
import com.dmitriy.moroz.tictactoe.gamelogic.TicTacToeCellState
import com.dmitriy.moroz.tictactoe.gamelogic.TicTacToeParams

class TicTacToeCell: ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val view by lazy {
        inflate(context,
            R.layout.view_tic_tac_toe_cell, this)
    }

    private val stateCross: ImageView by lazy { view.findViewById(R.id.cross) }
    private val stateZero: ImageView by lazy { view.findViewById(R.id.zero) }
    private val coordsView: TextView by lazy { view.findViewById(R.id.coordinates) }

    private var state: TicTacToeCellState = TicTacToeCellState.UNDEFINED
    private var coordinates: Pair<Int, Int> = Pair(0, 0)

    init {
        setState(state)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val size = if (width > height) height else width
        setMeasuredDimension(size, size)
    }

    fun hasSameCoordinates(): Boolean = this.coordinates.first == this.coordinates.second

    fun isSecondDiagonalItem(): Boolean = this.coordinates.first + this.coordinates.second == TicTacToeParams.BOARD_SIZE - 1

    fun getState(): TicTacToeCellState = this.state

    fun setState(state: TicTacToeCellState) {
        this.state = state
        when (this.state) {
            TicTacToeCellState.UNDEFINED -> {
                stateCross.hide()
                stateZero.hide()
            }
            TicTacToeCellState.CROSS -> {
                stateCross.show()
                stateZero.hide()
            }
            TicTacToeCellState.ZERO -> {
                stateCross.hide()
                stateZero.show()
            }
        }
        invalidate()
        requestLayout()
    }

    fun getCoordinates() = this.coordinates

    fun setCoordinates(x: Int, y: Int) {
        this.coordinates = Pair(x, y)
        coordsView.text = "[$x; $y]"
    }
}