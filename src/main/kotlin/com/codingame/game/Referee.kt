package com.codingame.game

import com.codingame.gameengine.core.AbstractPlayer
import com.codingame.gameengine.core.AbstractReferee
import com.codingame.gameengine.core.SoloGameManager
import com.codingame.gameengine.module.entities.GraphicEntityModule
import com.codingame.gameengine.module.entities.Rectangle
import com.codingame.gameengine.module.entities.Text
import com.google.inject.Inject
import kotlin.math.roundToInt

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

val tileColors = mapOf(
    'A' to 0xFE73C9,
    'B' to 0xEC9442,
    'C' to 0xDA5658,
    'D' to 0x8E2DA6,
    'E' to 0x50D0A6,
    'F' to 0x4D4DFF,
    'G' to 0xB469FF,
    'H' to 0xD1D145,
    'I' to 0x34ABB8,
    'J' to 0x8A8A8A,
    'K' to 0x7B9031,
    'L' to 0x45AB58,
    'M' to 0x698FEA,
    'N' to 0xAA3030
)

class Referee : AbstractReferee() {

    @Inject
    private lateinit var gameManager: SoloGameManager<Player>

    @Inject
    private lateinit var graphicEntityModule: GraphicEntityModule

    private lateinit var remainingCharacters: String
    private lateinit var board: Array<CharArray>

    override fun init() {
        gameManager.firstTurnMaxTime = 2000
        gameManager.turnMaxTime = 50
        remainingCharacters = gameManager.testCaseInput.last()
        val (rows, _) = gameManager.testCaseInput.first().split(" ").map { it.toInt() }
        board = gameManager.testCaseInput.drop(1).take(rows).map { it.toCharArray() }.toTypedArray()
        initVisual()
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

            visualize(adjustedInput, row, col)

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

    private lateinit var text: Text

    private fun isTaken(x: Int, y: Int) = x in board[0].indices && y in board.indices && board[y][x] != '.'

    private val tiles = mutableMapOf<Pair<Int,Int>, Rectangle>()

    private fun initVisual() {

        text = graphicEntityModule
            .createText("")
            .setFontFamily("monospace")
            .setFillColor(0xffffff)

        val til = board.mapIndexed { y, chars -> chars.mapIndexed { x, _ -> (x to y).takeIf { isTaken(x, y) } }.filterNotNull() }.flatten()
        val top = board.mapIndexed { y, chars -> chars.mapIndexed { x, _ -> (x to y).takeIf { isTaken(x, y) && !isTaken(x, y - 1) } }.filterNotNull() }.flatten()
        val bot = board.mapIndexed { y, chars -> chars.mapIndexed { x, _ -> (x to y).takeIf { isTaken(x, y) && !isTaken(x, y + 1) } }.filterNotNull() }.flatten()
        val lef = board.mapIndexed { y, chars -> chars.mapIndexed { x, _ -> (x to y).takeIf { isTaken(x, y) && !isTaken(x - 1, y) } }.filterNotNull() }.flatten()
        val rig = board.mapIndexed { y, chars -> chars.mapIndexed { x, _ -> (x to y).takeIf { isTaken(x, y) && !isTaken(x + 1, y) } }.filterNotNull() }.flatten()
        val otl = board.mapIndexed { y, chars -> chars.mapIndexed { x, _ -> (x to y).takeIf { isTaken(x, y) && !isTaken(x - 1, y) && !isTaken(x, y - 1) } }.filterNotNull() }.flatten()
        val otr = board.mapIndexed { y, chars -> chars.mapIndexed { x, _ -> (x to y).takeIf { isTaken(x, y) && !isTaken(x + 1, y) && !isTaken(x, y - 1) } }.filterNotNull() }.flatten()
        val obl = board.mapIndexed { y, chars -> chars.mapIndexed { x, _ -> (x to y).takeIf { isTaken(x, y) && !isTaken(x - 1, y) && !isTaken(x, y + 1) } }.filterNotNull() }.flatten()
        val obr = board.mapIndexed { y, chars -> chars.mapIndexed { x, _ -> (x to y).takeIf { isTaken(x, y) && !isTaken(x + 1, y) && !isTaken(x, y + 1) } }.filterNotNull() }.flatten()
        val ctl = board.mapIndexed { y, chars -> chars.mapIndexed { x, _ -> (x to y).takeIf { isTaken(x, y) && isTaken(x - 1, y) && isTaken(x, y - 1) && !isTaken(x - 1, y - 1) } }.filterNotNull() }.flatten()
        val ctr = board.mapIndexed { y, chars -> chars.mapIndexed { x, _ -> (x to y).takeIf { isTaken(x, y) && isTaken(x + 1, y) && isTaken(x, y - 1) && !isTaken(x + 1, y - 1) } }.filterNotNull() }.flatten()
        val cbl = board.mapIndexed { y, chars -> chars.mapIndexed { x, _ -> (x to y).takeIf { isTaken(x, y) && isTaken(x - 1, y) && isTaken(x, y + 1) && !isTaken(x - 1, y + 1) } }.filterNotNull() }.flatten()
        val cbr = board.mapIndexed { y, chars -> chars.mapIndexed { x, _ -> (x to y).takeIf { isTaken(x, y) && isTaken(x + 1, y) && isTaken(x, y + 1) && !isTaken(x + 1, y + 1) } }.filterNotNull() }.flatten()

        val g = graphicEntityModule.createGroup()
        for ((x, y) in til) { graphicEntityModule.createRectangle().setFillColor(0xD9D9D9).setLineWidth(3.0).setLineColor(0xABCDEF).setX(x * 100).setY(y * 100).also { g.add(it); tiles[x to y] = it } }
        for ((x, y) in top) { graphicEntityModule.createSprite().setImage("side_t.png").setRotation(Math.toRadians(0.0)).setX(x * 100).setY(y * 100 - 15).also { g.add(it) } }
        for ((x, y) in rig) { graphicEntityModule.createSprite().setImage("side_t.png").setRotation(Math.toRadians(90.0)).setX(x * 100 + 115).setY(y * 100).also { g.add(it) } }
        for ((x, y) in bot) { graphicEntityModule.createSprite().setImage("side_t.png").setRotation(Math.toRadians(180.0)).setX(x * 100 + 100).setY(y * 100 + 115).also { g.add(it) } }
        for ((x, y) in lef) { graphicEntityModule.createSprite().setImage("side_t.png").setRotation(Math.toRadians(270.0)).setX(x * 100 - 15).setY(y * 100 + 100).also { g.add(it) } }
        for ((x, y) in otl) { graphicEntityModule.createSprite().setImage("corner_tl.png").setRotation(Math.toRadians(0.0)).setX(x * 100 - 15).setY(y * 100 - 15).also { g.add(it) } }
        for ((x, y) in otr) { graphicEntityModule.createSprite().setImage("corner_tl.png").setRotation(Math.toRadians(90.0)).setX(x * 100 + 115).setY(y * 100 - 15).also { g.add(it) } }
        for ((x, y) in obl) { graphicEntityModule.createSprite().setImage("corner_tl.png").setRotation(Math.toRadians(270.0)).setX(x * 100 - 15).setY(y * 100 + 115).also { g.add(it) } }
        for ((x, y) in obr) { graphicEntityModule.createSprite().setImage("corner_tl.png").setRotation(Math.toRadians(180.0)).setX(x * 100 + 115).setY(y * 100 + 115).also { g.add(it) } }
        for ((x, y) in ctl) { graphicEntityModule.createSprite().setImage("curve_tr.png").setRotation(Math.toRadians(0.0)).setX(x * 100 - 30).setY(y * 100 - 30).also { g.add(it) } }
        for ((x, y) in ctr) { graphicEntityModule.createSprite().setImage("curve_tr.png").setRotation(Math.toRadians(90.0)).setX(x * 100 + 130).setY(y * 100 - 30).also { g.add(it) } }
        for ((x, y) in cbl) { graphicEntityModule.createSprite().setImage("curve_tr.png").setRotation(Math.toRadians(270.0)).setX(x * 100 - 30).setY(y * 100 + 130).also { g.add(it) } }
        for ((x, y) in cbr) { graphicEntityModule.createSprite().setImage("curve_tr.png").setRotation(Math.toRadians(180.0)).setX(x * 100 + 130).setY(y * 100 + 130).also { g.add(it) } }

        val totalH = board.size * 100
        val totalW = board[0].size * 100
        val scaleY = minOf(1.0, 1080 / totalH.toDouble())
        val scaleX = minOf(1.0, 1920 / totalW.toDouble())
        val scale = minOf(scaleX, scaleY) - 0.1

        g.setScale(scale)
        g.setX((1920 - (scale * totalW).roundToInt()) / 2)
        g.setY((1080 - (scale * totalH).roundToInt()) / 2)

    }

    private fun visualize(tile: List<String>, row: Int, col: Int) {
        text.setText(
            "remaining tiles = ${remainingCharacters.toList().sorted().joinToString("")}\n" +
            "currently placing = ${remainingCharacters[0]}\n" +
            board.joinToString("\n") { it.joinToString(" ") }
        )

        for (dy in tile.indices) {
            for (dx in tile[0].indices) {
                if (tile[dy][dx] == '.') continue
                tiles[col + dx to row + dy]?.setFillColor(tileColors[remainingCharacters[0]]!!)
            }
        }
    }
}
