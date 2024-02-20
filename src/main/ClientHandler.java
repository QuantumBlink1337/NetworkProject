package main;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final String filepath = "src/resources/users.txt";


    public PrintWriter getOut() {
        return out;
    }

    public BufferedReader getIn() {
        return in;
    }

    private final PrintWriter out;
    private final BufferedReader in;
    private int ID;

    public String getUsername() {
        return username;
    }

    private String username;
    private boolean isLoggedin = false;
    private boolean connected;

    public ClientHandler(Socket clientSocket, int ID) throws IOException {
        this.clientSocket = clientSocket;
        this.ID  = ID;
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        connected = true;
    }
    public void run() {

        String inputLine;
        while (connected) {
            try {
                inputLine = in.readLine();
                String[] parsedInput = inputLine.split(" ");
                String command = parsedInput[0];



                if (command.equals("login")) {
                    if (parsedInput.length < 3) {
                        out.println("Missing password");
                    }
                    else {
                        handleLogin(parsedInput[1], parsedInput[2]);
                    }
                }
                if (command.equals("send")) {
                    handleMessage(inputLine.substring(5));
                }
                if (command.equals("logout")) {
                    logout();
                }
                if (command.equals("newuser")) {
                    if (parsedInput.length < 3) {
                        out.println("Missing password");
                    }
                    else {
                        handleNewUser(parsedInput[1], parsedInput[2]);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        stop();
    }
    private void logout() throws IOException {
        if (!isLoggedin) {
            out.println("You are not logged in");
        }
        sendMessageToClients(username + " left.");
        isLoggedin = false;
        out.close();
        in.close();
        connected = false;


    }
    private void handleLogin(String user, String pass) throws IOException {

        if (isLoggedin) {
            out.println("You are already logged in as " + username);
            return;
        }
        final BufferedReader bufferedReader = new BufferedReader(new FileReader(filepath));
        String login = "(" + user + ", " + pass + ")";
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.equals(login)) {
                out.println("Login confirmed");
                username = user;
                isLoggedin = true;
                sendMessageToClients(user + " has logged in.");
                return;
            }

        }
        out.println("Denied. Username or password is incorrect.");
    }
    private void handleNewUser(String user, String pass) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new FileReader(filepath));
        String login = "(" + user + ", " + pass + ")";
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.contains(user)) {
                out.println("Denied. User account already exists");
                return;
            }
        }
        final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filepath, true));
        bufferedWriter.newLine();
        bufferedWriter.append(login);
        out.println("Account created successfully");
        sendMessageToClients("A new user has created an account.");
        bufferedWriter.flush();


    }
    private void handleMessage(String message) throws IOException {
        if (!isLoggedin) {
            out.println("Not logged in");
            return;
        }
        if (message.length() > 256) {
            out.println("Message received is too long");
            return;
        }
        sendMessageToClients(username + ": " + message);
    }
    private void sendMessageToClients(String message) {
        for (ClientHandler clientHandler : Server.getConnectedClients()) {
            if (clientHandler.isLoggedin) {
                clientHandler.out.println(message);
            }
        }
        System.out.println(message);
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
