package edu.alonso.rollerball

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect

const val WALL_SPEED = 10
const val WALL_COLOR = -0x5501

class Wall(var x: Int, var y: Int, initialDirectionRight: Boolean,
           private var surfaceWidth: Int, surfaceHeight: Int) {

    var rect: Rect
    private var moveDistance = 0
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        // Determine wall dimensions based on surface width and height
        val width = surfaceWidth / 6
        val height = surfaceHeight / 20

        // Make sure wall fits completely on the surface
        x = Math.min(x, surfaceWidth - width)
        y = Math.min(y, surfaceHeight - height)

        // Create wall's rectangle based on location and dimensions
        rect = Rect(x, y, x + width, y + height)

        // Determine how many pixels walls move each iteration
        moveDistance = if (initialDirectionRight) WALL_SPEED else -WALL_SPEED

        // Wall color
        paint.color = WALL_COLOR
    }

    fun relocate(xDistance: Int) {

        // Move wall to a new x location
        val x = Math.min(xDistance, surfaceWidth - rect.width())
        rect.offsetTo(x, rect.top)
    }

    fun move() {

        // Move wall right or left
        rect.offset(moveDistance, 0)

        // Bounce wall off surface edges
        if (rect.right > surfaceWidth) {
            rect.offsetTo(surfaceWidth - rect.width(), rect.top)
            moveDistance *= -1
        } else if (rect.left < 0) {
            rect.offsetTo(0, rect.top)
            moveDistance *= -1
        }
    }

    fun draw(canvas: Canvas) {
        canvas.drawRect(rect, paint)
    }
}