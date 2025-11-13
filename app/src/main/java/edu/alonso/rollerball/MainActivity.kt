package edu.alonso.rollerball

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.abs

/**
 * A constant representing the sensitivity threshold for detecting a shake gesture.
 * A higher value makes the shake detection less sensitive.
 */

/**
 * made by Andy Alonso
 * created in 11/12/25
 */
const val SHAKE_THRESHOLD = 100

/**
 * The main activity for the Rollerball application.
 * This activity manages sensor data (specifically the accelerometer) to control the ball's movement
 * and listens for shake gestures to reset the game state.
 * It implements SensorEventListener to receive updates from the device's sensors.
 */
class MainActivity : AppCompatActivity(), SensorEventListener {

    // Manages access to the device's sensors.
    private lateinit var sensorManager: SensorManager

    // Represents the device's accelerometer sensor. It's nullable in case the device doesn't have one.
    private var accelerometer: Sensor? = null

    // The custom SurfaceView that handles drawing the game elements.
    private lateinit var surfaceView: RollerSurfaceView

    // Stores the last recorded acceleration magnitude to calculate the difference for shake detection.
    // Initialized with the standard gravity of Earth.
    private var lastAcceleration = SensorManager.GRAVITY_EARTH

    /**
     * Called when the activity is first created. This is where you should do all of your normal
     * static set up: create views, bind data to lists, etc.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down
     * then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     * Note: Otherwise it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the user interface layout for this activity.
        setContentView(R.layout.activity_main)

        // Initialize the SensorManager by getting the system service.
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        // Get the default accelerometer sensor from the device.
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Find the RollerSurfaceView from the layout by its ID.
        surfaceView = findViewById(R.id.roller_surface)

        // Set a click listener on the surface view for testing purposes.
        // This allows triggering the shake() method manually, which is useful in an emulator
        // that cannot simulate a physical shake.
        surfaceView.setOnClickListener { surfaceView.shake() }
    }

    /**
     * Called when the activity will start interacting with the user.
     * At this point your activity is at the top of the activity stack, with user input going to it.
     * It's a good place to start animations, open exclusive-access devices (such as the camera), etc.
     */
    override fun onResume() {
        super.onResume()
        // Register the sensor listener to start receiving accelerometer updates.
        // SENSOR_DELAY_NORMAL provides updates at a standard rate suitable for screen orientation changes.
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    /**
     * Called when the activity is no longer in the foreground.
     * It's important to unregister the sensor listener here to save battery
     * and prevent the app from using resources when it's not visible.
     */
    override fun onPause() {
        super.onPause()
        // Unregister the sensor listener to stop receiving updates.
        sensorManager.unregisterListener(this, accelerometer)
    }

    /**
     * Called when there is a new sensor event.
     * This callback is the primary way to get sensor data.
     * @param event The SensorEvent object, which contains information about the new sensor data.
     */
    override fun onSensorChanged(event: SensorEvent) {

        // Extract the accelerometer values for the x, y, and z axes from the event.
        val x: Float = event.values[0]
        val y: Float = event.values[1]
        val z: Float = event.values[2]

        // Pass the x and y acceleration values to the RollerSurfaceView to move the ball.
        surfaceView.changeAcceleration(x, y)

        // Calculate the magnitude of the current acceleration vector using the formula x^2 + y^2 + z^2.
        // This gives a single value representing the overall force of acceleration.
        val currentAcceleration: Float = x * x + y * y + z * z

        // Calculate the change (delta) between the current acceleration and the last recorded one.
        val delta = currentAcceleration - lastAcceleration

        // Store the current acceleration magnitude for the next sensor event.
        lastAcceleration = currentAcceleration

        // Check if the absolute value of the change in acceleration exceeds the shake threshold.
        if (abs(delta) > SHAKE_THRESHOLD) {
            // If it does, call the shake() method on the surfaceView to reset the game.
            surfaceView.shake()
        }
    }

    /**
     * Called when the accuracy of the registered sensor has changed.
     * @param sensor The Sensor whose accuracy has changed.
     * @param accuracy The new accuracy of this sensor.
     */
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // This method is required by the SensorEventListener interface,
        // but it is not used in this application.
    }
}
