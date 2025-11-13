package edu.alonso.rollerball

import android.graphics.*
import kotlin.random.Random

/**
 * A constant that defines the number of walls to be created in the game.
 */
const val NUM_WALLS = 3

/**
 * The main class for the Rollerball game logic.
 * This class orchestrates the game, managing the ball, walls, game state (win/lose),
 * and drawing all the elements on the canvas.
 *
 * @param surfaceWidth The width of the game's drawing surface.
 * @param surfaceHeight The height of the game's drawing surface.
 */
class RollerGame(private val surfaceWidth: Int, private val surfaceHeight: Int) {

    // The game's ball object.
    private val ball = Ball(surfaceWidth, surfaceHeight)
    // A mutable list to hold all the wall objects in the game.
    private val wallList = mutableListOf<Wall>()
    // A Paint object for drawing text, such as the "You won!" message.
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    // A boolean flag to indicate if the game is over (either by collision or winning).
    private var gameOver = false

    /**
     * The initialization block that is executed when a RollerGame object is created.
     * It sets up the paint for text, creates the walls, and starts a new game.
     */
    init {
        // Configure the paint object for drawing text.
        paint.textSize = 90f
        paint.color = Color.RED

        // Calculate the vertical spacing for the walls to distribute them evenly.
        val wallY = surfaceHeight / (NUM_WALLS + 1)

        // Create and add walls to the wallList.
        // The walls are placed at random horizontal locations and alternate their initial movement direction.
        for (c in 1..NUM_WALLS) {
            val initialRight = c % 2 == 0 // Determines if the wall starts moving right or left.
            wallList.add(
                Wall(Random.nextInt(surfaceWidth), wallY * c, initialRight,
                    surfaceWidth, surfaceHeight)
            )
        }

        // Start the first game.
        newGame()
    }

    /**
     * Resets the game to its initial state.
     * This is called when the game starts or when a shake gesture is detected.
     */
    fun newGame() {
        gameOver = false

        // Place the ball at the top-center of the screen.
        ball.setCenter(surfaceWidth / 2, BALL_RADIUS + 10)

        // Relocate each wall to a new random horizontal position.
        for (wall in wallList) {
            wall.relocate(Random.nextInt(surfaceWidth))
        }
    }

    /**
     * Updates the state of the game for each frame.
     * This includes moving the ball and walls and checking for collisions or win conditions.
     * @param velocity A PointF object representing the ball's velocity, derived from sensor data.
     */
    fun update(velocity: PointF) {
        // Do not update the game state if the game is already over.
        if (gameOver) return

        // Move the ball based on the provided velocity.
        ball.move(velocity)
        // Move each wall according to its own movement logic.
        for (wall in wallList) {
            wall.move()
        }

        // Check for a collision between the ball and any of the walls.
        for (wall in wallList) {
            if (ball.intersects(wall)) {
                // If a collision occurs, the game is over.
                gameOver = true
            }
        }

        // Check if the player has won by reaching the bottom of the screen.
        if (ball.bottom >= surfaceHeight) {
            gameOver = true
        }
    }

    /**
     * Draws the current state of the game onto the canvas.
     * @param canvas The Canvas object on which to draw the game elements.
     */
    fun draw(canvas: Canvas) {

        // Clear the canvas with a white background for the new frame.
        canvas.drawColor(Color.WHITE)

        // Draw the ball and all the walls.
        ball.draw(canvas)
        for (wall in wallList) {
            wall.draw(canvas)
        }

        // If the ball has reached the bottom of the screen, display the win message.
        if (ball.bottom >= surfaceHeight) {
            val text = "You won!"
            val textBounds = Rect()
            // Get the bounding box of the text to center it accurately.
            paint.getTextBounds(text, 0, text.length, textBounds)
            // Draw the text in the center of the screen.
            canvas.drawText(
                text, surfaceWidth / 2f - textBounds.exactCenterX(),
                surfaceHeight / 2f - textBounds.exactCenterY(), paint
            )
        }
    }
}
