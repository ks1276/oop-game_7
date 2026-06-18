package com.oop.game.example

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class Bullet(
    startX: Float,
    private val startY: Float, //? 1. startY를 기억하기 위해 private val 추가
    private val worldHeight: Float
) : Weapon(x = startX, y = startY,
    width = 15f, height = 30f,
    velocityX = 0f, velocityY = 600f) {
    private val texture = Texture(Gdx.files.internal("bullet.png"))

    private val maxDistance = 500f

    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }
    override fun aliveCondition(): Boolean {
        return (y - startY) < maxDistance
    }

    override fun dispose() {
        texture.dispose()
    }
}