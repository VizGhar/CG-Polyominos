package com.codingame.game

import com.codingame.gameengine.core.AbstractPlayer
import com.codingame.gameengine.core.AbstractReferee
import com.codingame.gameengine.core.SoloGameManager
import com.codingame.gameengine.module.entities.GraphicEntityModule
import com.codingame.gameengine.module.entities.Group
import com.codingame.gameengine.module.entities.Rectangle
import com.google.inject.Inject
import kotlin.math.roundToInt
import kotlin.random.Random

val tileVariants = mapOf(
    'A' to (listOf(listOf("O..", "OOO"), listOf("OO", "O.", "O."), listOf("OOO", "..O"), listOf(".O", ".O", "OO"), listOf("..O", "OOO"), listOf("O.", "O.", "OO"), listOf("OOO", "O.."), listOf("OO", ".O", ".O"))),
    'B' to (listOf(listOf("O.O", "OOO"), listOf("OO", "O.", "OO"), listOf("OOO", "O.O"), listOf("OO", ".O", "OO"), listOf("O.O", "OOO"), listOf("OO", "O.", "OO"), listOf("OOO", "O.O"), listOf("OO", ".O", "OO"))),
    'C' to (listOf(listOf(".O.", "OOO", ".O."), listOf(".O.", "OOO", ".O."), listOf(".O.", "OOO", ".O."), listOf(".O.", "OOO", ".O."), listOf(".O.", "OOO", ".O."), listOf(".O.", "OOO", ".O."), listOf(".O.", "OOO", ".O."), listOf(".O.", "OOO", ".O."))),
    'D' to (listOf(listOf(".O.", "OOO"), listOf("O.", "OO", "O."), listOf("OOO", ".O."), listOf(".O", "OO", ".O"), listOf(".O.", "OOO"), listOf("O.", "OO", "O."), listOf("OOO", ".O."), listOf(".O", "OO", ".O"))),
    'E' to (listOf(listOf(".O.", ".O.", "OOO"), listOf("O..", "OOO", "O.."), listOf("OOO", ".O.", ".O."), listOf("..O", "OOO", "..O"), listOf(".O.", ".O.", "OOO"), listOf("O..", "OOO", "O.."), listOf("OOO", ".O.", ".O."), listOf("..O", "OOO", "..O"))),
    'F' to (listOf(listOf("OOOO"), listOf("O", "O", "O", "O"), listOf("OOOO"), listOf("O", "O", "O", "O"), listOf("OOOO"), listOf("O", "O", "O", "O"), listOf("OOOO"), listOf("O", "O", "O", "O"))),
    'G' to (listOf(listOf("OO.", ".OO", "..O"), listOf("..O", ".OO", "OO."), listOf("O..", "OO.", ".OO"), listOf(".OO", "OO.", "O.."), listOf(".OO", "OO.", "O.."), listOf("OO.", ".OO", "..O"), listOf("..O", ".OO", "OO."), listOf("O..", "OO.", ".OO"))),
    'H' to (listOf(listOf(".O", "OO"), listOf("O.", "OO"), listOf("OO", "O."), listOf("OO", ".O"), listOf("O.", "OO"), listOf("OO", "O."), listOf("OO", ".O"), listOf(".O", "OO"))),
    'I' to (listOf(listOf("OO", ".O", "OO", "O."), listOf("OO.O", ".OOO"), listOf(".O", "OO", "O.", "OO"), listOf("OOO.", "O.OO"), listOf("OO", "O.", "OO", ".O"), listOf(".OOO", "OO.O"), listOf("O.", "OO", ".O", "OO"), listOf("O.OO", "OOO."))),
    'J' to (listOf(listOf("OOO", "..O", "..O"), listOf("..O", "..O", "OOO"), listOf("O..", "O..", "OOO"), listOf("OOO", "O..", "O.."), listOf("OOO", "O..", "O.."), listOf("OOO", "..O", "..O"), listOf("..O", "..O", "OOO"), listOf("O..", "O..", "OOO"))),
    'K' to (listOf(listOf("OO", "OO"), listOf("OO", "OO"), listOf("OO", "OO"), listOf("OO", "OO"), listOf("OO", "OO"), listOf("OO", "OO"), listOf("OO", "OO"), listOf("OO", "OO"))),
    'L' to (listOf(listOf(".OO", ".O.", "OO."), listOf("O..", "OOO", "..O"), listOf(".OO", ".O.", "OO."), listOf("O..", "OOO", "..O"), listOf("OO.", ".O.", ".OO"), listOf("..O", "OOO", "O.."), listOf("OO.", ".O.", ".OO"), listOf("..O", "OOO", "O.."))),
    'M' to (listOf(listOf(".OO", "OO."), listOf("O.", "OO", ".O"), listOf(".OO", "OO."), listOf("O.", "OO", ".O"), listOf("OO.", ".OO"), listOf(".O", "OO", "O."), listOf("OO.", ".OO"), listOf(".O", "OO", "O."))),
    'N' to (listOf(listOf("OOO", "OO."), listOf("OO", "OO", ".O"), listOf(".OO", "OOO"), listOf("O.", "OO", "OO"), listOf("OOO", ".OO"), listOf(".O", "OO", "OO"), listOf("OO.", "OOO"), listOf("OO", "OO", "O.")))
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

    private lateinit var allCharacters: String
    private lateinit var remainingCharacters: String
    private lateinit var board: Array<CharArray>

    override fun init() {
        gameManager.firstTurnMaxTime = 2000
        gameManager.turnMaxTime = 50
        allCharacters = gameManager.testCaseInput.last()
        remainingCharacters = allCharacters
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
            val positions = gameManager.player.outputs[0].split(" ").map { it.substringAfter("(").substringBefore(")").split(",").map { it.toInt() } }
            val row = positions.minOf { it[0] }
            val col = positions.minOf { it[1] }
            val maxRow = positions.maxOf { it[0] }
            val maxCol = positions.maxOf { it[1] }
            val adjustedInput = (row..maxRow).map { y -> (col..maxCol).map { x -> if (positions.contains(listOf(y, x))) 'O' else '.'}.joinToString("") }

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

    private val tiles = mutableMapOf<Pair<Int,Int>, Rectangle>()
    private val polyominos = mutableMapOf<Int, Group>()
    private var baseX: Int = -1
    private var baseY: Int = -1
    private var step: Double = -1.0

    private fun initVisual() {
        graphicEntityModule.createSprite().setImage("background.jpg")

        val g = graphicEntityModule.createGroup()
        board.indices.forEach { y -> board[0].indices.forEach { x -> graphicEntityModule.createRectangle().setFillColor(if (board[y][x] == '.') 0xFFFFFF else 0xCCDDEE).setX(x * 100 + 6).setY(y * 100 + 6).setWidth(94).setHeight(94).also { g.add(it); tiles[x to y] = it } } }
        board[0].indices.forEach { x -> graphicEntityModule.createSprite().setImage("side_t.png").setRotation(Math.toRadians(0.0)).setX(x * 100).setY(-16).setScaleX(1.06).also { g.add(it) } }
        board[0].indices.forEach { x -> graphicEntityModule.createSprite().setImage("side_t.png").setRotation(Math.toRadians(180.0)).setX(x * 100 + 106).setY(board.size * 100 + 21).setScaleX(1.06).also { g.add(it) } }
        board.indices.forEach { y -> graphicEntityModule.createSprite().setImage("side_t.png").setRotation(Math.toRadians(90.0)).setX(board[0].size * 100 + 21).setY(y * 100).setScaleX(1.06).also { g.add(it) } }
        board.indices.forEach { y -> graphicEntityModule.createSprite().setImage("side_t.png").setRotation(Math.toRadians(270.0)).setX(-16).setY(y * 100 + 106).setScaleX(1.06).also { g.add(it) } }
        graphicEntityModule.createSprite().setImage("corner_tl.png").setRotation(Math.toRadians(0.0)).setX(-16).setY(-16).also { g.add(it) }
        graphicEntityModule.createSprite().setImage("corner_tl.png").setRotation(Math.toRadians(90.0)).setX(board[0].size * 100 + 21).setY(-16).also { g.add(it) }
        graphicEntityModule.createSprite().setImage("corner_tl.png").setRotation(Math.toRadians(270.0)).setX(-16).setY(board.size * 100 + 21).also { g.add(it) }
        graphicEntityModule.createSprite().setImage("corner_tl.png").setRotation(Math.toRadians(180.0)).setX(board[0].size * 100 + 21).setY(board.size * 100 + 21).also { g.add(it) }

        val totalH = board.size * 100
        val totalW = board[0].size * 100
        val scaleY = minOf(1.0, 1080 / totalH.toDouble())
        val scaleX = minOf(1.0, 1920 / totalW.toDouble())
        val scale = minOf(scaleX, scaleY) - 0.1

        baseX = (1920 - (scale * totalW).roundToInt()) / 2
        baseY = (1080 - (scale * totalH).roundToInt()) / 2
        step = (scale * 100)
        g.setScale(scale).setX(baseX).setY(baseY)

        remainingCharacters.forEachIndexed { index, c ->
            val tileGroup = graphicEntityModule.createGroup()
            val tile = tileVariants[c] ?: throw IllegalStateException("Error 01 - please contact author via comment on contribution page")
            val color = tileColors[c] ?: throw IllegalStateException("Error 01 - please contact author via comment on contribution page")
            for (y in tile[0].indices) { for (x in tile[0][0].indices) { if (tile[0][y][x] != '.') tileGroup.add(graphicEntityModule.createRectangle().setWidth(100).setHeight(100).setFillColor(color).setLineColor(0x000000).setLineWidth(3.0).setX(x * 100).setY(y * 100)) } }
            polyominos[index] = tileGroup.setX(50 + Random.nextInt(150)).setY(450 + Random.nextInt(100)).setZIndex(100 - index).setScale(scale).setRotation(Math.random() * 2 * Math.PI)
        }
    }

    private fun visualize(tile: List<String>, row: Int, col: Int) {
        val polyomino = polyominos[allCharacters.indexOf(remainingCharacters[0])] ?: throw IllegalStateException("Error 02 - please contact author via comment on contribution page")
        val configuration = tileVariants[remainingCharacters[0]]?.indexOf(tile) ?: throw IllegalStateException("Error 03 - please contact author via comment on contribution page")
        val flip = configuration / 4 == 1
        val rightRotations = configuration % 4

        var dx = if (rightRotations in 1..2) tile[0].length else 0
        var dy = if (rightRotations in 2..3 && !flip) tile.size else 0

        if (flip && rightRotations == 0) dx += tile[0].length
        if (flip && rightRotations == 1) dy += tile.size
        if (flip && rightRotations == 2) { dx -= tile[0].length; dy += tile.size }

        polyomino
            .setScaleX(polyomino.scaleX * if (flip) -1.0 else 1.0)
            .setRotation(Math.toRadians(90.0 * rightRotations))
            .setX(3 + baseX + ((col + dx) * step).roundToInt())
            .setY(3 + baseY + ((row + dy) * step).roundToInt())
        graphicEntityModule.commitWorldState(0.99)
        polyomino.setZIndex(1)
    }
}
