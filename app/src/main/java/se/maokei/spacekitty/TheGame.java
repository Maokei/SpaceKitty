/**
 * @file TheGame.java
 * @author rijo1001@student.miun.se
 * @date 2015-05-25
 * @brief Main game class
 * */
package se.maokei.spacekitty;

//imports
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Iterator;
import java.util.Random;
import java.util.ArrayList;
import android.os.*;


public class TheGame extends GameThread{
    //time in space
    double time;
    //Bitmap assets
    private Bitmap spaceCat;
    private Bitmap cat;
    private Bitmap earth;
    private Bitmap[] debries;
    private ArrayList<Debrie> spacejunk;

    //context
    Context t;

    //space cat positions
    private float catX = 0;
    private float catY = 0;

    //spawning state
    boolean spawn = false;

    //earth position
    private float earthX;
    private float earthY;
    private int atmosphereRadius;

    //The speed (pixel/second) of the spacecat in direction X and Y
    private float catSpeedX = 0;
    private float catSpeedY = 0;

    //time / score /status
    private SpaceTimer st;
    private double spawnTime;
    private TextView timeView;
    //private TextView statusView;
    private TextView warningView;
    private String statusMessage;

    //ui handler
    private Handler uiHandler;

    //random
    private Random r;

    //Database
    private Database db;

    //name
    private String name = "Player";
    private int score = 0;

    //input dialog player name
    private AlertDialog.Builder alert;
    private EditText input;

    /**
     * Constructor
     * */
    public TheGame(GameView gameView) {
        super(gameView);
        t = gameView.getContext();

        //load bitmaps
        spaceCat = BitmapFactory.decodeResource(gameView.getContext().getResources(), R.drawable.kitty_micro);
        earth = BitmapFactory.decodeResource(gameView.getContext().getResources(), R.drawable.earth);
        cat = BitmapFactory.decodeResource(gameView.getContext().getResources(), R.mipmap.ic_launcher);

        //scale earth bitmap
        //earth = Bitmap.createScaledBitmap(earth, ((int)Math.floor(earth.getWidth()*1.8)),(int)Math.floor(earth.getHeight()*1.8), false);
        //earth = Bitmap.createScaledBitmap(earth, ((int)Math.floor(mCanvasWidth/10)),(int)Math.floor(mCanvasHeight/10), false);

        //Initiate debries 0 - 2
        debries = new Bitmap[] {
                BitmapFactory.decodeResource(gameView.getContext().getResources(), R.drawable.ufo_white_tiny),
                BitmapFactory.decodeResource(gameView.getContext().getResources(), R.drawable.tiny_asteroid),
                BitmapFactory.decodeResource(gameView.getContext().getResources(), R.drawable.tiny_enemy)
        };

        //load particle sprites

        //set score view and status view
        this.timeView = gameView.getScoreView();
        this.warningView = gameView.getWarningView();

        //status message
        statusMessage = "";

        //create ui handler
        this.uiHandler = new Handler();

        //setup random
        r = new Random();

        //update score
        uiHandler = new Handler() {
            public void handleMessage(Message msg) {
                final int what = msg.what;
                switch(what) {
                    case 1: updateScoreView(); break;
                    case 2: updateWarningView(); break;
                    case 3: updateScore(); break;
                }
            }
        };
    }

    /**
     * @name setupBeginning
     * @brief Setup the game
     * */
    @Override
    public void setupBeginning() {
        //space junk aka debries
        spacejunk = new ArrayList<Debrie>();
        //Initialise speeds
        catSpeedX = 0;
        catSpeedY = 0;

        //Place the ufo in the middle of the screen.
        //cat.Width() and cat.getHeight() gives us the height and width of the image of the cat
        catX = mCanvasWidth / 2;
        catY = mCanvasHeight  - mCanvasHeight / 6; //start on lower part of screen

        //draw earth in the middle
        earthX = mCanvasWidth / 2;
        earthY = mCanvasHeight / 2;
        atmosphereRadius = 240;

        //setup dialog
        alert = new AlertDialog.Builder(t);
        alert.setTitle("What's your name?");
        alert.setMessage("Your survival time " + score);
        // Set an EditText view to get user input
        input = new EditText(t);
        alert.setView(input);

        //setup start timer
        st = new SpaceTimer();
        st.start();

    }

    /**
     * @name doDraw
     * @brief drawing on the canvas id done here.
     * */
    @Override
    protected void doDraw(Canvas canvas) {
        if(canvas == null) return;

        super.doDraw(canvas);

        //draw earth in the middle of the canvas
        canvas.drawBitmap(earth, (earthX - earth.getWidth()/2), (earthY - earth.getHeight()/2), null);

        //draw atmosphere warning boundary
        Paint paint = new Paint();
        paint.setColor(Color.CYAN);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);
        //draw earth outer atmosphere
        canvas.drawCircle((mCanvasWidth/2), (mCanvasHeight/2), atmosphereRadius, paint);
        //draw player  ちゃみ
        canvas.drawBitmap(spaceCat, (catX - spaceCat.getWidth() / 2), (catY - spaceCat.getHeight() / 2), null);

        //if spawn is true allow drawing debrie's
        if(spawn) {
            for(Debrie d: spacejunk) {
                d.doDraw(canvas);
            }
        }
    }


    /**
     * actionOnTouch listener
     * */
	@Override
	protected void actionOnTouch(float x, float y) {
		//Increase/decrease the speed of the cat making the cat move towards the touch
		catSpeedX = x - catX;
		catSpeedY = y - catY;
	}


    /**
     * actionWhenPhoneMoved
     * @name actionWhenPhoneMoved
     * */
    @Override
    protected void actionWhenPhoneMoved(float xDirection, float yDirection, float zDirection) {
		/*
		Increase/decrease the speed of the cat.
		If the cat moves too fast try and decrease 70f
		If the cat moves too slow try and increase 70f
		 */

        catSpeedX = catSpeedX + 70f * xDirection;
        catSpeedY = catSpeedY - 70f * yDirection;
    }

    /**
     * updateGame
     * @name updateGame
     * @brief Update game events
     * */
    @Override
    protected void updateGame(float secondsElapsed) {
        //Move the player X and Y using the speed (pixel/sec)
        catX = catX + secondsElapsed * catSpeedX;
        catY = catY + secondsElapsed * catSpeedY;

        //update space debrie's
        if(spawn) {
            for(Debrie d: spacejunk) {
                d.update(secondsElapsed);
                //check for collisions simple way
                if(d.isInsideBitmap(catX, catY, spaceCat)) {
                    uiHandler.sendEmptyMessage(3);
                    setState(GameThread.STATE_LOSE);
                    spawn = false;
                }
            }
        }

        //remove debris not on screen
        if(spawn) {
            Iterator<Debrie> i = spacejunk.iterator();
            while (i.hasNext()) {
                Debrie d = i.next();
                if(d.isOutSide(mCanvasWidth, mCanvasHeight)) {
                    i.remove();
                }
            }
        }

        //If spaceship is outside the screen edges -> loss
        if(catY >= mCanvasHeight || catX >= mCanvasWidth || catY <= 0 || catX <= 0) {
            uiHandler.sendEmptyMessage(3);
            setState(GameThread.STATE_LOSE);
            //Chami is too close to earth
        }else if(Math.sqrt(Math.pow((catX-earthX), 2) + Math.pow((catY-earthY),2)) <= atmosphereRadius - 40) {
            uiHandler.sendEmptyMessage(3);
            setState(GameThread.STATE_LOSE);
        }

        //Check perimiter
        if(catY >= mCanvasHeight - (mCanvasHeight * 0.03) || catX >= mCanvasWidth- (mCanvasWidth * 0.03) || catY <= (mCanvasHeight * 0.03) || catX <= (mCanvasWidth * 0.03)) {
            //show warning message -> Warning close to edge
            statusMessage = "Getting too far away!";
        }else if(Math.sqrt(Math.pow((catX-earthX), 2) + Math.pow((catY-earthY),2)) <= atmosphereRadius) {
            //if close to earth atmosphere show warning message
            //show warning message -> Warning close to edge
            statusMessage = "Too close to earth!";
        }else{
            statusMessage = "";
        }

        //if time is more than 0,5 second start spawning
        if(st.getElapsedTimeSecs() > 0.5 && spawn == false) {
            spawn = true;
            Log.d("RMJ","SPAWN OBJECTS KILL THE KITTY");
        }else if(spawn && st.getElapsedTimeSecs() > spawnTime) {
            spawnDebrie();
        }

        //update time
        uiHandler.sendEmptyMessage(1);
        //send debug message for time
        //Log.d("TIMER", "secs: " + st.getElapsedTimeSecs() +"");

        //update warning message
        uiHandler.sendEmptyMessage(2);
    }

    /**
     * spawnDebrie
     * @name spawnDebrie
     * @brief Spawn a new debrie
     * */
    private void spawnDebrie() {
        int debries = 0;
        //make random nr of debrie
        if(spawnTime < 10) {
            debries = r.nextInt(5) + 1;
        }else{ //make it harder
            debries = r.nextInt(8) + 1;
        }
        Log.d("RMJ","Spawning " + debries + " debries.");
        for(int i = 0; i < debries; i++) {
            Debrie d = new Debrie();
            randomStarting(d);
            spacejunk.add(d);
        }

        //increment spawn time
        spawnTime += 1.2;
    }

    /**
     * @name randomStarting
     * @brief random starting point for debrie
     * */
    private void randomStarting(Debrie d) {
        float x = 0;
        float y = 0;
        float sx = 0;
        float sy = 0;

        switch(r.nextInt(4) + 1) {
            case 1: //north
                y = 0;
                x = r.nextInt(mCanvasWidth);
                sx = r.nextInt(60) + 10;
                sy = r.nextInt(60) + 10;
                break;
            case 2: //east
                x = mCanvasWidth;
                y = r.nextInt(mCanvasHeight);
                sx = r.nextInt(55) - 60;
                sy = r.nextInt(120) - 60;
                break;
            case 3: // south
                y = mCanvasHeight;
                x = r.nextInt(mCanvasWidth);
                sx = r.nextInt(60) - 120;
                sy = r.nextInt(60) - 120;
                break;
            case 4:  //west
                x = 0;
                y = r.nextInt(mCanvasHeight);
                sx = r.nextInt(60) + 10;
                sy = r.nextInt(60) - 30;
                break;
        }

        //initiate and select random bitmap
        int i = r.nextInt(3);
        d.initiate(x, y, sx, sy, debries[i]);
    }

    /**
     * updateScoreView
     * @name updateScoreView
     * */
    private void updateScoreView() {
        timeView.setText(st.getElapsedTimeSecs()+"");
    }

    /**
     * updateWarningView
     * @name updateWarningView
     * */
    private void updateWarningView() {
        warningView.setText(statusMessage);
    }

    /**
     * @name updateScore
     * @brief Adds a new score entry to database
     * */
    private void updateScore() {
        score = (int)st.getElapsedTimeSecs();
        Log.d("RMJ","Getting player name");
        promptUsername(score);
    }

    private void promptUsername(int score) {
        final int s = score;
        AlertDialog.Builder alert = new AlertDialog.Builder(t);

        alert.setTitle("What's your name?");
        alert.setMessage("Your survival time " + score);
        // Set an EditText view to get user input
        final EditText input = new EditText(t);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                name = input.getText().toString();

                //talk to SQLite database
                Database db = new Database(t);
                db.storeHighscore(name, s);
                //close the activity
                ((Activity)t).finish();
            }
        });

        //no need for no/cancel button
        /*alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });*/

        alert.show();

        //return name;
        //return true;
    }

    class OkName implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            name = input.getText().toString();
        }
    }
        public void onClick(DialogInterface dialog, int whichButton) {

            // Do something with value!
        }
}


