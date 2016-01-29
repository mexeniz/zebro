package com.zebro.isel.zebro;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;

/**
 * Created by ASUS on 28/1/2559.
 */
public class LogReceiver extends Thread {

    MapsActivity mapsActivity ;
    InetAddress myIP ;
    DatagramSocket serverSocket ;
    byte[] receiveData = new byte[1024];
    int port ;

    public LogReceiver (MapsActivity mapsActivity,int port , String myIP){
        Log.d("Log Receiver" , "Create socket ip="+myIP+" port="+port);
        try{
            this.mapsActivity = mapsActivity ;
            this.myIP = InetAddress.getByName(myIP);
            this.port = port ;
            serverSocket = new DatagramSocket(port);
        }catch(Exception e){
            System.out.println(e);
        }
    }
    @Override
    public void run(){
        try {
            while(true)
            {

                receiveData = new byte[1024];

                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                serverSocket.receive(receivePacket);

                String message = new String(receivePacket.getData());

                mapsActivity.updateNodeLocation(message);
                /*InetAddress IPAddress = receivePacket.getAddress();

                int port = receivePacket.getPort();

                if(!BlindCornerVANET.myCar.carID.equals(message.split(" ")[0] ) && !myIP.toString().equals(IPAddress.toString()))
                {
                    //Message is not mine!
                    System.out.println ("Receive Beacon From: " + IPAddress + ":" + port + " Message: " + message);
                    boolean mustReb = BlindCornerVANET.validateMessage(IPAddress.toString(),message);
                    if(mustReb){
                        String rebMessage = message ;
                        sendData = rebMessage.getBytes();
                        System.out.println ("Rebroadcast Beacon " + sendData.length + " bytes Message: " + rebMessage);
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, targetIP, this.port);
                        serverSocket.send(sendPacket);
                    }
                }*/
            }
        } catch (Exception e) {
            System.out.println(e + " at "+ serverSocket.getPort());
            System.exit(1);
        }
    }
}
