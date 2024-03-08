package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Scanner;

public class Client {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    private final Scanner scanner = new Scanner(System.in);

    private boolean connected = false;


    /*
        We will open two threads for receiving and sending messages.
     */
    private final Thread sendMessage = new Thread(new Runnable() {
        @Override
        public void run() {
            while (connected) {
                String msg = scanner.nextLine();
                String[] parsedInput = msg.split(" ");
                String command = parsedInput[0];
                switch (command) {
                    // Verify the new user args before sending them to the server.
                    case "newuser" -> {
                        if (verifyNewUser(parsedInput[1], parsedInput[2])) {
                            out.println(msg);
                        } else {
                            System.out.println("Invalid new user attempt");
                        }
                    }
                    // Server will handle the commands itself.
                    case "login", "send", "who" -> out.println(msg);
                    case "logout" -> {
                        out.println(msg);
                        try {
                            stopConnection();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        connected = false;
                        System.out.println("Successfully logged out");
                    }
                    default -> System.out.println("Command not recognized");
                }
            }
        }
    });
    private final Thread readMessage = new Thread(new Runnable() {
        @Override
        public void run() {
            while (connected) {
                try {
                    // If a message is on in, print it.
                    if (in.ready()) {
                        String msg = in.readLine();
                        System.out.println(msg);
                    }

                }
                catch (IOException ignored) {

                }
            }
        }
    });

    public Client() {
    }
    private boolean verifyNewUser(String userID, String password) {
        return (userID.length() >= 3 && userID.length() <= 32) && (password.length() >= 4 && password.length() <= 8);
    }

    public void startConnection(String ip, int port) throws IOException {
        try {
            clientSocket = new Socket(ip, port);
        }
        catch (ConnectException e) {
            System.out.println("There was a problem connecting to the server");
            e.printStackTrace();
            System.exit(1);
        }
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        connected = true;
    }

    public void inputLoop() {
        System.out.println("Chatroom Client version 2");
        try {
            startConnection("127.0.0.1", 1060);

        } catch (IOException e) {
            e.printStackTrace();
        }
        sendMessage.start();
        readMessage.start();
    }
    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

}
