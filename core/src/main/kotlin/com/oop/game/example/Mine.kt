//Grenade 상태 중간에 Waiting상태 , STRATEXPLOSION상태를 추가하여 Mine 으로 응용시킴
package com.oop.game.example

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.Gdx
import com.oop.game.GameObject

enum class MineCondition { FLYING, WAITING, STRATEXPLODING, EXPLODING, DONE }

class Mine(
    startX: Float, startY: Float
) : Weapon(
    x = startX, y = startY,
    width = 16f, height = 16f,
    velocityX = 0f, velocityY = 100f
) {
    private var condition = MineCondition.FLYING
    private var flyingTime = 0f
    private var stratExplodingTime = 0f
    private var explosionTime = 0f
    private val explosionSize = 150f

    private val mineTexture = Texture(Gdx.files.internal("grenade.png"))
    private val explosionTexture = Texture(Gdx.files.internal("explosion.png"))

    private fun explose() {
        condition = MineCondition.EXPLODING
        val center_x = x + (width / 2)
        val center_y = y + (height / 2)
        width = explosionSize
        height = explosionSize
        x = center_x - (explosionSize / 2)
        y = center_y - (explosionSize / 2)
    }

    override fun update(delta: Float) { //매프레임 계산만 맡겼음, 상태별 이러나는 행동들은 함수가처리
        when (condition) {
            MineCondition.FLYING -> {
                fly(delta)
                flyingTime += delta
                if (flyingTime >= 1f) condition = MineCondition.WAITING
            }
            MineCondition.WAITING -> {}
            MineCondition.STRATEXPLODING -> {
                stratExplodingTime += delta
                if (stratExplodingTime >= 0.5f) explose()
            }
            MineCondition.EXPLODING -> {
                explosionTime += delta
                if (explosionTime >= 0.5f) condition = MineCondition.DONE

            }
            MineCondition.DONE -> {}
        }
    }

    override fun collidesWith(other: GameObject): Boolean {
        if (condition == MineCondition.WAITING) {
            if (super.collidesWith(other)) condition = MineCondition.STRATEXPLODING
        }
        if (condition == MineCondition.EXPLODING) return super.collidesWith(other)
        return false
    }

    override fun aliveCondition(): Boolean {
        return condition != MineCondition.DONE
    }

    override fun draw(batch: SpriteBatch) {
        when (condition) {
            MineCondition.FLYING, MineCondition.WAITING, MineCondition.STRATEXPLODING -> {
                batch.draw(mineTexture, x, y, width, height)
            }
            MineCondition.EXPLODING -> batch.draw(explosionTexture, x, y, width, height)
            MineCondition.DONE -> {}
        }
    }

    override fun dispose() {
        mineTexture.dispose()
        explosionTexture.dispose()
    }
}

