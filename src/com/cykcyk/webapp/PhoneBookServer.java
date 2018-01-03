package com.cykcyk.webapp;

/*
 *  Program serwera
 *
 *  Autor: Daniel Cyktor
 *   Data: grudzien 2017 r.
 */

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class PhoneBookServer extends JFrame implements ActionListener, Runnable{

    private static final long serialVersionUID = 1L;

    private static final int SERVER_PORT = 25000;

    private PhoneBook mainPhoneBook = new PhoneBook();
    private JLabel actionLabel = new JLabel("Akcje: ");
    private JLabel clientLabel = new JLabel("Klient: " );
    private JComboBox<ClientThread> clientComboBox = new JComboBox<>();
    private JTextArea actionTextArea = new JTextArea(15,18);
    Boolean acceptingNewConnections = true;


    public static void main(String [] args){
        new PhoneBookServer();
    }

    PhoneBookServer(){
        super("Server");
        setSize(260, 400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.add(clientLabel);
        panel.add(clientComboBox);
        clientComboBox.setPrototypeDisplayValue(new ClientThread("####################"));
        panel.add(actionLabel);
        panel.add(actionTextArea);
        actionTextArea.setLineWrap(true);
        actionTextArea.setWrapStyleWord(true);
        actionTextArea.setEditable(false);
        JScrollPane actionTextAreaScroll = new JScrollPane(actionTextArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panel.add(actionTextAreaScroll);
        setResizable(false);
        setContentPane(panel);
        setVisible(true);
        new Thread(this).start();
    }

    synchronized void printReceivedCommand(ClientThread client, String message){
        String text = actionTextArea.getText();
        actionTextArea.setText(client.getName() + " >>> " + message + "\n" + text);
    }

    synchronized void addClient(ClientThread client){
        clientComboBox.addItem(client);
    }

    synchronized void removeClient(ClientThread client){
        clientComboBox.removeItem(client);
    }

    void saveToFile(ClientThread client, String fileName){
        try {
            mainPhoneBook.saveToFile(fileName);
            client.sendMessage("OK");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void loadFromFile(ClientThread client, String fileName) throws IOException {
        try {
            mainPhoneBook.loadFromFile(fileName);
            client.sendMessage("OK");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void putNewSubscriber(ClientThread client, String name, String number){
        mainPhoneBook.putNewSubscriber(name, number);
        try {
            client.sendMessage("OK");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void listPhoneBook(ClientThread client){
        String [] toPrint = mainPhoneBook.listPhoneBook();
        try {
            client.sendMessage(toPrint);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void replaceSubscriber(ClientThread client, String name, String number){
        mainPhoneBook.replaceSubscriber(name, number);
        try {
            client.sendMessage("OK");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void deleteSubscriber(ClientThread client, String name){
        mainPhoneBook.deleteSubscriber(name);
        try {
            client.sendMessage("OK");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void printPhoneBookSubscriber(ClientThread client, String name){
        Map.Entry<String, String> temp = mainPhoneBook.getEntryByName(name);
        try {
            client.sendMessage(temp.toString().replace("=", " "));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void actionPerformed(ActionEvent event) {

    }

    @Override
    public void run() {
        boolean socket_created = false;
        try (ServerSocket server = new ServerSocket(SERVER_PORT)) {
            String host = InetAddress.getLocalHost().getHostName();
            System.out.println("Serwer został uruchomiony na hoscie " + host);
            socket_created = true;
            while (true) {
                Socket socket = new Socket();
                if(acceptingNewConnections) {
                    socket = server.accept();
                }
                else{
                    server.close();
                }
                if (socket != null && acceptingNewConnections) {
                    new ClientThread(this, socket);
                }
            }
        } catch (IOException e) {
            System.out.println(e);
            if (!socket_created) {
                JOptionPane.showMessageDialog(null, "Gniazdko dla serwera nie może być utworzone");
                System.exit(0);
            } else {
                JOptionPane.showMessageDialog(null, "BLAD SERWERA: Nie mozna polaczyc sie z klientem ");
            }
        }
    }
}

