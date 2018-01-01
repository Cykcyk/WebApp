package com.cykcyk.webbapp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

class ClientThread implements Runnable {
    private Socket socket;
    private String name;
    private PhoneBookServer phoneBookServer;

    private ObjectOutputStream outputStream = null;


    ClientThread(PhoneBookServer server, Socket socket) {
        phoneBookServer = server;
        this.socket = socket;
        new Thread(this).start();
    }

    public String getName(){ return name; }

    public String toString(){ return name; }

    public void sendMessage(String message){
        try {
            outputStream.writeObject(message);
            if (message.equals("exit")){
                phoneBookServer.removeClient(this);
                socket.close();
                socket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void run(){
        String message;
        try(ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream()); )
        {
            outputStream = output;
            name = (String)input.readObject();
            phoneBookServer.addClient(this);
            while(true){
                message = (String)input.readObject();
                phoneBookServer.printReceivedMessage(this,message);
                if (message.equals("exit")){
                    phoneBookServer.removeClient(this);
                    break;
                }
            }
            socket.close();
            socket = null;
        } catch(Exception e) {
            phoneBookServer.removeClient(this);
        }
    }

}
