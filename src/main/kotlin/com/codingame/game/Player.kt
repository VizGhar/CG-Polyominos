package com.codingame.game

import com.codingame.gameengine.core.AbstractSoloPlayer
import com.codingame.gameengine.core.SoloGameManager
import com.google.inject.Inject

class Player : AbstractSoloPlayer() {

    @Inject
    private lateinit var gameManager: SoloGameManager<Player>

    override fun getExpectedOutputLines() = gameManager.testCaseInput.first().split(" ").first().toInt()

}
