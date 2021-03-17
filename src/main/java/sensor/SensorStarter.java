package sensor;



import java.util.Date;

import static java.lang.Thread.yield;

public class SensorStarter {
    public static void main(String[] args) {
        try {
            Sensor sensor = new Sensor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
