package sensor;


import java.util.Random;
import java.util.UUID;



public class Sensor {

    public Sensor() throws InterruptedException {

        UUID sensorID = UUID.randomUUID();
        // use gaussian distributed values
        Random r = new Random();
        //n values per second
        int N = 2;
        long taskTime;
        // ratio of sleep time
        long sleepTime = 1000/N;
        while (true) {
            taskTime = System.currentTimeMillis();
            System.out.println(sensorID + ": " + r.nextGaussian());
            taskTime = System.currentTimeMillis()-taskTime;
            if (sleepTime-taskTime > 0 ) {
                Thread.sleep(sleepTime-taskTime);
            }
        }



    }
}
