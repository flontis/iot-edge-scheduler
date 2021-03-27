package Testclasses.sensor;



//TODO comments

import Webserver.Webserver;

import java.net.UnknownHostException;

public class SensorStarter {
    public static void main(String[] args) throws UnknownHostException {
        Webserver server = new Webserver();
        server.start();
//        try {
//            Sensor sensor = new Sensor(2);
//            new Thread(sensor).start();
//        } catch (InterruptedException e) {
//            System.out.println("Error");
//            e.printStackTrace();
//        }






    }
}
