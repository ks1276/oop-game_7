package com.oop.game.example

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch


class Bullet(
    startX: Float,
    startY: Float,
    private val worldHeight: Float
) : Weapon(
    x = startX,
    y = startY,
    width = 15f,
    height = 30f,
    velocityX = 0f,    //총알은 x축으로는 이동x
    velocityY = 600f   // 총알은 y축으로만 이동
) {

    private val texture = Texture(Gdx.files.internal("bullet.png"))

    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

        override fun checkAliveCondition(): Boolean {
        return y < worldHeight
    }

    override fun dispose() {
        texture.dispose()
    }
}