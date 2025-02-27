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

    private val random = Random(0)

    @Inject
    private lateinit var gameManager: SoloGameManager<Player>

    @Inject
    private lateinit var graphicEntityModule: GraphicEntityModule

    private lateinit var allCharacters: String
    private lateinit var board: Array<CharArray>

    override fun init() {
        gameManager.firstTurnMaxTime = 5000
        gameManager.turnMaxTime = 50
        allCharacters = gameManager.testCaseInput.last()
        val (rows, _) = gameManager.testCaseInput.first().split(" ").map { it.toInt() }
        board = gameManager.testCaseInput.drop(1).take(rows).map { it.toCharArray() }.toTypedArray()
        initVisual()
    }

    data class PolyominoPlacement(val tileId: Char, val shape: List<String>, val row: Int, val col: Int)

    private var placements = mutableListOf<PolyominoPlacement>()

    override fun gameTurn(turn: Int) {
        if (turn == 1) {
            // input processing
            gameManager.player.sendInputLine(allCharacters.toList().sorted().joinToString(""))
            gameManager.player.sendInputLine("${board.size} ${board[0].size}")
            board.forEach { gameManager.player.sendInputLine(it.joinToString("")) }

            try {
                // execution
                gameManager.player.execute()

                // processing outputs
                val receivedBoard = gameManager.player.outputs.take(board.size)

                if (receivedBoard.any { it.length != board[0].size }) {
                    gameManager.loseGame("All lines should be exactly ${board[0].size} long")
                    return
                }

                allCharacters.filter { !receivedBoard.joinToString("").contains(it) }.takeIf { it.isNotEmpty() }?.let {
                    gameManager.loseGame("Polyominoes ${it.split("").filter { it.isNotEmpty() }.joinToString()} missing")
                    return
                }

                val preparedPlacements = allCharacters.map { tileId ->
                    val positions = receivedBoard.mapIndexed { y, s -> s.mapIndexedNotNull { x, c -> if (c == tileId) x to y else null } }.flatten()
                    val minRow = positions.minOf { it.second }
                    val minCol = positions.minOf { it.first }
                    val maxRow = positions.maxOf { it.second }
                    val maxCol = positions.maxOf { it.first }
                    val adjustedInput = (minRow..maxRow).map { y -> (minCol..maxCol).map { x -> if (positions.contains(x to y)) 'O' else '.' }.joinToString("") }
                    PolyominoPlacement(tileId, adjustedInput, minRow, minCol)
                }
                placements +=
                    preparedPlacements.filter { it.shape in (tileVariants[it.tileId] ?: emptyList()) }.shuffled(random) +
                    preparedPlacements.filter { it.shape !in (tileVariants[it.tileId] ?: emptyList()) }

            } catch (e: AbstractPlayer.TimeoutException) {
                gameManager.loseGame("Timeout - failed to provide ${board.size} lines describing final board")
                return
            }
        }

        val (tileId, adjustedInput, row, col) = placements.removeAt(0)

        if (adjustedInput !in (tileVariants[tileId] ?: throw IllegalStateException("Referee error, contact author"))) {
            gameManager.loseGame("Polyomino $tileId - incorrect shape")
            return
        }

        for (dy in adjustedInput.indices) {
            for (dx in adjustedInput[0].indices) {
                if (adjustedInput[dy][dx] == '.') continue
                if (dy + row >= board.size || dx + col >= board[0].size) {
                    gameManager.loseGame("Placing polyomino outside the board")
                    return
                }
                if (board[dy + row][dx + col] != 'O') {
                    visualize(adjustedInput, tileId, row, col)
                    gameManager.loseGame("Incorrect polyomino placement")
                    return
                }
                board[dy + row][dx + col] = tileId
            }
        }

        visualize(adjustedInput, tileId, row, col)

        if (placements.isEmpty()) {
            gameManager.winGame("Congratulations!")
        }
    }

    private val tiles = mutableMapOf<Pair<Int,Int>, Rectangle>()
    private val polyominoes = mutableMapOf<Char, Group>()
    private var baseX: Int = -1
    private var baseY: Int = -1
    private var step: Double = -1.0

    private fun initVisual() {
        graphicEntityModule.createSprite().setImage("background.jpg")

        val g = graphicEntityModule.createGroup()
        board.indices.forEach { y -> board[0].indices.forEach { x -> graphicEntityModule.createRectangle().setFillColor(if (board[y][x] == '.') 0xFFFFFF else 0xCCDDEE).setX(x * 100 + 6).setY(y * 100 + 6).setWidth(94).setHeight(94).also { g.add(it); tiles[x to y] = it } } }
        board[0].indices.forEach { x -> graphicEntityModule.createSprite().setImage("side_t.png").setRotation(Math.toRadians(0.0)).setX(x * 100 - 1).setY(-16).setScaleX(1.07).also { g.add(it) } }
        board[0].indices.forEach { x -> graphicEntityModule.createSprite().setImage("side_t.png").setRotation(Math.toRadians(180.0)).setX(x * 100 + 106).setY(board.size * 100 + 21).setScaleX(1.07).also { g.add(it) } }
        board.indices.forEach { y -> graphicEntityModule.createSprite().setImage("side_t.png").setRotation(Math.toRadians(90.0)).setX(board[0].size * 100 + 21).setY(y * 100 - 1).setScaleX(1.07).also { g.add(it) } }
        board.indices.forEach { y -> graphicEntityModule.createSprite().setImage("side_t.png").setRotation(Math.toRadians(270.0)).setX(-16).setY(y * 100 + 106).setScaleX(1.07).also { g.add(it) } }
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

        allCharacters.forEachIndexed { index, c ->
            val tileGroup = graphicEntityModule.createGroup()
            val tile = tileVariants[c] ?: throw IllegalStateException("Error 01 - please contact author via comment on contribution page")
            val color = tileColors[c] ?: throw IllegalStateException("Error 01 - please contact author via comment on contribution page")
            for (y in tile[0].indices) { for (x in tile[0][0].indices) { if (tile[0][y][x] != '.') tileGroup.add(graphicEntityModule.createRectangle().setWidth(100).setHeight(100).setFillColor(color).setLineColor(0x000000).setLineWidth(3.0).setX(x * 100).setY(y * 100)) } }
            polyominoes[c] = tileGroup.setX(50 + Random.nextInt(150)).setY(450 + Random.nextInt(100)).setZIndex(100 - index).setScale(scale).setRotation(Math.random() * 2 * Math.PI)
        }
    }

    private fun visualize(tile: List<String>, tileId: Char, row: Int, col: Int) {
        val polyomino = polyominoes[tileId] ?: throw IllegalStateException("Error 02 - please contact author via comment on contribution page")
        val configuration = tileVariants[tileId]?.indexOf(tile) ?: throw IllegalStateException("Error 03 - please contact author via comment on contribution page")

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
