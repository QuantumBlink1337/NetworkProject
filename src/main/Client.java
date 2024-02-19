package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    private String userID;
    private boolean isLoggedIn = false;

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void inputLoop() throws IOException {
        Scanner scanner = new Scanner(System.in);
        String input;
        boolean validInput = false;
        System.out.println("Chatroom main.Client v. 0.0.1");
        try {
            startConnection("127.0.0.1", 1060);

        }
        catch (IOException e ) {
            e.printStackTrace();
        }

        while (true) {
            boolean validCommand;
            boolean validUserID;
            boolean validPass;
            if (in.ready()) {
                System.out.println(in.readLine());
            }
            input = scanner.nextLine();
            String[] parsedInput = input.split(" ");
            if (parsedInput[0].equals("login")) {
                isLoggedIn = login(parsedInput[1], parsedInput[2]);
            }
            else if (parsedInput[0].equals("send")) {
                if (isLoggedIn) {
                    send(input.substring(5));
                }
                else {
                    System.out.println("Not logged in");
                }
            }




        }
    }
    private void newUser(String userID, String password) {

    }

    private void send(String message) {
        if (message.length() > 256) {
            System.out.println("Message too long");
            return;
        }
        try {
            if (sendMessage("send").equals("Ready")) {
                String resp = sendMessage(userID + "|" + message);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private boolean login(String userID, String password) {
        try {
            String validation = sendMessage("login");
            if (validation == null) {
                throw new IOException();
            }
            if (validation.equals("Ready")) {
                String resp = sendMessage("(" + userID + ", "+password + ")");
                if (resp.equals("Login accepted")) {
                    this.userID = userID;
                    System.out.println("Login confirmed");
                    return true;
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Denied, login does not match");
        return false;
    }



    public String sendMessage(String msg) throws IOException {
        out.println(msg);
        String resp = in.readLine();
        return resp;
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

}
