package com.cykcyk.webbapp;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class PhoneBookClient extends JFrame implements ActionListener, Runnable{

    private static final long serialVersionUID = 1L;

    public static void main(String [] args){
        String clientName;
        String hostAdress;

        hostAdress = JOptionPane.showInputDialog("Podaj adres serwera");
        clientName = JOptionPane.showInputDialog("Podaj nazwe klienta");
        if(clientName == null && clientName.equals("")){
            new PhoneBookClient(clientName, hostAdress);
        }
    }

    static final int SERVER_PORT = 25000;
    private String name;
    private String serverHost;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    PhoneBookClient(String name, String serverHost){
        super(name);
        this.name = name;
        this.serverHost = serverHost;
        setSize(300, 310);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try{
                    outputStream.close();
                    inputStream.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void windowClosed(WindowEvent event){
                windowClosing(event);
            }
        });

    }



}
