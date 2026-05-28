package com.oop.game.example

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.GameWorld
import com.oop.game.InputHandler
import kotlin.math.floor

/**
 * ════════════════════════════════════════════════════════════
 *  게임 월드 예제 — Player vs Enemy 회피 게임 (이미지 사용).
 * ════════════════════════════════════════════════════════════
 *
 *  GameWorld 를 상속해 만든 가장 작은 플레이 가능한 예제.
 *  학생은 이 파일을 참고해서 자기만의 월드를 만들면 된다.
 *
 *  ── 조작법 ──
 *   ▸ 화살표 키  : 플레이어 이동
 *   ▸ WASD      : 카메라 이동 (월드가 화면보다 커서 탐험 가능)
 *   ▸ ESC       : 게임 오버 후 종료
 *
 *  ── 사용 이미지 (core/src/main/resources/) ──
 *   ▸ player.png  — 30x30 플레이어 스프라이트
 *   ▸ enemy.png   — 40x40 적 스프라이트
 *   ▸ tile.png    — 64x64 흰색 정사각형 (체스판 배경에 색만 입혀 사용)
 *
 *  ── 게임 상태 ──
 *   IN_PLAY   : 일반 진행 (이동·충돌 체크)
 *   GAME_OVER : 충돌 후 정지, ESC 입력 대기
 *
 *  ── 텍스트 데모 ──
 *   ▸ 좌측 상단 "HP: 3"       — 화면 좌표 (카메라 움직여도 고정)
 *   ▸ 월드 중앙 "WORLD CENTER" — 월드 좌표 (카메라와 함께 이동)
 *   두 개를 같이 두어, 두 좌표계의 차이를 눈으로 확인할 수 있게 했다.
 *
 *  ── 배경 ──
 *   tile.png(흰 사각형)를 두 가지 색으로 틴트해 체스판처럼 깐다.
 *   카메라 이동을 눈으로 보여주기 위함이다.
 *   GameWorld.drawBackground(batch) 를 override 해서 그린다.
 *
 * @param screenWidth  화면에 보이는 영역 너비
 * @param screenHeight 화면에 보이는 영역 높이
 * @param worldWidth   월드 전체 너비 (화면보다 크면 WASD 로 탐험 가능)
 * @param worldHeight  월드 전체 높이
 */
class ExampleWorld(
    screenWidth: Float,
    screenHeight: Float,
    worldWidth: Float,
    worldHeight: Float
) : GameWorld(screenWidth, screenHeight, worldWidth, worldHeight) {

    /**
     * 게임의 현재 상태를 나타내는 열거형.
     *
     * Boolean 깃발(isGameOver) 대신 enum 을 쓰는 이유:
     *   ▸ 상태 가짓수가 늘어날 때 깔끔히 확장 가능 (예: PAUSED, MENU, VICTORY)
     *   ▸ when 으로 분기하면 'else' 없이 모든 상태를 다뤘는지 컴파일러가 체크해줌
     *   ▸ 코드를 읽을 때 "이 게임에 어떤 상태들이 있는가" 가 한눈에 보임
     *   (7주차에서 배우는 enum class 의 전형적 활용)
     */
    private enum class GameState {
        IN_PLAY,
        GAME_OVER,
        VICTORY
    }

    // 플레이어 — 월드 중앙 하단에서 시작.
    //   월드 크기를 함께 넘겨서, 경계 밖으로 못 나가게 한다.
    private val player = ExamplePlayer(
        x = worldWidth / 2 - 15f,   // 가로 30 의 절반을 빼서 정확히 중앙
        y = 50f,
        worldWidth = worldWidth,
        worldHeight = worldHeight
    )

    // 여러 개의 적들 — 다양한 높이와 위치에서 왕복
    private val enemies = mutableListOf(
        ExampleEnemy(x = 100f, y = worldHeight - 100f, minX = 0f, maxX = worldWidth),
        ExampleEnemy(x = 300f, y = worldHeight - 200f, minX = 0f, maxX = worldWidth),
        ExampleEnemy(x = 500f, y = worldHeight - 150f, minX = 0f, maxX = worldWidth),
        ExampleEnemy(x = 700f, y = worldHeight - 250f, minX = 0f, maxX = worldWidth)
    )

    // 회복 아이템들 — 월드 각 곳에 배치
    private val healthItems = listOf(
        HealthItem(x = 200f, y = worldHeight - 300f),
        HealthItem(x = 600f, y = worldHeight - 400f),
        HealthItem(x = 400f, y = worldHeight - 500f)
    )

    private val coin = Coin(worldWidth, worldHeight)
    private val bullets = mutableListOf<Bullet>()
    private val grenades = mutableListOf<Grenade>() //!! 수류탄 리스트 추가
    private val targetCoinCount = 100
    private var coinCount = 0

    // 현재 게임 상태 — 입력/충돌에 따라 IN_PLAY ↔ GAME_OVER 로 전환된다.
    private var state = GameState.IN_PLAY

    // ── 체스판 배경 설정 (drawBackground() 에서 사용) ──
    //   이게 없으면 검은 배경뿐이라 카메라(WASD) 이동이 눈에 안 보인다.
    //   학생은 자기 게임에선 다른 배경을 그리거나, 그냥 두면 검은 배경이다.
    //
    //   tile.png 는 흰색 64x64 정사각형 한 장. 같은 텍스처에 batch.color 를
    //   바꿔가며 두 가지 색으로 그리는 트릭(틴트) 으로 체스판을 만든다.
    private val tileTexture = Texture(Gdx.files.internal("tile.png"))
    private val bgColorDark = Color(0.08f, 0.08f, 0.08f, 1f)
    private val bgColorLight = Color(0.15f, 0.15f, 0.15f, 1f)
    private val tileSize = 64f

    /**
     * 생성자 본문 — 월드에 플레이어, 적들, 아이템들을 등록한다.
     *   이렇게 등록해야 update / draw 루프에 포함된다.
     */
    init {
        add(player)
        // 모든 적 등록
        for (enemy in enemies) {
            add(enemy)
        }
        // 모든 회복 아이템 등록
        for (item in healthItems) {
            add(item)
        }
        add(coin)
    }

    /**
     * 매 프레임 게임 로직 — 모든 '입력 처리·상태 변경' 은 이 안에서.
     *
     * 상태별로 해야 할 일이 완전히 다르므로 when 으로 분기한다.
     * (입력 처리가 render() 가 아닌 update() 에 있는 이유:
     *  '로직과 그리기의 분리' — render 는 매 프레임 그리는 일에만 집중하고,
     *  상태 변화·입력은 update 가 책임진다.)
     */
    override fun update(delta: Float) {
        when (state) {
            GameState.IN_PLAY -> updateInPlay(delta)
            GameState.GAME_OVER -> updateGameOver()
            GameState.VICTORY -> updateVictory()
        }
    }

    /** IN_PLAY 상태에서 매 프레임 처리 — 카메라 이동, 객체 갱신, 충돌 체크. */
    private fun updateInPlay(delta: Float) {
        // ── 카메라 이동 (WASD) ──
        //   offsetX/Y 를 바꾸면 카메라가 월드 안에서 움직인다.
        val cameraSpeed = 200f * delta
        if (InputHandler.isKeyPressed(InputHandler.W)) offsetY += cameraSpeed
        if (InputHandler.isKeyPressed(InputHandler.S)) offsetY -= cameraSpeed
        if (InputHandler.isKeyPressed(InputHandler.A)) offsetX -= cameraSpeed
        if (InputHandler.isKeyPressed(InputHandler.D)) offsetX += cameraSpeed

        // 카메라가 월드 경계 밖을 보여주지 않도록 clamp.
        //   보여주는 영역이 [offset, offset+screen] 이어야 하므로
        //   offset 은 0 ~ (world - screen) 범위여야 한다.
        offsetX = offsetX.coerceIn(0f, worldWidth - screenWidth)
        offsetY = offsetY.coerceIn(0f, worldHeight - screenHeight)

        // ── 1) 게임 객체 갱신 — 각자 한 프레임씩 진행 ──
        fireBullet()
        fireGrenade() //!! 수류탄 발사 함수 호출 추가
        updateAllObjects(delta)

        // ── 2) 충돌 감시 ──
        
        // 2-1) 적과의 충돌 — hp 1씩 감소
        for (enemy in enemies) {
            if (player.collidesWith(enemy)) {
                player.takeDamage()
            }
        }

        // 2-2) 회복 아이템과의 충돌 — hp 1씩 회복, 아이템 제거
        val bulletsToRemove = mutableListOf<Bullet>()
        val enemiesToRemove = mutableListOf<ExampleEnemy>()
        for (bullet in bullets) {
            for (enemy in enemies) {
                if (bullet.collidesWith(enemy)) {
                    bullet.markHit()
                    enemy.takeDamage()
                    bulletsToRemove.add(bullet)
                    if (!enemy.isAlive()) {
                        enemiesToRemove.add(enemy)
                    }
                    break
                }
            }
        }
        for (bullet in bulletsToRemove) {
            remove(bullet)
        }

        //!! 수류탄과 적 충돌 처리 로직 추가 시작
        val grenadesToRemove = mutableListOf<Grenade>()
        for (grenade in grenades) {
            for (enemy in enemies) {
                if (grenade.collidesWith(enemy)) {
                    enemy.takeDamage()
                    if (!enemy.isAlive()) {
                        enemiesToRemove.add(enemy)
                    }
                }
            }
        }
        //!! 수류탄과 적 충돌 처리 로직 추가 끝

        for (enemy in enemiesToRemove) {
            remove(enemy)
        }

        val itemsToRemove = mutableListOf<HealthItem>()
        for (item in healthItems) {
            if (player.collidesWith(item)) {
                player.heal()
                itemsToRemove.add(item)
            }
        }
        for (item in itemsToRemove) {
            remove(item)
        }

        if (player.collidesWith(coin)) {
            coinCount += coin.getScore()
            coin.randomRespawn(worldWidth, worldHeight)
        }
        if (coinCount >= targetCoinCount) {
            coinCount = targetCoinCount
            state = GameState.VICTORY
        }

        // 2-3) 플레이어의 hp 체크 — 0이 되면 게임 오버
        if (!player.isAlive()) {
            state = GameState.GAME_OVER
        }

        // ── 3) 죽은 객체 정리 ──
        //   현재 예제에선 아무 것도 안 죽으므로 영향 없지만,
        //   bullet/enemy 가 추가될 때를 대비한 표준 흐름이다.
        removeDead()
        bullets.removeAll { !it.isAlive() }
        grenades.removeAll { !it.isAlive() } //!! 죽은 수류탄 리스트에서 정리
        enemies.removeAll { !it.isAlive() }
    }

    private fun fireBullet() {
        if (InputHandler.isKeyJustPressed(InputHandler.SPACE)) {
            val bulletX = player.x + (player.width / 2) - 7.5f
            val bulletY = player.y + player.height
            val bullet = Bullet(
                startX = bulletX,
                startY = bulletY,
                worldHeight = worldHeight
            )
            bullets.add(bullet)
            add(bullet)
        }
    }

    private fun fireGrenade() { //!! 수류탄 발사 함수 추가
        if (InputHandler.isKeyJustPressed(InputHandler.G)) { //!! G키 입력 확인
            val grenadeX = player.x + (player.width / 2) - 8f
            val grenadeY = player.y + player.height
            val grenade = Grenade(
                startX = grenadeX,
                startY = grenadeY,
                velocityX = 0f, //!! 위로 날아가도록 설정
                velocityY = 300f //!! 위로 날아가는 속도
            )
            grenades.add(grenade) //!! 리스트에 추가
            add(grenade) //!! 월드에 추가
        }
    }

    /** GAME_OVER 상태에서 매 프레임 처리 — ESC 입력만 감시한다. */
    private fun updateGameOver() {
        // ESC 키가 '막 눌린 순간' 앱 종료.
        //   isKeyJustPressed 로 한 이유: 누르고 있는 동안 매 프레임 exit 호출되지 않게.
        if (InputHandler.isKeyJustPressed(InputHandler.ESCAPE)) {
            Gdx.app.exit()
        }
    }

    private fun updateVictory() {
        if (InputHandler.isKeyJustPressed(InputHandler.ESCAPE)) {
            Gdx.app.exit()
        }
    }

    /**
     * 배경 그리기 — GameWorld.drawBackground(batch) 를 override.
     *
     * 부모가 이미 batch.begin() 을 호출한 상태에서 이 함수를 부르므로,
     * 여기선 batch.draw() 호출만 하면 된다. (begin/end 를 또 부르면 안 된다)
     *
     * 카메라(offset) 에 따라 타일 위치가 바뀌어 이동감을 준다.
     *   타일 인덱스 자체는 월드 좌표 격자에서 변하지 않지만,
     *   각 타일을 그릴 때 offset 만큼 빼서 화면 좌표로 변환한다.
     *
     * 색을 입히는 방법:
     *   batch.color 를 바꾼 뒤 batch.draw 하면 텍스처가 그 색으로 곱해져 그려진다.
     *   tile.png 가 흰색이라 어떤 색이든 그대로 적용된다.
     *   끝에 다시 흰색으로 되돌려두지 않으면 그 다음 그리는 것까지 영향을 받으니 주의.
     */
    override fun drawBackground(batch: SpriteBatch) {
        // 현재 카메라 시작점이 속한 타일 인덱스 (여유분으로 -1)
        val startCol = floor(offsetX / tileSize).toInt() - 1
        val startRow = floor(offsetY / tileSize).toInt() - 1
        // 화면을 채우는 데 필요한 타일 개수 (여유분 +3)
        val cols = (screenWidth / tileSize).toInt() + 3
        val rows = (screenHeight / tileSize).toInt() + 3

        for (row in startRow until startRow + rows) {
            for (col in startCol until startCol + cols) {
                // 행+열이 짝수면 어둡게, 홀수면 밝게 → 체스판 패턴
                batch.color = if ((row + col) % 2 == 0) bgColorDark else bgColorLight

                // 월드 좌표의 타일 위치에서 offset 만큼 빼면 화면 좌표
                val drawX = col * tileSize - offsetX
                val drawY = row * tileSize - offsetY
                batch.draw(tileTexture, drawX, drawY, tileSize, tileSize)
            }
        }

        // 배경에 입힌 색이 다음 그리기(게임 객체)에 영향을 주지 않도록 흰색으로 복원.
        batch.color = Color.WHITE
    }

    /**
     * 매 프레임 그리기 — 부모가 배경·객체까지 그려준 뒤, 텍스트 UI 를 얹는다.
     *
     * 이 함수에서는 '그리기' 만 한다. 입력 처리·상태 변경은 update() 의 책임.
     *
     * 주의: super.render(delta) 가 화면 clear + 배경 + 객체까지 그리므로,
     *       텍스트는 반드시 super 호출 **이후** 그려야 가려지지 않는다.
     */
    override fun render(delta: Float) {
        super.render(delta)

        // ── 항상 보이는 UI ──
        drawHud()

        // ── 상태별로 그리는 것이 다름 ──
        when (state) {
            GameState.IN_PLAY -> {
                // 플레이 중에는 추가로 그릴 것 없음
            }
            GameState.GAME_OVER -> drawGameOverOverlay()
            GameState.VICTORY -> drawVictoryOverlay()
        }
    }

    /** 항상 화면에 표시되는 정보 — HP 표시와 월드 중앙 표지. */
    private fun drawHud() {
        // 1) UI 텍스트 (화면 고정) — 좌측 상단 HP 표시.
        //    카메라가 움직여도 항상 이 위치에 있다.
        drawTextOnScreen(
            text = "HP: ${player.hp}",
            x = 10f,
            y = screenHeight - 10f,   // 화면 y 축은 위로 증가 → 맨 위가 screenHeight
            color = Color.YELLOW,
            scale = 1.2f
        )

        // 2) 월드 텍스트 (월드 좌표) — 월드 정중앙에 "WORLD CENTER".
        //    WASD 로 카메라를 움직이면 이 글자도 화면에서 움직인다.
        drawTextOnScreen(
            text = "Coins: $coinCount / $targetCoinCount",
            x = 10f,
            y = screenHeight - 35f,
            color = Color.GOLD,
            scale = 1.2f
        )

        drawTextInWorld(
            text = "WORLD CENTER",
            worldX = worldWidth / 2 - 70f,
            worldY = worldHeight / 2,
            color = Color.CYAN,
            scale = 1.5f
        )
    }

    /** 게임 오버 시 화면 중앙에 띄우는 안내 메시지. */
    private fun drawGameOverOverlay() {
        drawTextOnScreen(
            text = "Game Over!",
            x = screenWidth / 2 - 80f,
            y = screenHeight / 2,
            color = Color.WHITE,
            scale = 2f
        )
        drawTextOnScreen(
            text = "Press ESC to exit",
            x = screenWidth / 2 - 70f,
            y = screenHeight / 2 - 40f,
            color = Color.WHITE,
            scale = 1f
        )
    }

    /** 화면이 닫힐 때 — 부모도 dispose 한 뒤 우리만의 자원도 해제. */
    private fun drawVictoryOverlay() {
        drawTextOnScreen(
            text = "Victory!",
            x = screenWidth / 2 - 70f,
            y = screenHeight / 2,
            color = Color.GOLD,
            scale = 2f
        )
        drawTextOnScreen(
            text = "Collected 100 coins",
            x = screenWidth / 2 - 95f,
            y = screenHeight / 2 - 40f,
            color = Color.WHITE,
            scale = 1f
        )
        drawTextOnScreen(
            text = "Press ESC to exit",
            x = screenWidth / 2 - 70f,
            y = screenHeight / 2 - 70f,
            color = Color.WHITE,
            scale = 1f
        )
    }

    override fun dispose() {
        super.dispose()
        tileTexture.dispose()
    }
}
