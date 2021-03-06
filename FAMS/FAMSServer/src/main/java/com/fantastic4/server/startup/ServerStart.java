package com.fantastic4.server.startup;

import com.fantastic4.common.dto.SensorDTO;
import com.fantastic4.common.services.custom.SensorService;
import com.fantastic4.server.services.impl.ServicesFactoryImpl;
import com.fantastic4.server.services.impl.custom.SensorServiceImpl;


import javax.swing.*;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerStart {
	
    public static void main(String[] args){
        System.setProperty("java.rmi.server.hostname","localhost");
        System.setProperty("java.security.policy", "file:./server.policy");

//        System.setProperty("java.rmi.server.hostname","127.0.0.1");
        System.setProperty("java.security.policy","security.policy");
        System.setSecurityManager(new RMISecurityManager());
        try {
            if (true) {
//                if(System.getSecurityManager() == null ){
//                    System.setSecurityManager( new RMISecurityManager() );
//                }
                Registry registry = LocateRegistry.createRegistry(5050);
                registry.rebind("fams", ServicesFactoryImpl.getInstance());
                System.out.println("Server has been started successfully");
                SensorServiceImpl sensorService = new SensorServiceImpl();
                SensorDTO sensorDTO = new
                                SensorDTO("S004", 3, 3,3,3, false);
                System.out.println(sensorDTO.toString());

                System.out.println(sensorService.addSensor(sensorDTO));
                Timer timer = new Timer();
                timer.schedule(new checkStatus(),0,5000);


            } else {
                JOptionPane.showMessageDialog(null,
                        "API is not connected", "Error", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class checkStatus extends TimerTask{
        private final HttpClient client;

        public checkStatus() {
            client = HttpClient.newHttpClient();
        }

        @Override
        public void run() {
            try {
                SensorServiceImpl sensorService = new SensorServiceImpl();
                List<SensorDTO> sensorDTOList = sensorService.getAllSensors();
                for (SensorDTO sensor:sensorDTOList
                ) {
                    if(sensor.getLatestCO2Level() >= 5 || sensor.getLatestSmokeLevel() >= 5){
                        //Implement and Call Endpoint
                        HttpRequest request1 = HttpRequest.newBuilder()
                                .uri(URI.create("http://localhost:9090/send-mail"))
                                .build();

                        HttpRequest request2 = HttpRequest.newBuilder()
                                .uri(URI.create("http://localhost:9091/send-sms"))
                                .build();

                        System.out.println("Email Sent");
                        System.out.println("SMS Sent");

                        //HttpResponse response1 = client.send(request1, HttpResponse.BodyHandlers.discarding());
                        //HttpResponse response2 = client.send(request2, HttpResponse.BodyHandlers.discarding());
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
