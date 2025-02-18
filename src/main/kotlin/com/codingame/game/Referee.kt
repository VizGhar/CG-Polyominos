package com.codingame.game

import com.codingame.gameengine.core.AbstractPlayer
import com.codingame.gameengine.core.AbstractReferee
import com.codingame.gameengine.core.SoloGameManager
import com.codingame.gameengine.module.entities.GraphicEntityModule
import com.google.inject.Inject

val tileVariants = mapOf(
    'A' to listOf(listOf("O..", "OOO"), listOf(".O", ".O", "OO"), listOf("OOO", "..O"), listOf("OO", "O.", "O."), listOf("OO", ".O", ".O"), listOf("OOO", "O.."), listOf("O.", "O.", "OO"), listOf("..O", "OOO")),
    'B' to listOf(listOf("O.O", "OOO"), listOf("OO", ".O", "OO"), listOf("OOO", "O.O"), listOf("OO", "O.", "OO")),
    'C' to listOf(listOf(".O.", "OOO", ".O.")),
    'D' to listOf(listOf(".O.", "OOO"), listOf(".O", "OO", ".O"), listOf("OOO", ".O."), listOf("O.", "OO", "O.")),
    'E' to listOf(listOf(".O.", ".O.", "OOO"), listOf("..O", "OOO", "..O"), listOf("OOO", ".O.", ".O."), listOf("O..", "OOO", "O..")),
    'F' to listOf(listOf("OOOO"), listOf("O", "O", "O", "O")),
    'G' to listOf(listOf("OO.", ".OO", "..O"), listOf(".OO", "OO.", "O.."), listOf("O..", "OO.", ".OO"), listOf("..O", ".OO", "OO.")),
    'H' to listOf(listOf(".O", "OO"), listOf("OO", ".O"), listOf("OO", "O."), listOf("O.", "OO")),
    'I' to listOf(listOf("OO", ".O", "OO", "O."), listOf("OOO.", "O.OO"), listOf(".O", "OO", "O.", "OO"), listOf("OO.O", ".OOO"), listOf("O.OO", "OOO."), listOf("O.", "OO", ".O", "OO"), listOf(".OOO", "OO.O"), listOf("OO", "O.", "OO", ".O")),
    'J' to listOf(listOf("OOO", "..O", "..O"), listOf("OOO", "O..", "O.."), listOf("O..", "O..", "OOO"), listOf("..O", "..O", "OOO")),
    'K' to listOf(listOf("OO", "OO")),
    'L' to listOf(listOf(".OO", ".O.", "OO."), listOf("O..", "OOO", "..O"), listOf("..O", "OOO", "O.."), listOf("OO.", ".O.", ".OO")),
    'M' to listOf(listOf(".OO", "OO."), listOf("O.", "OO", ".O"), listOf(".O", "OO", "O."), listOf("OO.", ".OO")),
    'N' to listOf(listOf("OOO", "OO."), listOf("O.", "OO", "OO"), listOf(".OO", "OOO"), listOf("OO", "OO", ".O"), listOf("OO", "OO", "O."), listOf("OO.", "OOO"), listOf(".O", "OO", "OO"), listOf("OOO", ".OO"))
)

class Referee : AbstractReferee() {

    @Inject
    private lateinit var gameManager: SoloGameManager<Player>

    @Inject
    private val graphicEntityModule: GraphicEntityModule? = null

    private lateinit var remainingCharacters: String
    private lateinit var board: Array<CharArray>

    override fun init() {
        gameManager.firstTurnMaxTime = 1000
        gameManager.turnMaxTime = 50
        remainingCharacters = gameManager.testCaseInput.last()
        val (rows, _) = gameManager.testCaseInput.first().split(" ").map { it.toInt() }
        board = gameManager.testCaseInput.drop(1).take(rows).map { it.toCharArray() }.toTypedArray()
    }

    override fun gameTurn(turn: Int) {
        val currentChar = remainingCharacters[0]

        // input processing
        gameManager.player.sendInputLine(remainingCharacters.toList().sorted().joinToString(""))
        gameManager.player.sendInputLine(currentChar.toString())
        gameManager.player.sendInputLine("${board.size} ${board[0].size}")
        board.forEach { gameManager.player.sendInputLine(it.joinToString("")) }

        try {
            // execution
            gameManager.player.execute()

            // processing outputs
            val (tile, pos) = gameManager.player.outputs
            val adjustedInput = tile.split(";")
            val (row, col) = pos.split(" ").map { it.toInt() }

            if (!(tileVariants[currentChar]?: listOf()).contains(adjustedInput)) {
                gameManager.loseGame("Illegal tile variant")
                return
            }

            for (dy in adjustedInput.indices) {
                for (dx in adjustedInput[0].indices) {
                    if (adjustedInput[dy][dx] == '.') continue
                    if (dy + row >= board.size || dx + col >= board[0].size) { gameManager.loseGame("Incorrect tile placement"); return }
                    if (board[dy + row][dx + col] != 'O') { gameManager.loseGame("Incorrect tile placement"); return }
                    board[dy + row][dx + col] = currentChar
                }
            }

            remainingCharacters = remainingCharacters.drop(1)
        } catch (e: AbstractPlayer.TimeoutException) {
            gameManager.loseGame("Timeout")
            return
        } catch (e: Exception) {
            gameManager.loseGame("Invalid player output. Check game statement")
            return
        }

        if (remainingCharacters == "") {
            gameManager.winGame("Congrats!")
        }
    }
}
