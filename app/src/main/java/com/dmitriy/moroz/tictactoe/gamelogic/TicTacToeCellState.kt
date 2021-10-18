package com.dmitriy.moroz.tictactoe.gamelogic

enum class TicTacToeCellState {
    UNDEFINED, CROSS, ZERO;

    override fun toString(): String {
        return when(this) {
            UNDEFINED -> "Undefined"
            CROSS -> "Cross"
            ZERO -> "Zero"
        }
    }
}

fun TicTacToeCellState.isValid(): Boolean = this != TicTacToeCellState.UNDEFINED

fun TicTacToeCellState.isFree(): Boolean = this == TicTacToeCellState.UNDEFINED