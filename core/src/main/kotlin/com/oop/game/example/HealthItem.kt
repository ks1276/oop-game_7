package com.oop.game.example

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.GameObject

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *  회복 아이템 — 플레이어가 접하면 HP를 1 회복.
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *
 *  게임 월드에 고정된 위치에 나타나는 회복 아이템.
 *  플레이어가 이것과 충돌하면 HP가 1 증가한다.
 *  아이템은 수집 후 월드에서 사라진다.
 *
 * @param x 월드 좌표 X (왼쪽 아래 기준)
 * @param y 월드 좌표 Y (왼쪽 아래 기준)
 */
class HealthItem(
    x: Float,
    y: Float
) : GameObject(x, y, 20f, 20f) {

    // 아이템 이미지 (tile.png 를 재사용하거나 다른 이미지 사용 가능)
    private val texture = Texture(Gdx.files.internal("tile.png"))

    // 아이템이 생성된 후 경과 시간 — 깜빡임 이펙트에 사용
    private var elapsedTime = 0f

    override fun update(delta: Float) {
        elapsedTime += delta
        // 아이템을 계속 떨어뜨리거나 회전시키려면 여기 추가
    }

    /**
     * 아이템을 그린다. 녹색으로 틴트해서 다른 객체와 구분한다.
     */
    override fun draw(batch: SpriteBatch) {
        // 깜빡임 효과: 2초마다 투명도 변경
        val alpha = if ((elapsedTime % 0.5f) < 0.25f) 1f else 0.5f
        batch.setColor(0f, 1f, 0f, alpha)  // 녹색
        batch.draw(texture, x, y, width, height)
        batch.setColor(1f, 1f, 1f, 1f)  // 색상 리셋
    }

    override fun dispose() {
        texture.dispose()
    }
}
