/**
 * @file 2015-05-29
 * @authour rijo1001@student.miun.se
 * */
package se.maokei.spacekitty;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorManager;
import android.os.*;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.TextView;


/**
 * @class GameActivity
 * @brief this is the game activity where the game takes place.
 * */
public class GameActivity extends Activity {
    private static final int MENU_RESUME = 1;
    private static final int MENU_START = 2;
    private static final int MENU_STOP = 3;

    //Game thread
    private GameThread mGameThread;
    //Game view / canvas
    private GameView mGameView;

    //handler
    private Handler h;

    //Database

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        //set content view
        setContentView(R.layout.activity_game);
        //set views
        mGameView = (GameView)findViewById(R.id.gamearea);
        mGameView.setStatusView((TextView)findViewById(R.id.text));
        mGameView.setScoreView((TextView) findViewById(R.id.score));
        mGameView.setWarningView((TextView)findViewById(R.id.warning));

        this.startGame(mGameView, null, savedInstanceState);

        //handler
        h = new Handler() {
            public void handleMessage(Message msg) {
                final int what = msg.what;
                if(what == 1) {
                    finish();
                }
            }
        };
    }

    private void startGame(GameView gView, GameThread gThread, Bundle savedInstanceState) {

        //Set up a new game, we don't care about previous states
        mGameThread = new TheGame(mGameView);
        mGameView.setThread(mGameThread);
        mGameThread.setState(GameThread.STATE_READY);
        mGameView.startSensor((SensorManager)getSystemService(Context.SENSOR_SERVICE));
    }

	/**
	 * Activity state functions
	 */
    @Override
    protected void onPause() {
        super.onPause();

        if(mGameThread.getMode() == GameThread.STATE_RUNNING) {
            mGameThread.setState(GameThread.STATE_PAUSE);
            finish();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        mGameView.cleanup();
        mGameView.removeSensor((SensorManager)getSystemService(Context.SENSOR_SERVICE));
        mGameThread = null;
        mGameView = null;
    }

    /*
     * UI Functions
     */

    /**
     * onCreateOptionsMenu
     * Setup menu
     * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        //menu.add(0, MENU_START, 0, R.string.menu_start);
        //menu.add(0, MENU_STOP, 0, R.string.menu_stop);
        //menu.add(0, MENU_RESUME, 0, R.string.menu_resume);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_START:
                mGameThread.doStart();
                return true;
            case MENU_STOP:
                mGameThread.setState(GameThread.STATE_LOSE,  "stopped");
                return true;
            case MENU_RESUME:
                mGameThread.unpause();
                return true;
        }

        return false;
    }

    /*
    public void onNothingSelected(AdapterView<?> arg0) {
        // Do nothing if nothing is selected
    }*/
}
