package com.zebro.isel.zebro;

import android.util.Log;

import java.io.BufferedReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;
import java.util.Map;

/**
 * Created by ASUS on 28/1/2559.
 */
public class LogReceiver extends Thread {

    private final double freq = 0.5; // request freq in Hz
    private final int timeout = 10000;

    private MapsActivity mapsActivity ;

    private String serverHostname ;
    //BufferedReader inFromUser ;
    private DatagramSocket clientSocket ;
    private int targetPort ;
    private InetAddress IPAddress ;
    private int mode ;


    byte[] sendData = new byte[1024];
    byte[] receiveData = new byte[1024];
    public LogReceiver(MapsActivity m , int port, String ip , int mode){
        try{
            this.mode = mode ;
            serverHostname = new String (ip);
            //BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            mapsActivity = m;
            targetPort = port ;
            clientSocket = new DatagramSocket();
            IPAddress = InetAddress.getByName(serverHostname);
        }catch(Exception e) {
            System.out.println(e);
        }
    }
    public void run(){
        try {
            System.out.println ("Attemping to connect to " + IPAddress + ") via UDP port " + targetPort);
            while(true){
                String message = String.valueOf(mode);
                sendData = message.getBytes();

                System.out.println("Send Request " + sendData.length + " bytes Message: " + message);
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, targetPort);

                clientSocket.send(sendPacket);

                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                System.out.println ("Waiting for return packet");
                clientSocket.setSoTimeout(timeout);

                try {
                    clientSocket.receive(receivePacket);
                    String modifiedSentence = new String(receivePacket.getData());

                    InetAddress returnIPAddress = receivePacket.getAddress();

                    int port = receivePacket.getPort();

                    System.out.println ("From server at: " + returnIPAddress + ":" + port);
                    System.out.println("Message: " + modifiedSentence);

                    mapsActivity.updateNodeLocation(modifiedSentence);

                }
                catch (Exception ste)
                {
                    System.out.println ("Timeout Occurred: Packet assumed lost" + ste.toString());
                }

                Thread.sleep((int)(1000/freq));
            }

        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }

    }
    public void kill(){
        clientSocket = null ;
    }
    /*

    InetAddress myIP ;
    DatagramSocket serverSocket ;
    byte[] receiveData = new byte[1024];
    int port ;

    public LogReceiver (MapsActivity mapsActivity,int port , String myIP){
        Log.i("Map" , "CREATED LOG RECEIVER");
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
                System.out.println("BEFORE RECEIVE");
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                serverSocket.receive(receivePacket);
                System.out.println("RECEIVED");

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
                }* /
            }
        } catch (Exception e) {
            System.out.println(e + " at "+ serverSocket.getPort());
            System.exit(1);
        }
    }
    */
}
