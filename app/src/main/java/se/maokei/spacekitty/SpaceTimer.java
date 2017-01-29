/**
 * @file SpaceTimer
 *
 * */
package se.maokei.spacekitty;

/**
 * Created by maokei on 2015-06-01.
 */
public class SpaceTimer {
    private long startTime = 0;
    private boolean running = false;
    private long currentTime = 0;

    public void start() {
        this.startTime = System.currentTimeMillis();
        this.running = true;
    }

    public void stop() {
        this.running = false;
    }

    public void pause() {
        this.running = false;
        currentTime = System.currentTimeMillis() - startTime;
    }
    public void resume() {
        this.running = true;
        this.startTime = System.currentTimeMillis() - currentTime;
    }


    /**
     * @return elaspsed time in milliseconds
     * */
    public long getElapsedTimeMili() {
        long elapsed = 0;
        if (running) {
            elapsed =((System.currentTimeMillis() - startTime)/100) % 1000 ;
        }
        return elapsed;
    }

    public long getElapsedTimeHundredthsSecond() {
        long elapsed = 0;
        if(running ){
            elapsed = ((System.currentTimeMillis() - startTime) + 5) / 10;
        }
        return elapsed;
    }

    /**
     * @return elaspsed time in seconds
     * */
    public long getElapsedTimeSecs() {
        long elapsed = 0;
        if (running) {
            elapsed = ((System.currentTimeMillis() - startTime) / 1000) % 60;
        }
        return elapsed;
    }

    /**
     * @return elaspsed time in minutes
     * */
    public long getElapsedTimeMin() {
        long elapsed = 0;
        if (running) {
            elapsed = (((System.currentTimeMillis() - startTime) / 1000) / 60 ) % 60;
        }
        return elapsed;
    }

    /**
     * @return elapsed time hours
     * */
    public long getElapsedTimeHour() {
        long elapsed = 0;
        if (running) {
            elapsed = ((((System.currentTimeMillis() - startTime) / 1000) / 60 ) / 60);
        }
        return elapsed;
    }

    /**
     * getSec
     * ss:ss
     * */
    public String getSSss() {
        long hundreth = 0;
        String elapsed = "";
        if(running) {
            hundreth = getElapsedTimeHundredthsSecond();
            elapsed = (hundreth/100) + ":" + hundreth;
        }
        return elapsed;
    }
}