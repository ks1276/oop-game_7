
/*기존에 GameOject에있던 충돌판정 함수 open fun collidesWith(other: GameObject)
를 이용해서 수류탄 폭발시 적이 죽도록 만들었습니다. 기존 총알 코드에서 수류탄으로 발전시켜 추가했습니다.
 */
package com.oop.game.example

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.Gdx
import com.oop.game.GameObject

enum class GrenadeCondition { FLYING, EXPLODING, DONE }

class Grenade(
    startX: Float, startY: Float,
    velocityX: Float, velocityY: Float
) : Weapon(
    x = startX, y = startY,
    width = 16f, height = 16f,
    velocityX = velocityX, velocityY = velocityY
) {
    private var condition = GrenadeCondition.FLYING

    private var aliveTime = 0f
    private val exploseTime = 1.5f//??
    private val explosionDuration = 0.1f
    private val explosionSize = 150f

    private val grenadeTexture = Texture(Gdx.files.internal("grenade.png"))
    private val explosionTexture = Texture(Gdx.files.internal("explosion.png"))

    private fun explose() {
        condition = GrenadeCondition.EXPLODING

        //중심위치 조정
        var center_x = x+(width/2)
        var center_y = y+(height/2)
        width = explosionSize
        height = explosionSize
        x = center_x - (explosionSize/2)
        y = center_y - (explosionSize/2)
    }


    override fun update(delta: Float) {
        aliveTime += delta
        when (condition) {
            GrenadeCondition.FLYING -> {
                fly(delta)

                if (aliveTime >= exploseTime) explose()
            }
            GrenadeCondition.EXPLODING -> {
                if (aliveTime >= exploseTime + explosionDuration) {
                    condition = GrenadeCondition.DONE
                }
            }
            GrenadeCondition.DONE -> {}
        }
    }

    override fun collidesWith(other: GameObject): Boolean {
        if (condition == GrenadeCondition.EXPLODING) {
            return super.collidesWith(other)//무한루프때문에 부모collidesWith사용
        }
        return false
    }
    override fun aliveCondition(): Boolean {
        return condition != GrenadeCondition.DONE
    }

    override fun draw(batch: SpriteBatch) {
        when (condition) {
            GrenadeCondition.FLYING -> batch.draw(grenadeTexture, x, y, width, height)
            GrenadeCondition.EXPLODING -> batch.draw(explosionTexture, x, y, width, height)
            GrenadeCondition.DONE -> {}
        }
    }

    override fun dispose() {
        grenadeTexture.dispose()
        explosionTexture.dispose()
    }
}
