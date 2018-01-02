package com.cykcyk.webbapp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

class ClientThread implements Runnable {
    private String [] split;
    private Socket socket;
    private String name;
    private PhoneBookServer phoneBookServer;

    private ObjectOutputStream outputStream = null;

    ClientThread(String prototypeDisplayValue){
        name = prototypeDisplayValue;
    }

    ClientThread(PhoneBookServer server, Socket socket) {
        phoneBookServer = server;
        this.socket = socket;
        new Thread(this).start();
    }

    public String getName(){ return name; }

    public String toString(){ return name; }

    private void splitCommand(String message){
        split = message.split(" ");
    }

    public void sendCommand(String message){
        try {
            message = message + " @";
            splitCommand(message);
            switch(split[0]){
                case "LOAD": {
                    phoneBookServer.loadFromFile(this, split[1]);
                    break;
                }
                case "SAVE": {
                    phoneBookServer.saveToFile(this, split[1]);
                    break;
                }
                case "GET": {
                    phoneBookServer.printPhoneBookSubscriber(this, split[1]);
                    break;
                }
                case "PUT": {
                    phoneBookServer.putNewSubscriber(this, split[1], split[2]);
                    break;
                }
                case "REPLACE": {
                    phoneBookServer.replaceSubscriber(this, split[1], split[2]);
                    break;
                }
                case "DELETE": {
                    phoneBookServer.deleteSubscriber(this, split[1]);
                    break;
                }
                case "LIST": {
                    phoneBookServer.listPhoneBook(this);
                    break;
                }
                case "CLOSE": {
                    break;
                }
                case "BYE": {
                    phoneBookServer.removeClient(this);
                    socket.close();
                    socket = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void run(){
        String message;
        try(ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream()) )
        {
            outputStream = output;
            name = (String)input.readObject();
            phoneBookServer.addClient(this);
            while(true){
                message = (String)input.readObject();
                phoneBookServer.printReceivedCommand(this, message);
                sendCommand(message);
                if (message.equals("BYE")){
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

    public void sendMessage(String message) throws IOException {
        outputStream.writeObject(message);
    }

    public void sendMessage(String[] message) throws IOException {
        for(int i = 0; i < message.length; i++) {
            outputStream.writeObject(message[i]);
        }
    }
}
