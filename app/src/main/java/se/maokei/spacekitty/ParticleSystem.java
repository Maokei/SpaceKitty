package se.maokei.spacekitty;

/**
 * Created by maokei on 2015-06-06.
 */
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class ParticleSystem {
    private Particle particles[];

    private int startYPos;
    private int startXPos;
    private int xPosRange;
    private int minSpeed;
    private int speedRange;
    private Bitmap bitmap;

    public ParticleSystem(int startYPos, int startXPos,
                          int xPosRange, int minSpeed, int speedRange,
                          Bitmap bitmap) {
        this.startYPos = startYPos;
        this.startXPos = startXPos;
        this.xPosRange = xPosRange;
        this.minSpeed = minSpeed;
        this.bitmap = bitmap;
        this.speedRange = speedRange;

        particles = new Particle[20];

        for (int i = 0; i < particles.length; i++) {
            particles[i] = new Particle(startYPos, startXPos,
                    xPosRange, minSpeed, speedRange,
                    bitmap);
        }
    }

    public void doDraw(Canvas canvas) {
        for(int i = 0; i < particles.length; i++) {
            Particle particle = particles[i];
            particle.doDraw(canvas);
        }
    }

    public void updatePhysics(int altDelta) {
        for(int i = 0; i < particles.length; i++) {
            Particle particle = particles[i];
            particle.updatePhysics(altDelta);

            // If this particle is completely out of sight
            // replace it with a new one.
            if(particle.outOfSight()) {
                particles[i] = new Particle(startYPos, startXPos,
                        xPosRange, minSpeed, speedRange,
                        bitmap);
            }
        }
    }
}