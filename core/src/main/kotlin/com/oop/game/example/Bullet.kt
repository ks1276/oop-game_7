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

    private val texture = Texture(
        Gdx.files.internal(
            when {
                Gdx.files.internal("bullet.png").exists() -> "bullet.png"
                Gdx.files.internal("image.png").exists() -> "image.png"
                else -> "tile.png"
            }
        )
    )
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
        batch.draw(texture, x, y, width, height)
    }

    override fun dispose() {
        texture.dispose()
    }
}
