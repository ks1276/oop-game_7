
//비행, 충돌, 사망판정 공통된 기능들을 묶어서 부모클래스 Weapon을 만든뒤 각각 상속
package com.oop.game.example

import com.oop.game.GameObject

abstract class Weapon(
    x: Float, y: Float,
    width: Float, height: Float,
    protected val velocityX: Float, protected val velocityY: Float
) : GameObject(x, y, width, height) {
    var isDestroyed: Boolean = false

    protected open fun fly(delta: Float) {
        x += velocityX * delta
        y += velocityY * delta
    }

    override fun update(delta: Float) {
        fly(delta)
    }


    /*AI코드
    open fun markHit() {
        isDestroyed = true
    }파괴와 맞았다는 것이 같은 성질이므로 makrHit 함수 반환값을 isDestoryed로*/


    override fun isAlive(): Boolean {
        if (isDestroyed) return false
        return aliveCondition()
    }
    protected abstract fun aliveCondition(): Boolean
}