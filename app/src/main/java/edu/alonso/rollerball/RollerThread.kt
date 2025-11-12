package edu.alonso.rollerball

import android.view.SurfaceHolder
import android.graphics.PointF

class RollerThread(private val surfaceHolder: SurfaceHolder) : Thread() {

    private var rollerGame: RollerGame
    private var threadRunning = false
    private val velocity = PointF()

    init {
        threadRunning = true

        // Create a ball with boundaries determined by SurfaceView
        val canvas = surfaceHolder.lockCanvas()
        rollerGame = RollerGame(canvas.width, canvas.height)
        surfaceHolder.unlockCanvasAndPost(canvas)
    }

    override fun run() {
        while (threadRunning) {
            val canvas = surfaceHolder.lockCanvas()
            canvas?.let {
                rollerGame.update(velocity)
                rollerGame.draw(canvas)
                surfaceHolder.unlockCanvasAndPost(it)
            }
        }
    }

    fun changeAcceleration(xForce: Float, yForce: Float) {
        velocity.x = xForce
        velocity.y = yForce
    }

    fun stopThread() {
        threadRunning = false
    }

    fun shake() {
        rollerGame.newGame()
    }
}