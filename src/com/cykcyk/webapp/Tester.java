package com.cykcyk.webapp;

public class Tester {
    public static void main (String [] args){
        new PhoneBookServer();

        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new PhoneBookClient("Adam", "localhost");
        new PhoneBookClient("Ewa", "localhost");
    }
}
