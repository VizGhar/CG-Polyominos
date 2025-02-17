package com.codingame.game

import com.codingame.gameengine.core.AbstractPlayer
import com.codingame.gameengine.core.AbstractReferee
import com.codingame.gameengine.core.SoloGameManager
import com.codingame.gameengine.module.entities.GraphicEntityModule
import com.google.inject.Inject

class Referee : AbstractReferee() {

    @Inject
    private lateinit var gameManager: SoloGameManager<Player>

    @Inject
    private val graphicEntityModule: GraphicEntityModule? = null

    override fun init() {
        // Initialize your game here.
    }

    override fun gameTurn(turn: Int) {
        val player = gameManager.player
        player.sendInputLine("input")
        player.execute()
        try {
            val outputs = player.outputs
            // Check validity of the player output and compute the new game state
        } catch (e: AbstractPlayer.TimeoutException) {
            gameManager.loseGame("Oh no")
        }
        if (turn == 10) {
            gameManager.winGame("Congrats!");
        }
    }
}
