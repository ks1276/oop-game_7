
/*기존에 GameOject에있던 충돌판정 함수 open fun collidesWith(other: GameObject)
를 이용해서 수류탄 폭발시 적이 죽도록 만들었습니다. 기존 총알 코드에서 수류탄으로 발전시켜 추가했습니다.
 */
package com.oop.game.example

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.Gdx
import com.oop.game.GameObject
// 수류탄의 상태 enum class 로 표현
enum class GrenadeState {
    FLYING, // 날아가는 중
    EXPLODING, // 폭발 중 (충돌 발생시)
    DONE // 소멸
}

// 수류탄 비행
class Grenade(
    startX: Float,
    startY: Float,
    private val velocityX: Float, // x축 이동 속도
    private val velocityY: Float // y축 이동 속도
) : GameObject(startX, startY, 16f, 16f) {

    // 수류탄 던져서 날고 있는 상태임
    private var state = GrenadeState.FLYING

    // 타이머와 설정값들
    private var timeAlive = 0f
    private val fuseTime = 1.5f  // 폭발까지 시간
    private val explosionDuration = 0.1f // 폭발지속시간
    private val explosionSize = 150f // 폭발 시 충돌 범위 (가로세로 150 픽셀)

    // 수류탄 그림과 폭발시 그림 코드
    private val grenadeTexture = Texture(Gdx.files.internal("grenade.png"))
    private val explosionTexture = Texture(Gdx.files.internal("explosion.png"))

    /* 수류탄 폭발을 위해서 기능함수에 살아있는 시간과 수류탄의 비행속도,
     비행시간이 끝나면 폭발상태로 바뀌는 기능, 폭발 하고 사라지는 기능 추가 */
    // 폭발을 구현하는 함수
    private fun triggerExplosion() {
        state = GrenadeState.EXPLODING

        /* 폭발시 수류탄 크기 키워야하는데 원점방향기준으로 커지므로 중심위치를 폭발범위 가운데로 재설정 */
        x = x + (width / 2) - (explosionSize / 2)
        y = y + (height / 2) - (explosionSize / 2)
        width = explosionSize
        height = explosionSize
    }

    override fun update(delta: Float) {
        timeAlive += delta

        when (state) {
            GrenadeState.FLYING -> {

                x += velocityX * delta
                y += velocityY * delta

                // 1.5초 지나면 폭발
                if (timeAlive >= fuseTime) {
                    triggerExplosion()
                }
            }
            GrenadeState.EXPLODING -> {
                // 폭발 지속시간이 끝나면 소멸상태로
                if (timeAlive >= fuseTime + explosionDuration) {
                    state = GrenadeState.DONE
                }
            }
            GrenadeState.DONE -> {
                // 수류탄이 사라져야하는 내용은 뒤에 isAlive함수에서 구현
            }
        }
    }



    /* 오직 폭발 중일 때만 충돌 판정이 일어나도록 조건문 걸어줌 */
    override fun collidesWith(other: GameObject): Boolean {
        if (state == GrenadeState.EXPLODING) {
            return super.collidesWith(other)/*??collidesWith(other)로하면 다시
            override collidesWith(other)로 올라가서 무한루프 반복->super.collidesWith*/
        }
        return false
    }

    /* 상태가 DONE이 되면 false를 반환하여 월드에서 삭제되도록 함 */
    override fun isAlive(): Boolean {
        return state != GrenadeState.DONE
    }

    override fun draw(batch: SpriteBatch) {
        when (state) {
            GrenadeState.FLYING -> {
                batch.draw(grenadeTexture, x, y, width, height)
            }
            GrenadeState.EXPLODING -> {
                batch.draw(explosionTexture, x, y, width, height)
            }
            GrenadeState.DONE -> {} // 사진 존재x
        }
    }

    override fun dispose() {
        grenadeTexture.dispose()
        explosionTexture.dispose()
    }
}

