package com.cykcyk.webapp;

/*
 *  Autor: Daniel Cyktor
 *   Data: grudzien 2017 r.
 */

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
