/**
 * @file Particle.java
 * @auhour rijo1001@student.miun.se
 * @date 2015-05-25
 * */
package se.maokei.spacekitty;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * @class Particle
 * @brief Class describes a particle
 */
/*public class Particle {
    private float posX;
    private float posY;
    private float speed;
    private Bitmap bitmap;

    public Particle(float startX, float startY, int speed, Bitmap bitmap) {
        this.posX = startX;
        this.posY = startY;
        this.speed = speed;
        this.bitmap = bitmap;

    }

    public void updatePhysics() {

    }

    public void doDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, posX, posY, null);
    }

    public boolean outOfSight(int cw, int ch) {
        //is visible?
        if(posX>0 && posY >0 && posX < cw && posY < ch) {
            return false;
        }
        return true;
    }

    public float getSpeedX() {
        return speed;
    }

    public float getSpeedY() {
        return speed;
    }
}*/


import android.graphics.Bitmap;
import android.graphics.Canvas;

class Particle {
    private int xpos;
    private int ypos;
    private int speed;
    private Bitmap bitmap;

    public Particle(int startYPos, int startXPos,
                    int xPosRange, int minSpeed, int speedRange,
                    Bitmap bitmap) {
        xpos = startXPos + (int) (Math.random() * xPosRange);
        ypos = startYPos;

        this.speed = (int) (minSpeed + Math.random() * speedRange);
        this.bitmap = bitmap;
    }

    public void updatePhysics(int distChange) {
        ypos -= distChange * speed;
    }

    public void doDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, xpos, ypos, null);
    }

    public boolean outOfSight() {
        return ypos <= -1 * bitmap.getHeight();
    }
}