package edu.alonso.rollerball

import android.graphics.*

/**
 * The radius of the ball in pixels.
 */
const val BALL_RADIUS = 100

/**
 * The color of the ball, represented as a hex value.
 */
const val BALL_COLOR = 0xffaaaaff

/**
 * Represents the ball in the game.
 * This class manages the ball's position, movement, drawing, and collision detection.
 *
 * @param surfaceWidth The width of the drawing surface.
 * @param surfaceHeight The height of the drawing surface.
 */
class Ball(private val surfaceWidth: Int, private val surfaceHeight: Int) {

    // Paint object used for drawing the ball with anti-aliasing.
    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // The center point of the ball, initialized at the top-left corner.
    private var center = Point(BALL_RADIUS, BALL_RADIUS)

    /**
     * Calculates the position of the bottom edge of the ball.
     */
    val bottom
        get() = center.y + BALL_RADIUS

    /**
     * Initialization block that runs when a Ball object is created.
     */
    init {
        // Sets the ball's color.
        paint.color = BALL_COLOR.toInt()
    }

    /**
     * Sets the center of the ball to a specific coordinate.
     *
     * @param x The x-coordinate for the ball's center.
     * @param y The y-coordinate for the ball's center.
     */
    fun setCenter(x: Int, y: Int) {
        // Update the circle's center coordinates.
        center.x = x
        center.y = y
    }

    /**
     * Moves the ball based on a given velocity and handles screen boundary collisions.
     *
     * @param velocity A PointF object representing the velocity on the x and y axes.
     */
    fun move(velocity: PointF) {

        // Update the ball's center position by applying the velocity.
        // Note: X-velocity is inverted to match sensor orientation, and Y-velocity is applied normally.
        center.offset(-velocity.x.toInt(), velocity.y.toInt())

        // Prevent the ball from going past the bottom or top of the screen.
        if (center.y > surfaceHeight - BALL_RADIUS) {
            center.y = surfaceHeight - BALL_RADIUS
        } else if (center.y < BALL_RADIUS) {
            center.y = BALL_RADIUS
        }

        // Prevent the ball from going past the right or left of the screen.
        if (center.x > surfaceWidth - BALL_RADIUS) {
            center.x = surfaceWidth - BALL_RADIUS
        } else if (center.x < BALL_RADIUS) {
            center.x = BALL_RADIUS
        }
    }

    /**
     * Draws the ball on the provided Canvas.
     *
     * @param canvas The canvas on which to draw the ball.
     */
    fun draw(canvas: Canvas) {
        canvas.drawCircle(center.x.toFloat(), center.y.toFloat(), BALL_RADIUS.toFloat(), paint)
    }

    /**
     * Checks if the ball is intersecting with a given Wall object.
     * This uses the Separating Axis Theorem for circle-rectangle collision.
     *
     * @param wall The Wall object to check for intersection.
     * @return True if the ball intersects the wall, false otherwise.
     */
    fun intersects(wall: Wall): Boolean {

        // Find the point on the wall's rectangle that is closest to the ball's center.
        val nearestX = Math.max(wall.rect.left.toFloat(), Math.min(center.x.toFloat(), wall.rect.right.toFloat()))
        val nearestY = Math.max(wall.rect.top.toFloat(), Math.min(center.y.toFloat(), wall.rect.bottom.toFloat()))

        // Calculate the distance vector from the nearest point to the ball's center.
        val deltaX = center.x - nearestX
        val deltaY = center.y - nearestY

        // Use the Pythagorean theorem (a^2 + b^2 = c^2) to check for collision.
        // If the squared distance to the nearest point is less than the ball's squared radius, a collision has occurred.
        // Using squared distances is a common optimization to avoid a costly square root operation.
        return deltaX * deltaX + deltaY * deltaY < BALL_RADIUS * BALL_RADIUS
    }
}
