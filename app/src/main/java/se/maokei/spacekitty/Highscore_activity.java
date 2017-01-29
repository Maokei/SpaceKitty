/**
 * @file Highscore_activity.java
 * @authors rijo1001
 * */
package se.maokei.spacekitty;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class Highscore_activity extends Activity {
    private Database db;
    private ArrayList<String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        //Database
        setupDatbase();
        data = db.getHighscores();
        //log out score! test
        if(data.size() > 1)
            Log.d("RMJ score", data.get(0));

        ListView listView = (ListView) findViewById(R.id.scoreView);
        //get list view
        //ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.activity_highscore, data);
        //ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.activity_highscore, data);

        ArrayAdapter adapter;

        adapter = new ArrayAdapter<String>(this,R.layout.score_item, data);

        //populate list view
        listView.setAdapter(adapter);
    }

    public void setupDatbase() {
        db = new Database(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_highscore_activity, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }
}
