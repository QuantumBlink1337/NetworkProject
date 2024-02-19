package main;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private int ID;
    private boolean isLoggedin;

    public ClientHandler(Socket clientSocket, int ID) throws IOException {
        this.clientSocket = clientSocket;
        this.ID  = ID;
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }
    public void run() {

        String inputLine;
        while (true) {
            try {
                inputLine = in.readLine();
                if (inputLine.equals("logout")) {
                    logout();
                }

                if ("login".equals(inputLine)) {
                    handleLogin();
                }
                if ("send".equals(inputLine)) {
                    handleMessage();
                }
                if (".".equals(inputLine)) {
                    out.println("goodbye");
                    break;
                } else {
                    out.println(inputLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
    private void logout() throws IOException {
        isLoggedin = false;
        Server.getConnectedClients().remove(this);
        clientSocket.close();
    }
    private void handleLogin() throws IOException {
        out.println("Ready");
        String userString = in.readLine();
        String filepath = "src/resources/users.txt";
        final BufferedReader bufferedReader = new BufferedReader(new FileReader(filepath));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.equals(userString)) {
                out.println("Login accepted");
            }

        }
        out.println("Login rejected");
    }
    private void handleMessage() throws IOException {
        out.println("Ready");
        String string = in.readLine();
        String[] strings = string.split("\\|");
        System.out.println(strings[0] + ": " + strings[1]);
    }
    public void stop() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}
