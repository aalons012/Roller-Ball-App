package edu.alonso.rollerball

import android.graphics.*
import kotlin.random.Random

const val NUM_WALLS = 3

class RollerGame(private val surfaceWidth: Int, private val surfaceHeight: Int) {

    private val ball = Ball(surfaceWidth, surfaceHeight)
    private val wallList = mutableListOf<Wall>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var gameOver = false

    init {
        paint.textSize = 90f
        paint.color = Color.RED

        val wallY = surfaceHeight / (NUM_WALLS + 1)

        // Add walls at random locations, and alternate initial direction
        for (c in 1..NUM_WALLS) {
            val initialRight = c % 2 == 0
            wallList.add(
                Wall(Random.nextInt(surfaceWidth), wallY * c, initialRight,
                    surfaceWidth, surfaceHeight)
            )
        }

        newGame()
    }

    fun newGame() {
        gameOver = false

        // Reset ball at the top of the screen
        ball.setCenter(surfaceWidth / 2, BALL_RADIUS + 10)

        // Reset walls at random spots
        for (wall in wallList) {
            wall.relocate(Random.nextInt(surfaceWidth))
        }
    }

    fun update(velocity: PointF) {
        if (gameOver) return

        // Move ball and walls
        ball.move(velocity)
        for (wall in wallList) {
            wall.move()
        }

        // Check for collision
        for (wall in wallList) {
            if (ball.intersects(wall)) {
                gameOver = true
            }
        }

        // Check for win
        if (ball.bottom >= surfaceHeight) {
            gameOver = true
        }
    }

    fun draw(canvas: Canvas) {

        // Wipe canvas clean
        canvas.drawColor(Color.WHITE)

        // Draw ball and walls
        ball.draw(canvas)
        for (wall in wallList) {
            wall.draw(canvas)
        }

        // Does ball bottom reach the screen bottom?
        if (ball.bottom >= surfaceHeight) {
            val text = "You won!"
            val textBounds = Rect()
            paint.getTextBounds(text, 0, text.length, textBounds)
            canvas.drawText(
                text, surfaceWidth / 2f - textBounds.exactCenterX(),
                surfaceHeight / 2f - textBounds.exactCenterY(), paint
            )
        }
    }
}