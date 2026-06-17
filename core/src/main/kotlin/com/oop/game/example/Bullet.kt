package com.oop.game.example

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class Bullet(
    startX: Float,
    private val startY: Float, // 1. startY를 기억하기 위해 private val 추가
    private val worldHeight: Float
) : Weapon(
    x = startX,
    y = startY,
    width = 15f,
    height = 30f,
    velocityX = 0f,    // 총알은 x축으로는 이동x
    velocityY = 600f   // 총알은 y축으로만 이동
) {

    private val texture = Texture(Gdx.files.internal("bullet.png"))

    // 2. 총알이 날아갈 수 있는 최대 거리 설정 (원하는 수치로 조절하세요)
    private val maxDistance = 500f

    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

    // 3. 화면 끝 도달 판정에서 일정 거리 도달 판정으로 변경
    override fun checkAliveCondition(): Boolean {
        // 현재 y 좌표에서 시작 y 좌표를 뺀 값(=이동한 거리)이 maxDistance보다 작을 때만 생존
        return (y - startY) < maxDistance
    }

    override fun dispose() {
        texture.dispose()
    }
}