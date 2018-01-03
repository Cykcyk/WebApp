package com.cykcyk.webapp;

/*
 *  Autor: Daniel Cyktor
 *   Data: grudzien 2017 r.
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

class ClientThread implements Runnable {
    private Socket socket;
    private String name;
    private PhoneBookServer phoneBookServer;

    private String helpText = "Dostepne komendy:\nLOAD nazwa_pliku - wczytanie danych z pliku o podanej nazwie" +
            "\nSAVE nazwa_pliku - zapis danych do pliku o podanej nazwie\n" +
            "GET imie - pobranie numeru telefonu osoby o podanym imieniu\n" +
            "PUT imie numer - zapis numru telefonu osoby o podanym imieniu\n" +
            "REPLACE imie numer - zamiana numeru telefonu dla osoby o podanym imieniu\n" +
            "DELETE imie - usuniecie z kolekcji osoby o podanym imieniu\n" +
            "LIST - przeslanie listy imion, ktore sa zapamietane w kolekcji\n" +
            "CLOSE - zakonczenie nasluchu polaczen od nowych klientow i zamkniecie gniazda serwera\n" +
            "BYE - zakonczenie komunikacji kilenta z serwerem i zamkniecie strumieni danych oraz gniazda\n" +
            "HELP - wyswietlenie iformacji o dostepnych komendach";

    private ObjectOutputStream outputStream = null;

    ClientThread(String prototypeDisplayValue){
        name = prototypeDisplayValue;
    }

    ClientThread(PhoneBookServer server, Socket socket) {
        phoneBookServer = server;
        this.socket = socket;
        new Thread(this).start();
    }

    String getName(){ return name; }

    public String toString(){ return name; }

    private String[] splitCommand(String message){
        return message.split(" ");
    }

    private void sendCommand(String message){
        try {
            String [] split = splitCommand(message);
            splitCommand(message);
            split[0] = split[0].toLowerCase();
            switch(split[0]){
                case "load": {
                    if(split.length < 2){
                        this.outputStream.writeObject("ERROR bledna nazwa pliku");
                        break;
                    }
                    phoneBookServer.loadFromFile(this, split[1]);
                    break;
                }
                case "save": {
                    if(split.length < 2){
                        this.outputStream.writeObject("ERROR bledna nazwa pliku");
                        break;
                    }
                    phoneBookServer.saveToFile(this, split[1]);
                    break;
                }
                case "get": {
                    if(split.length < 2){
                        this.outputStream.writeObject("ERROR brak podanego uzytkownika w bazie");
                        break;
                    }
                    phoneBookServer.printPhoneBookSubscriber(this, split[1]);
                    break;
                }
                case "put": {
                    if(split.length < 2){
                        this.outputStream.writeObject("ERROR nie mozna wprowadzic podanego uzytkownika");
                        break;
                    }
                    phoneBookServer.putNewSubscriber(this, split[1], split[2]);
                    break;
                }
                case "replace": {
                    if(split.length < 2){
                        this.outputStream.writeObject("ERROR nie mozna wprowadzic podanego uzytkownika");
                        break;
                    }
                    phoneBookServer.replaceSubscriber(this, split[1], split[2]);
                    break;
                }
                case "help": {
                    this.sendMessage(helpText);
                    break;
                }
                case "delete": {
                    if(split.length < 2){
                        this.outputStream.writeObject("ERROR brak podanego uzytkownika w bazie");
                        break;
                    }
                    phoneBookServer.deleteSubscriber(this, split[1]);
                    break;
                }
                case "list": {
                    phoneBookServer.listPhoneBook(this);
                    break;
                }
                case "close": {
                    phoneBookServer.acceptingNewConnections = false;
                    this.outputStream.writeObject("OK");
                    break;
                }
                case "bye": {
                    phoneBookServer.removeClient(this);
                    socket.close();
                    socket = null;
                }
                default: {
                    this.outputStream.writeObject("ERROR niepoprawna komenda");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void run(){
        String message;
        try(ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream()))
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

    void sendMessage(String message) throws IOException {
        outputStream.writeObject(message);
    }

    void sendMessage(String[] message) throws IOException {
        for (String aMessage : message) {
            outputStream.writeObject(aMessage);
        }
    }
}
