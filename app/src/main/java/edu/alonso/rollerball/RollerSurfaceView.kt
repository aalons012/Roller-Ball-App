package edu.alonso.rollerball

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView

/**
 * A custom SurfaceView for rendering the Rollerball game.
 * This view manages the game's rendering thread (`RollerThread`) and acts as an interface
 * between the MainActivity (handling UI and sensor events) and the game logic.
 *
 * It implements SurfaceHolder.Callback to listen for lifecycle events of the underlying Surface,
 * such as when it's created, changed, or destroyed.
 *
 * @param context The Context the view is running in, through which it can access the current theme, resources, etc.
 * @param attrs The attributes of the XML tag that is inflating the view.
 */
class RollerSurfaceView(context: Context, attrs: AttributeSet) :
    SurfaceView(context, attrs), SurfaceHolder.Callback {

    // The dedicated thread that runs the game loop (update and draw).
    // It is nullable because it is only created when the surface is ready.
    private var rollerThread: RollerThread? = null

    /**
     * The initialization block that runs when a RollerSurfaceView object is created.
     */
    init {
        // Register this class as the callback handler for the SurfaceHolder.
        // This allows the view to receive surface lifecycle events (created, changed, destroyed).
        holder.addCallback(this)
    }

    /**
     * This is called immediately after the surface is first created.
     * It's the ideal place to create and start the rendering thread.
     * @param holder The SurfaceHolder whose surface is being created.
     */
    override fun surfaceCreated(holder: SurfaceHolder) {
        // Create a new instance of the game thread, passing it the surface holder.
        rollerThread = RollerThread(holder)
        // Start the game loop.
        rollerThread?.start()
    }

    /**
     * This is called immediately after any structural changes (format or size) have been made to the surface.
     * @param holder The SurfaceHolder whose surface has changed.
     * @param format The new PixelFormat of the surface.
     * @param width The new width of the surface.
     * @param height The new height of the surface.
     */
    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // This method is required by the interface, but no action is needed here for this game.
        // The width and height are accessed by the RollerThread directly from the SurfaceHolder.
    }

    /**
     * This is called immediately before a surface is being destroyed.
     * It is crucial to stop the rendering thread here to prevent it from trying to draw on an invalid surface.
     * @param holder The SurfaceHolder whose surface is being destroyed.
     */
    override fun surfaceDestroyed(holder: SurfaceHolder) {
        // Gracefully stop the game thread.
        rollerThread?.stopThread()
    }

    /**
     * Relays acceleration data from the MainActivity to the game thread.
     * This method serves as a bridge to pass sensor data into the game logic.
     * @param x The acceleration force along the x-axis.
     * @param y The acceleration force along the y-axis.
     */
    fun changeAcceleration(x: Float, y: Float) {
        // Delegate the call to the rollerThread, which will update the ball's velocity.
        rollerThread?.changeAcceleration(x, y)
    }

    /**
     * Relays a shake event from the MainActivity to the game thread.
     * This is used to trigger a reset of the game state.
     */
    fun shake() {
        // Delegate the call to the rollerThread, which will trigger the newGame() method.
        rollerThread?.shake()
    }
}
