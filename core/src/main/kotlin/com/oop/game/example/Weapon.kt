
//비행, 충돌, 사망판정 공통된 기능들을 묶어서 부모클래스 Weapon을 만든뒤 각각 상속
package com.oop.game.example

import com.oop.game.GameObject

abstract class Weapon(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    protected var velocityX: Float = 0f, // x축 이동 속도 (부모가 관리)
    protected var velocityY: Float = 0f  // y축 이동 속도 (부모가 관리)
) : GameObject(x, y, width, height) {

        protected var isDestroyed: Boolean = false

        protected open fun fly(delta: Float) {
        x += velocityX * delta
        y += velocityY * delta
    }

        override fun update(delta: Float) {
        fly(delta)
    }

    open fun markHit() {
        isDestroyed = true
    }


    override fun isAlive(): Boolean {
        if (isDestroyed) return false
        return checkAliveCondition()
    }

    // 자식 클래스마다 다른 고유 생존 조건이므로 함수로
    protected abstract fun checkAliveCondition(): Boolean
}