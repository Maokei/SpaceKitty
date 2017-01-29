/**
 * @file GameThread.java
 * @author rijo1001@student.miun.se
 * @date 2015-05-25
 * */
package se.maokei.spacekitty;

//imports
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.TextView;


/**
 * @class GameThread
 * @extends Thread
 * @brief
 * */
public abstract class GameThread extends Thread {
    //Different game mode states
    public static final int STATE_LOSE = 1;
    public static final int STATE_PAUSE = 2;
    public static final int STATE_READY = 3;
    public static final int STATE_RUNNING = 4;
    public static final int STATE_WIN = 5;

    //Control variable for the mode of the game (e.g. STATE_WIN)
    protected int mMode = 1;

    //Control of the actual running inside run()
    private boolean mRun = false;

    //The surface this thread (and only this thread) writes upon
    private SurfaceHolder mSurfaceHolder;

    //the message handler to the View/Activity thread
    private Handler mHandler;

    //Android Context - this stores almost all we need to know
    private Context mContext;

    //The view
    public GameView mGameView;

    //We might want to extend this call - therefore protected
    protected int mCanvasWidth = 1;
    protected int mCanvasHeight = 1;

    //Last time we updated the game physics
    protected long mLastTime = 0;

    protected Bitmap mBackgroundImage;

    protected long score = 0;

    //Used for time keeping, time between updates
    private long now;
    private float elapsed;

    //Rotation vectors used to calculate orientation
    float[] mGravity;
    float[] mGeomagnetic;

    //Used to ensure appropriate threading
    static final Integer monitor = 1;

    //time playing
    protected long timePlaying;


    public GameThread(GameView gameView) {
        mGameView = gameView;

        mSurfaceHolder = gameView.getHolder();
        mHandler = gameView.getmHandler();
        mContext = gameView.getContext();

        mBackgroundImage = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.space_back);
    }

    /**
     * cleanup
     *
     * Clean up at shutdown
     * */
    public void cleanup() {
        this.mContext = null;
        this.mGameView = null;
        this.mHandler = null;
        this.mSurfaceHolder = null;
    }

    //Pre-begin a game
    abstract public void setupBeginning();

    //Starting up the game
    public void doStart() {
        synchronized(monitor) {

            setupBeginning();

            //update timer
            mLastTime = System.currentTimeMillis() + 100;

            //timePLaying
            timePlaying = 0;

            //set state
            setState(STATE_RUNNING);

            setScore(0);
        }
    }

    //The thread start
    @Override
    public void run() {
        Canvas canvasRun;
        while (mRun) {
            canvasRun = null;
            try {
                canvasRun = mSurfaceHolder.lockCanvas(null);
                synchronized (monitor) {
                    if (mMode == STATE_RUNNING) {
                        updatePhysics();
                    }
                    doDraw(canvasRun);
                }
            }
            finally {
                if (canvasRun != null) {
                    if(mSurfaceHolder != null)
                        mSurfaceHolder.unlockCanvasAndPost(canvasRun);
                }
            }
        }
    }

    /*
     * Surfaces and drawing
     */
    public void setSurfaceSize(int width, int height) {
        synchronized (monitor) {
            mCanvasWidth = width;
            mCanvasHeight = height;

            // don't forget to resize the background image
            mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage, width, height, true);
        }
    }


    /**
     * @name doDraw
     * @brief draw background if canvas is not null.
     * */
    protected void doDraw(Canvas canvas) {

        if(canvas == null) return;

        if(mBackgroundImage != null) canvas.drawBitmap(mBackgroundImage, 0, 0, null);
    }

    private void updatePhysics() {
        now = System.currentTimeMillis();
        elapsed = (now - mLastTime) / 1000.0f;

        updateGame(elapsed);

        mLastTime = now;
    }

    abstract protected void updateGame(float secondsElapsed);

	/*
	 * Control functions
	 */

    //Finger touches the screen
    public boolean onTouch(MotionEvent e) {
        if(e.getAction() != MotionEvent.ACTION_DOWN) return false;

        if(mMode == STATE_READY || mMode == STATE_LOSE || mMode == STATE_WIN) {
            doStart();
            return true;
        }

        if(mMode == STATE_PAUSE) {
            unpause();
            return true;
        }

        synchronized (monitor) {
            this.actionOnTouch(e.getRawX(), e.getRawY());
        }

        return false;
    }

    protected void actionOnTouch(float x, float y) {
        //Override to do something
    }

    //The Orientation has changed
    @SuppressWarnings("deprecation")
    public void onSensorChanged(SensorEvent event) {
        synchronized (monitor) {

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                mGravity = event.values;
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                mGeomagnetic = event.values;
            if (mGravity != null && mGeomagnetic != null) {
                float R[] = new float[9];
                float I[] = new float[9];
                boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
                if (success) {
                    float orientation[] = new float[3];
                    SensorManager.getOrientation(R, orientation);
                    actionWhenPhoneMoved(orientation[2],orientation[1],orientation[0]);
                }
            }
        }
    }

    protected void actionWhenPhoneMoved(float xDirection, float yDirection, float zDirection) {
        //Override to do something
    }

    /**
     * Game states
     */
    public void pause() {
        synchronized (monitor) {
            if (mMode == STATE_RUNNING) setState(STATE_PAUSE);
        }
    }

    /**
     * @name unpause
     * */
    public void unpause() {
        // Move the real time clock up to now
        synchronized (monitor) {
            mLastTime = System.currentTimeMillis();
        }
        setState(STATE_RUNNING);
    }

    /**
     * @name setState
     * @brief Send messages to View/Activity thread
     * */
    public void setState(int mode) {
        synchronized (monitor) {
            setState(mode, null);
        }
    }

    /**
     * setState
     * @name setState
     * @brief Set game states
     * */
    public void setState(int mode, CharSequence message) {
        synchronized (monitor) {
            mMode = mode;

            if (mMode == STATE_RUNNING) {
                Message msg = mHandler.obtainMessage();
                Bundle b = new Bundle();
                b.putString("text", "");
                b.putInt("viz", View.INVISIBLE);
                b.putBoolean("showAd", false);
                msg.setData(b);
                mHandler.sendMessage(msg);
            }
            else {
                Message msg = mHandler.obtainMessage();
                Bundle b = new Bundle();

                Resources res = mContext.getResources();
                CharSequence str = "";
                if (mMode == STATE_READY)
                    str = res.getText(R.string.mode_ready);
                else
                if (mMode == STATE_PAUSE)
                    str = res.getText(R.string.mode_pause);
                else
                if (mMode == STATE_LOSE)
                    str = res.getText(R.string.mode_lose);
                else
                if (mMode == STATE_WIN) {
                    str = res.getText(R.string.mode_win);
                }

                if (message != null) {
                    str = message + "\n" + str;
                }

                b.putString("text", str.toString());
                b.putInt("viz", View.VISIBLE);

                msg.setData(b);
                mHandler.sendMessage(msg);
            }
        }
    }

    /**
     * Getter and setter
     */
    public void setSurfaceHolder(SurfaceHolder h) {
        mSurfaceHolder = h;
    }

    public boolean isRunning() {
        return mRun;
    }

    public void setRunning(boolean running) {
        mRun = running;
    }

    public int getMode() {
        return mMode;
    }

    public void setMode(int mMode) {
        this.mMode = mMode;
    }

    /**
     * @name setScore
     * @brief Send a score to the View to view.
     * Would it be better to do this inside this thread writing it manually on the screen?
     * */
    public void setScore(long score) {
        this.score = score;

        synchronized (monitor) {
            Message msg = mHandler.obtainMessage();
            Bundle b = new Bundle();
            b.putBoolean("score", true);
            b.putString("text", getScoreString().toString());
            msg.setData(b);
            mHandler.sendMessage(msg);
        }
    }

    public float getScore() {
        return score;
    }

    public void updateScore(long score) {
        this.setScore(this.score + score);
    }


    protected CharSequence getScoreString() {
        return Long.toString(Math.round(this.score));
    }

}