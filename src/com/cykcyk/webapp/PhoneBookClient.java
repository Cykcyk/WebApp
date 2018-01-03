package com.cykcyk.webapp;

/*
 *  Program Klienta
 *
 *  Autor: Daniel Cyktor
 *   Data: grudzien 2017 r.
 */

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;



class PhoneBookClient extends JFrame implements ActionListener, Runnable{

    private static final long serialVersionUID = 1L;

    public static void main(String[] args) {
        String name;
        String host;

        host = JOptionPane.showInputDialog("Podaj adres serwera");
        name = JOptionPane.showInputDialog("Podaj nazwe klienta");
        if (name != null && !name.equals("")) {
            new PhoneBookClient(name, host);
        }
    }

    private JTextField messageField = new JTextField(20);
    private JTextArea  textArea     = new JTextArea(15,18);

    private static final int SERVER_PORT = 25000;
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
    private String name;
    private String serverHost;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private JButton helpButton = new JButton("Pomoc");

    PhoneBookClient(String name, String host) {
        super(name);
        this.name = name;
        this.serverHost = host;
        setSize(270, 400);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                try {
                    outputStream.close();
                    inputStream.close();
                    socket.close();
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
            @Override
            public void windowClosed(WindowEvent event) {
                windowClosing(event);
            }
        });
        JPanel panel = new JPanel();
        JLabel messageLabel = new JLabel("Polecenie dla serwera: ");
        JLabel textAreaLabel = new JLabel("Dialog: ");
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        DefaultCaret caret = (DefaultCaret)textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        panel.add(messageLabel);
        panel.add(messageField);
        messageField.addActionListener(this);
        panel.add(textAreaLabel);
        JScrollPane scroll_bars = new JScrollPane(textArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panel.add(scroll_bars);
        panel.add(helpButton);
        helpButton.addActionListener(this);
        setContentPane(panel);
        setVisible(true);
        new Thread(this).start();
    }

    private synchronized void printReceivedMessage(String message){
        String tmp_text = textArea.getText();
        textArea.setText(tmp_text + ">>> " + message + "\n");
    }

    private synchronized void printSentMessage(String message) {
        String text = textArea.getText();
        if (!message.equals("")) {
            textArea.setText(text + "<<< " + message + "\n");
        }
    }

    private void showHelp(){
        JOptionPane.showMessageDialog(null, helpText);
    }

    public void actionPerformed(ActionEvent event) {
        String message;
        Object source = event.getSource();
        if (source == messageField) {
            try {
                message = messageField.getText();
                    outputStream.writeObject(message);
                    printSentMessage(message);
                    messageField.setText("");
                    if (message.equals("BYE")) {
                        inputStream.close();
                        outputStream.close();
                        socket.close();
                        setVisible(false);
                        dispose();
                        return;
                    }
            }
            catch(IOException e) {
                System.out.println("Wyjatek klienta " + e);
            }
        }
        if(source == helpButton){
            showHelp();
        }
        repaint();
    }

    public void run(){
        if (serverHost.equals("")) {
            serverHost = "localhost";
        }
        try{
            socket = new Socket(serverHost, SERVER_PORT);
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(name);
        } catch(IOException e){
            JOptionPane.showMessageDialog(null, "Polaczenie sieciowe dla klienta nie moze byc utworzone");
            setVisible(false);
            dispose();
            return;
        }
        try{
            while(true){
                String message = (String)inputStream.readObject();
                printReceivedMessage(message);
                if(message.equals("BYE")){
                    inputStream.close();
                    outputStream.close();
                    socket.close();
                    setVisible(false);
                    dispose();
                    break;
                }
            }
        } catch(Exception e){
            JOptionPane.showMessageDialog(null, "Polaczenie sieciowe dla klienta zostalo przerwane");
            setVisible(false);
            dispose();
        }
    }

}