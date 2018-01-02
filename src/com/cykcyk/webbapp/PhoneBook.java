package com.cykcyk.webbapp;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PhoneBook {
    private ConcurrentHashMap<String, String> phoneBookSubscribers = new ConcurrentHashMap<>();

    Map.Entry<String, String> getEntryByName (String name){
       for (Map.Entry<String, String> entry : phoneBookSubscribers.entrySet()){
           if(entry.getKey().equals(name)){
               return entry;
           }
       }
        return null;
    }

    PhoneBook (){


    }

    void putNewSubscriber(String name, String number){
        phoneBookSubscribers.put(name, number);
    }

    void replaceSubscriber(String name, String number) {
        for (Map.Entry<String, String> entry : phoneBookSubscribers.entrySet()) {
            if (entry.getKey().equals(name)) {
                entry.setValue(number);
            }
        }
    }

    String[] listPhoneBook(){
        String [] toPrint = new String[phoneBookSubscribers.size()];
        int i = 0;
        for (Map.Entry<String,String> entry : phoneBookSubscribers.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            toPrint[i] = (key + " " + value);
            i++;
        }
        return toPrint;

    }

    void deleteSubscriber(String name){
        for (Map.Entry<String, String> entry : phoneBookSubscribers.entrySet()) {
            if (entry.getKey().equals(name)) {
               phoneBookSubscribers.remove(entry.getKey());
            }
        }
    }

    public void saveToFile(String fileName){
        try{
            File file = new File(fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            PrintWriter printWriter = new PrintWriter(fileOutputStream);
            for(Map.Entry<String, String> m : phoneBookSubscribers.entrySet()){
                printWriter.println(m.getKey() + "#" + m.getValue());
            }
            printWriter.flush();
            printWriter.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadFromFile(String fileName){
        try{
            File toRead = new File(fileName);
            FileInputStream fileInputStream = new FileInputStream(toRead);
            Scanner scanner = new Scanner(fileInputStream);
            String currentLine;
            phoneBookSubscribers.clear();
            while(scanner.hasNext()){
                currentLine = scanner.nextLine();
                StringTokenizer stringTokenizer = new StringTokenizer(currentLine, "#", false);
                phoneBookSubscribers.put(stringTokenizer.nextToken(), stringTokenizer.nextToken());
            }
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
