/**
 * @file GameView.java
 * @author rijo1001@student.miun.se
 * @date 2015-05-25
 * */
package se.maokei.spacekitty;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

/**
 * @class GameView
 * @extends SurfaceView
 * @implements SurfaceHolder.Callback
 * @implements SensorEventListener
 * @brief Custom game view contains canvas
 * */
public class GameView extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener {
    private volatile GameThread thread;

    //private SensorEventListener sensorAccelerometer;

    private Handler mHandler;
    private TextView mScoreView;
    private TextView mStatusView;
    private TextView mWarningView;

    Sensor accelerometer;
    Sensor magnetometer;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //Get the holder of the screen and register interest
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        //Set up a handler for messages from GameThread
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message m) {
                if(m.getData().getBoolean("score")) {
                    mScoreView.setText(m.getData().getString("text"));
                }
                else {
                    //So it is a status
                    int i = m.getData().getInt("viz");
                    switch(i) {
                        case View.VISIBLE:
                            mStatusView.setVisibility(View.VISIBLE);
                            break;
                        case View.INVISIBLE:
                            mStatusView.setVisibility(View.INVISIBLE);
                            break;
                        case View.GONE:
                            mStatusView.setVisibility(View.GONE);
                            break;
                    }

                    mStatusView.setText(m.getData().getString("text"));
                }
            }
        };
    }

    //Used to release any resources.
    public void cleanup() {
        this.thread.setRunning(false);
        this.thread.cleanup();

        this.removeCallbacks(thread);
        thread = null;

        this.setOnTouchListener(null);

        SurfaceHolder holder = getHolder();
        holder.removeCallback(this);
    }
	
	/**
	 * Setters and Getters
	 */
    public void setThread(GameThread newThread) {

        thread = newThread;

        setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                return thread != null && thread.onTouch(event);
            }
        });

        setClickable(true);
        setFocusable(true);
    }

    public GameThread getThread() {
        return thread;
    }

    public TextView getStatusView() {
        return mStatusView;
    }

    public void setStatusView(TextView mStatusView) {
        this.mStatusView = mStatusView;
    }

    public TextView getScoreView() {
        return mScoreView;
    }

    public TextView getWarningView() {return mWarningView;}

    public void setWarningView(TextView mWarningView) {this.mWarningView = mWarningView;}

    public void setScoreView(TextView mScoreView) {
        this.mScoreView = mScoreView;
    }


    public Handler getmHandler() {
        return mHandler;
    }

    public void setmHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }
	
	
	/**
	 * Screen functions
	 */

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if(thread!=null) {
            if (!hasWindowFocus)
                thread.pause();
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("RMJ", "Gameview created.");
        if(thread!=null) {
            thread.setRunning(true);

            if(thread.getState() == Thread.State.NEW){
                //Just start the new thread
                thread.start();
            }
            else {
                if(thread.getState() == Thread.State.TERMINATED){
                    /**
                     * Start a new thread
                     * Should be this to update screen with old game: new GameThread(this, thread);
                     * The method should set all fields in new thread to the value of old thread's fields
                     **/
                    thread = new TheGame(this);
                    thread.setRunning(true);
                    thread.start();
                }
            }
        }
    }

    //Always called once after surfaceCreated. Tell the GameThread the actual size
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(thread!=null) {
            thread.setSurfaceSize(width, height);
        }
    }

    /*
     * Need to stop the GameThread if the surface is destroyed
     * Remember this doesn't need to happen when app is paused on even stopped.
     */
    public void surfaceDestroyed(SurfaceHolder arg0) {

        boolean retry = true;
        if(thread!=null) {
            thread.setRunning(false);
        }

        //join the thread with this thread
        while (retry) {
            try {
                if(thread!=null) {
                    thread.join();
                }
                retry = false;
            }
            catch (InterruptedException e) {
                //naugthy, ought to do something...
            }
        }
    }
	
	/*
	 * Accelerometer
	 */

    public void startSensor(SensorManager sm) {

        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sm.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);

    }

    public void removeSensor(SensorManager sm) {
        sm.unregisterListener(this);

        accelerometer = null;
        magnetometer = null;
    }

    //A sensor has changed, let the thread take care of it
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(thread!=null) {
            thread.onSensorChanged(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}