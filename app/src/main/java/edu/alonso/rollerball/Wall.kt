package edu.alonso.rollerball

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect

/**
 * Defines the speed at which the walls move horizontally, in pixels per frame.
 */
const val WALL_SPEED = 10

/**
 * Defines the color of the walls, represented as a hex value.
 */
const val WALL_COLOR = -0x5501 // This is an ARGB integer, equivalent to 0xFFFAAF_FF

/**
 * Represents a single wall obstacle in the game.
 * This class manages the wall's position, dimensions, movement, and drawing.
 *
 * @param x The initial x-coordinate for the wall's top-left corner.
 * @param y The initial y-coordinate for the wall's top-left corner.
 * @param initialDirectionRight A boolean to determine the wall's initial movement direction.
 *                              `true` for right, `false` for left.
 * @param surfaceWidth The width of the drawing surface, used for boundary checks.
 * @param surfaceHeight The height of the drawing surface, used for initial placement.
 */
class Wall(
    var x: Int, var y: Int, initialDirectionRight: Boolean,
    private var surfaceWidth: Int, surfaceHeight: Int
) {

    // The Rect object that defines the wall's position and size.
    var rect: Rect

    // The number of pixels the wall moves each frame. Can be positive (right) or negative (left).
    private var moveDistance = 0

    // The Paint object used for drawing the wall with anti-aliasing.
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * The initialization block that runs when a Wall object is created.
     * It sets up the wall's dimensions, initial position, and movement properties.
     */
    init {
        // Determine the wall's dimensions based on the surface size.
        val width = surfaceWidth / 6
        val height = surfaceHeight / 20

        // Ensure the wall's initial position does not place it outside the screen boundaries.
        // It takes the lesser of the provided 'x' and the maximum possible 'x' to keep the wall onscreen.
        x = Math.min(x, surfaceWidth - width)
        y = Math.min(y, surfaceHeight - height)

        // Create the wall's rectangular shape based on its location and calculated dimensions.
        rect = Rect(x, y, x + width, y + height)

        // Set the movement speed and direction.
        // If initialDirectionRight is true, moveDistance is positive (moves right).
        // Otherwise, it's negative (moves left).
        moveDistance = if (initialDirectionRight) WALL_SPEED else -WALL_SPEED

        // Set the wall's color.
        paint.color = WALL_COLOR
    }

    /**
     * Moves the wall to a new horizontal position.
     * This is typically used to randomize wall locations when a new game starts.
     *
     * @param xDistance The new target x-coordinate for the wall's left edge.
     */
    fun relocate(xDistance: Int) {
        // Ensure the new location is valid and won't place the wall off the right edge of the screen.
        val x = Math.min(xDistance, surfaceWidth - rect.width())
        // Move the rectangle to the new coordinates. The vertical position (rect.top) remains unchanged.
        rect.offsetTo(x, rect.top)
    }

    /**
     * Moves the wall horizontally for the current game frame and handles bouncing off the screen edges.
     */
    fun move() {
        // Apply the horizontal movement (either positive or negative) to the wall's rectangle.
        rect.offset(moveDistance, 0)

        // Check for collisions with the screen's vertical edges.
        if (rect.right > surfaceWidth) {
            // If the wall hits the right edge, snap it back to the edge...
            rect.offsetTo(surfaceWidth - rect.width(), rect.top)
            // ...and reverse its direction of movement.
            moveDistance *= -1
        } else if (rect.left < 0) {
            // If the wall hits the left edge, snap it back to the edge...
            rect.offsetTo(0, rect.top)
            // ...and reverse its direction of movement.
            moveDistance *= -1
        }
    }

    /**
     * Draws the wall onto the provided Canvas.
     *
     * @param canvas The canvas on which to draw the wall.
     */
    fun draw(canvas: Canvas) {
        // Use the drawRect method of the canvas to render the wall's rectangle with its specified paint.
        canvas.drawRect(rect, paint)
    }
}
