package com.oop.game.example // 본인의 패키지 경로에 맞게 확인해주세요! (예: com.oop.game)

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.GameObject // GameObject가 다른 패키지에 있다면 import 해줍니다.

/**
 * GameObject를 상속받아 구현한 총알 클래스
 */
class Bullet(
    startX: Float,
    startY: Float,
    private val worldHeight: Float
) : GameObject(
    x = startX,
    y = startY,
    width = 15f,   // 총알의 가로 크기
    height = 30f   // 총알의 세로 크기
) {

    // 1. 이미지 로딩: assets 폴더에 있는 "bullet.png"를 불러옵니다.
    private val texture = Texture(Gdx.files.internal("tile.png"))

    // 총알이 날아가는 속도 (초당 600 픽셀)
    private val speed = 600f

    // 2. 상태 갱신: 매 프레임마다 y 좌표를 증가시켜 위로 날아가게 합니다.
    override fun update(delta: Float) {
        y += speed * delta
    }

    // 3. 그리기: 부모 클래스에서 물려받은 x, y, width, height를 사용해 화면에 그립니다.
    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

    // 4. 생존 여부 (메모리 최적화): 총알이 화면 위쪽 밖으로 나가면 없애줍니다.
    override fun isAlive(): Boolean {
        // 현재 y 좌표가 화면 전체 높이보다 작을 때만 살아있음(true)
        return y < worldHeight
    }

    fun markHit() {
        y = worldHeight
    }

    // 5. 자원 해제: 총알이 사라질 때 이미지(Texture) 메모리도 같이 비워줍니다.
    override fun dispose() {
        texture.dispose()
    }
}
