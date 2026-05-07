package com.oop.game.example

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.GameObject

class Coin(
    x: Float,
    y: Float,
    private val fallSpeed: Float = 180f
) : GameObject(x, y, 18f, 18f) {

    private val texture = Texture(Gdx.files.internal("tile.png"))
    private var collected = false
    private var elapsedTime = 0f

    override fun update(delta: Float) {
        elapsedTime += delta
        y -= fallSpeed * delta
    }

    fun collect() {
        collected = true
    }

    override fun isAlive(): Boolean = !collected && y + height >= 0f

    override fun draw(batch: SpriteBatch) {
        val pulse = 0.85f + 0.15f * kotlin.math.sin(elapsedTime * 8f)
        batch.setColor(1f, pulse, 0f, 1f)
        batch.draw(texture, x, y, width, height)
        batch.setColor(1f, 1f, 1f, 1f)
    }

    override fun dispose() {
        texture.dispose()
    }
}
