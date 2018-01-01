package com.cykcyk.webbapp;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class PhoneBookServer extends JFrame implements ActionListener, Runnable{

    private static final long serialVersionUID = 1L;

    static final int SERVER_PORT = 25000;

    private JLabel actionLabel = new JLabel("Akcje");
    private JTextArea actionTextArea = new JTextArea(15,18);
    private JScrollPane actionTextAreaScroll = new JScrollPane(actionTextArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                                                     ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    public static void main(String [] args){
        new PhoneBookServer();
    }

    PhoneBookServer(){
        super("Server");
        setSize(300,340);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.add(actionLabel);
        panel.add(actionTextArea);
        actionTextArea.setLineWrap(true);
        actionTextArea.setWrapStyleWord(true);
        actionTextArea.setEditable(false);
        panel.add(actionTextAreaScroll);
        setContentPane(panel);
        setVisible(true);
        new Thread(this).start();
    }

    synchronized public void printReceivedCommand(ClientThread client, String message){
        String text = actionTextArea.getText();
        actionTextArea.setText(client.getName() + " >>> " + message + "\n" + text);
    }

    @Override
    public void actionPerformed(ActionEvent event) {

    }

    @Override
    public void run() {
        boolean socket_created = false;

        try (ServerSocket server = new ServerSocket(SERVER_PORT)) {
            String host = InetAddress.getLocalHost().getHostName();
            System.out.println("Serwer zosta� uruchomiony na hoscie " + host);
            socket_created = true;

            while (true) {
                Socket socket = server.accept();
                if (socket != null) {
                    new ClientThread(this, socket);
                }
            }
        } catch (IOException e) {
            System.out.println(e);
            if (!socket_created) {
                JOptionPane.showMessageDialog(null, "Gniazdko dla serwera nie mo�e by� utworzone");
                System.exit(0);
            } else {
                JOptionPane.showMessageDialog(null, "BLAD SERWERA: Nie mozna polaczyc sie z klientem ");
            }
        }
    }
}

