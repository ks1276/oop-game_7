package com.oop.game.example

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.GameObject

class Bullet(
    x: Float,
    y: Float,
    private val worldHeight: Float
) : GameObject(x, y, 8f, 18f) {

    private val texture = Texture(Gdx.files.internal("tile.png"))
    private val speed = 420f
    private var hit = false

    override fun update(delta: Float) {
        y += speed * delta
    }

    fun markHit() {
        hit = true
    }

    override fun isAlive(): Boolean = !hit && y <= worldHeight

    override fun draw(batch: SpriteBatch) {
        batch.setColor(1f, 0.2f, 0.1f, 1f)
        batch.draw(texture, x, y, width, height)
        batch.setColor(1f, 1f, 1f, 1f)
    }

    override fun dispose() {
        texture.dispose()
    }
}
