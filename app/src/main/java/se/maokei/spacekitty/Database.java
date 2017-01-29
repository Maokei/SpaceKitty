/**
 * @file Database.java
 * @author rijo1001
 * */
package se.maokei.spacekitty;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

/**
 * Created by rijo1001 on 2015-06-06.
 */
public class Database extends SQLiteOpenHelper{
    private static final String DB_TABLE = "HIGHSCORES";
    private static final String COL_ID = "ID";
    private static final String COL_NAME = "NAME";
    private static final String COL_SCORE = "SCORE";

    public Database(Context context) {
        super(context, "highscores.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //ID NAMN POÄNG
        String sql = String.format("create table %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL, %s INTEGER NOT NULL)",DB_TABLE,COL_ID,COL_NAME,COL_SCORE);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //for upgrading database
    }

    public void storeHighscore(String name, int score) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_SCORE, score);
        db.insert(DB_TABLE, null, values);
        db.close();
    }

    public ArrayList<String> getHighscores() {
        ArrayList<String> highscores = new ArrayList<String>();
        SQLiteDatabase db = getReadableDatabase();
        String sql = String.format("SELECT %s, %s FROM %s ORDER BY %s DESC",COL_NAME, COL_SCORE, DB_TABLE, COL_SCORE);
        Cursor cursor = db.rawQuery(sql, null);

        while(cursor.moveToNext()) {
            StringBuilder sb = new StringBuilder();
            int id = 0;
            sb.append(cursor.getString(0));
            sb.append(" : ");
            sb.append(cursor.getInt(1));
            highscores.add(sb.toString());
        }

        db.close();
        //return highscores
        return highscores;
    }
}
