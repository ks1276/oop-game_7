package com.oop.game.example

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.GameObject

/**
 * Health item that restores player HP when collected.
 *
 * @param x world X coordinate
 * @param y world Y coordinate
 */
class HealthItem(
    x: Float,
    y: Float
) : GameObject(x, y, 20f, 20f) {
    private val texture = Texture(Gdx.files.internal("tile.png"))
    private var elapsedTime = 0f

    override fun update(delta: Float) {
        elapsedTime += delta
    }

    override fun draw(batch: SpriteBatch) {
        val alpha = if ((elapsedTime % 0.5f) < 0.25f) 1f else 0.5f
        batch.setColor(0f, 1f, 0f, alpha)
        batch.draw(texture, x, y, width, height)
        batch.setColor(1f, 1f, 1f, 1f)
    }

    override fun dispose() {
        texture.dispose()
    }
}
