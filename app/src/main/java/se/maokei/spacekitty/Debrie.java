/**
 * @file Debrie.java
 * @authors rijo1001
 * */
package se.maokei.spacekitty;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * @class Debrie
 * @brief Class describes a debrie
 */
public class Debrie {
    private float posX;
    private float posY;
    private float speedX;
    private float speedY;
    private int size;
    private Bitmap bitmap;

    Debrie(float x, float y, float sx, float sy,  Bitmap b){
        size = 10;
        this.posX = x;
        this.posY = y;
        this.speedX = sx;
        this.speedY = sy;
        this.bitmap = b;
    }

    /**
     * @constructor Debrie
     * */
    Debrie(){
        size = 10;
        this.posX = 0;
        this.posY = 0;
        this.speedX = 0;
        this.speedY = 0;
        this.bitmap = null;
    }

    public void initiate(float x, float y) {
        this.posX = x;
        this.posY = y;
    }

    /**
     * @name initiate
     * */
    public void initiate(float x, float y, float sx, float sy, Bitmap b) {
        this.posX = x;
        this.posY = y;
        this.speedX = sx;
        this.speedY = sy;
        this.bitmap = b;
    }

    /**
     * @name setSpeed
     * @brief Set debrie speed.
     * */
    public void setSpeed(float sx, float sy) {
        this.speedX = sx;
        this.speedY = sy;
    }

    /**
     * @name outOfSight
     * @brief Is debries outside of given area?
     * */
    public boolean outOfSight(int cw, int ch) {
        //is visible?
        if(posX>0 && posY >0 && posX < cw && posY < ch) {
            return false;
        }
        return true;
    }

    /**
     * @name isInsideBitmap
     * @brief Simple collision detection
     * */
    public boolean isInsideBitmap(float x, float y, Bitmap b) {
        float bw = bitmap.getWidth();
        float bh = bitmap.getHeight();

        //if(x > (posX-bitmap.getWidth()/2) && x < (posX +bitmap.getWidth()/2))
            //if(y > (posY-bitmap.getHeight()/ 2) && y < (posY +bitmap.getHeight()/2))
                //return true;
        if(posX < x + bw && posX + bitmap.getWidth() > x && posY < y + bh && posY + bitmap.getHeight() > y)
            return true;
        return false;
    }

    /**
     * @name isOutSide
     * @brief Check if debrie is outside the screen/canvas
     * */
    public boolean isOutSide(int width, int height) {
        if((posX < -10 && posX > width + 20)) {
            if((posY < -10 && posY > height + 20))
                return true;
        }
        return false;
    }

    /**
     * doDraw
     * @name doDraw
     * @brief draw debrie on canvas.
     * */
    public void doDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, posX - bitmap.getWidth()/2, posY - bitmap.getHeight()/2, null);
    }

    /**
     * @name update
     * @brief update position
     * */
    public void update(float secondsElapsed) {
        posX = posX + secondsElapsed * speedX;
        posY = posY + secondsElapsed * speedY;
    }
}
