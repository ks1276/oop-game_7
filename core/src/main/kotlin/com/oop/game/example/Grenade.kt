
/*기존에 GameOject에있던 충돌판정 함수 open fun collidesWith(other: GameObject)
를 이용해서 수류탄 폭발시 적이 죽도록 만들었습니다. 기존 총알 코드에서 수류탄으로 발전시켜 추가했습니다.
 */
package com.oop.game.example

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.Gdx
import com.oop.game.GameObject

enum class GrenadeState {
    FLYING,
    EXPLODING,
    DONE
}

class Grenade(
    startX: Float,
    startY: Float,
    velocityX: Float,
    velocityY: Float
) : Weapon(
    x = startX,
    y = startY,
    width = 16f,
    height = 16f,
    velocityX = velocityX, // 입력받은 x 속도를 부모에게 전달
    velocityY = velocityY  // 입력받은 y 속도를 부모에게 전달
) {

    private var state = GrenadeState.FLYING

    private var timeAlive = 0f
    private val fuseTime = 1.5f
    private val explosionDuration = 0.1f
    private val explosionSize = 150f

    private val grenadeTexture = Texture(Gdx.files.internal("grenade.png"))
    private val explosionTexture = Texture(Gdx.files.internal("explosion.png"))

    private fun triggerExplosion() {
        state = GrenadeState.EXPLODING

        // 폭발 시 충돌 범위를 키우고 중심점 보정
        x = x + (width / 2) - (explosionSize / 2)
        y = y + (height / 2) - (explosionSize / 2)
        width = explosionSize
        height = explosionSize
    }

    override fun update(delta: Float) {
        timeAlive += delta

        when (state) {
            GrenadeState.FLYING -> {
                super.fly(delta)

                if (timeAlive >= fuseTime) {
                    triggerExplosion()
                }
            }
            GrenadeState.EXPLODING -> {
                if (timeAlive >= fuseTime + explosionDuration) {
                    state = GrenadeState.DONE
                }
            }
            GrenadeState.DONE -> {}
        }
    }

    override fun collidesWith(other: GameObject): Boolean {
        if (state == GrenadeState.EXPLODING) {
            return super.collidesWith(other)
        }
        return false
    }

    //수류탄은 충돌하더라도 폭발 지속시간 동안 남아있어야 하므로 즉시 파괴 처리를 무시함
    override fun markHit() {

    }

    //투사체 고유의 생존 조건: 상태가 DONE이 아닐 때만 살아있음
    override fun checkAliveCondition(): Boolean {
        return state != GrenadeState.DONE
    }

    override fun draw(batch: SpriteBatch) {
        when (state) {
            GrenadeState.FLYING -> batch.draw(grenadeTexture, x, y, width, height)
            GrenadeState.EXPLODING -> batch.draw(explosionTexture, x, y, width, height)
            GrenadeState.DONE -> {}
        }
    }

    override fun dispose() {
        grenadeTexture.dispose()
        explosionTexture.dispose()
    }
}
