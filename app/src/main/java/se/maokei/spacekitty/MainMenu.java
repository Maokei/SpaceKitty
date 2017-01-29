/**
 * @file MainMenu.java
 * @author rijo1001@student.miun.se
 * @date 2015-05-25
 * */
package se.maokei.spacekitty;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * @class MainMenu
 * @extends Activity
 * @brief This class describes the main menu of the application.
 * */
public class MainMenu extends Activity {
    //Music players
    private MediaPlayer menuMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //setup background music
        menuMusic = MediaPlayer.create(this, R.raw.catinspace_hq);
        menuMusic.start();


        //setup play button
        setupPlayButton();
        setupHighscoreButton();
    }

    /**
     * startGame
     * @name startGame
     * @brief Start game activity
     * */
    private void startGame() {
        Intent gameTest = new Intent(this, GameActivity.class);
        startActivity(gameTest);
    }

    /**
     * showHighscore
     * @name showHighscore
     * @brief Start highscore activity
     * */
    private void showHighscore() {
        Intent test = new Intent(this, Highscore_activity.class);
        startActivity(test);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        menuMusic.release();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        menuMusic.release();
    }

    /**
     * setupHighscoreButton
     * @name setupHighscoreButton
     * */
    public void setupHighscoreButton() {
        Button highscoreBtn = (Button) findViewById(R.id.highscore_button);
        highscoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Getting highscores!", Toast.LENGTH_SHORT).show();
                showHighscore();
            }
        });
    }

    /**
     * setupPlayButton
     * @name setupPlayButton
     * */
    public void setupPlayButton() {
        Button play = (Button) findViewById(R.id.play_button);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("RMJ","Play button clicked");
                Toast.makeText(getApplicationContext(), "Launching kitty into space!", Toast.LENGTH_SHORT).show();
                startGame();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }/*else if (id == R.id.Mute) {

        }*/

        return super.onOptionsItemSelected(item);
    }
}
