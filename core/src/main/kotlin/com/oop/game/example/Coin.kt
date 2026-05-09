package com.oop.game.example

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.oop.game.GameObject

class Coin(
    worldWidth: Float,
    worldHeight: Float
) : GameObject(
    x = MathUtils.random(0f, worldWidth - 15f),
    y = MathUtils.random(0f, worldHeight - 15f),
    width = 30f,
    height = 30f
) {
    private val texture = Texture(
        Gdx.files.internal(
            when {
                Gdx.files.internal("Coin.png").exists() -> "Coin.png"
                Gdx.files.internal("coin.png").exists() -> "coin.png"
                else -> "tile.png"
            }
        )
    )

    override fun update(delta: Float) {
    }

    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

    fun randomRespawn(worldWidth: Float, worldHeight: Float) {
        x = MathUtils.random(0f, worldWidth - width)
        y = MathUtils.random(0f, worldHeight - height)
    }

    fun getScore(): Int {
        return 1
    }

    override fun dispose() {
        texture.dispose()
    }
}
