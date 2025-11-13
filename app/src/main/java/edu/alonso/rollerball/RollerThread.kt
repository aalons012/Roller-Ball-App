package edu.alonso.rollerball

import android.graphics.PointF
import android.view.SurfaceHolder

/**
 * The dedicated thread for running the game loop.
 * This class handles the continuous cycle of updating the game state and drawing it to the screen.
 * By running the game on a separate thread, it prevents the main UI thread from being blocked,
 * ensuring a smooth user experience.
 *
 * @param surfaceHolder The SurfaceHolder that provides access to the drawing canvas.
 */
class RollerThread(private val surfaceHolder: SurfaceHolder) : Thread() {

    // The main game logic object, which manages all game elements like the ball and walls.
    private var rollerGame: RollerGame
    // A flag to control the execution of the thread's loop. When false, the thread terminates.
    private var threadRunning = false
    // A PointF object that stores the current velocity of the ball, determined by the accelerometer.
    private val velocity = PointF()

    /**
     * The initialization block that is executed when a RollerThread object is created.
     * It sets up the initial state of the game.
     */
    init {
        // Set the running flag to true to allow the run() loop to start.
        threadRunning = true

        // Before the game loop starts, create the RollerGame instance.
        // To do this safely, we need to get the dimensions of the drawing surface.
        // We lock the canvas to get its properties (like width and height).
        val canvas = surfaceHolder.lockCanvas()
        // Create the RollerGame instance with the correct surface dimensions.
        rollerGame = RollerGame(canvas.width, canvas.height)
        // Immediately unlock the canvas, as we are not drawing anything yet.
        surfaceHolder.unlockCanvasAndPost(canvas)
    }

    /**
     * The main entry point for the thread after it is started.
     * This method contains the game loop that continuously updates and renders the game.
     */
    override fun run() {
        // The game loop continues as long as threadRunning is true.
        while (threadRunning) {
            // Lock the canvas to get exclusive access for drawing.
            val canvas = surfaceHolder.lockCanvas()
            // The canvas can be null if the surface is not ready, so we use a null-safe 'let' block.
            canvas?.let {
                // First, update the state of all game objects (ball, walls, collisions).
                rollerGame.update(velocity)
                // Second, draw all game objects onto the canvas in their new state.
                rollerGame.draw(canvas)
                // Finally, unlock the canvas and post the new frame to the screen.
                surfaceHolder.unlockCanvasAndPost(it)
            }
        }
    }

    /**
     * Updates the ball's velocity based on new accelerometer data.
     * This method is called from the RollerSurfaceView when a sensor event occurs.
     * @param xForce The force detected on the x-axis.
     * @param yForce The force detected on the y-axis.
     */
    fun changeAcceleration(xForce: Float, yForce: Float) {
        // Update the velocity components with the new force values.
        velocity.x = xForce
        velocity.y = yForce
    }

    /**
     * Stops the game loop and terminates the thread.
     * This is typically called when the surface is destroyed (e.g., app is backgrounded).
     */
    fun stopThread() {
        // Setting this flag to false will cause the while loop in the run() method to exit.
        threadRunning = false
    }

    /**
     * Triggers a reset of the game.
     * This method is called from the RollerSurfaceView when a shake gesture is detected.
     */
    fun shake() {
        // Delegate the call to the rollerGame object to reset the game state.
        rollerGame.newGame()
    }
}
