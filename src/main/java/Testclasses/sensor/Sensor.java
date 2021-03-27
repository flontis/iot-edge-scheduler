package Testclasses.sensor;


import java.util.Random;
import java.util.UUID;
//todo comments

public class Sensor implements Runnable {

    private int N;
    private UUID sensorID;

    public int getN() {
        return N;
    }

    public void setN(int n) {
        N = n;
    }

    public Sensor(int N) throws InterruptedException {

        this.sensorID = UUID.randomUUID();
        this.N = N;


    }


    @Override
    public void run() {
        long taskTime;
        // use gaussian distributed values
        Random r = new Random();

        while (true) {
            long sleepTime = 1000 / this.N;
            taskTime = System.currentTimeMillis();
            System.out.println(sensorID + " produced: " + r.nextGaussian());
            taskTime = System.currentTimeMillis() - taskTime;
            if (sleepTime - taskTime > 0) {
                try {
                    Thread.sleep(sleepTime - taskTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
