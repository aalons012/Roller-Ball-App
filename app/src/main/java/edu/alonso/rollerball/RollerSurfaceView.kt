package edu.alonso.rollerball

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView

class RollerSurfaceView(context: Context, attrs: AttributeSet) :
    SurfaceView(context, attrs), SurfaceHolder.Callback {

    private var rollerThread: RollerThread? = null

    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        rollerThread = RollerThread(holder)
        rollerThread?.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // Nothing to do
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        rollerThread?.stopThread()
    }

    fun changeAcceleration(x: Float, y: Float) {
        rollerThread?.changeAcceleration(x, y)
    }

    fun shake() {
        rollerThread?.shake()
    }
}