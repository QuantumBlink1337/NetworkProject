package main;

import java.io.*;
import java.net.Socket;
/*
    ClientHandler Class
        Responsible for receiving client commands and translating them to executable statements.
    Matt Marlow
    Spring 2024
 */

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final String filepath = "src/resources/users.txt";




    private final PrintWriter out;
    private final BufferedReader in;
    private String username;
    private boolean isLoggedin = false;
    private boolean connected;

    public ClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        connected = true;
    }

    /*
        Generalized loop for receiving commands.
     */
    public void run() {
        String inputLine;
        while (connected) {
            try {
                // We need to do the following here - receive the line, and break each portion into the command and the message.
                // We can assume that index 0 of the tokenized message is the client command.
                inputLine = in.readLine();
                String[] parsedInput = inputLine.split(" ");
                String command = parsedInput[0];
                if (command.equals("login")) {
                    if (parsedInput.length < 3) {
                        out.println("Missing password");
                    }
                    else {
                        // 1 and 2 should be the username and password.
                        handleLogin(parsedInput[1], parsedInput[2]);
                    }
                }
                if (command.equals("send")) {
                    // Since we tokenize the string by space, we'll need to reconstruct the message since a standard message can always have spaces.
                    // We can assume that index 2 will make up the message since 0 and 1 are reserved for commands.
                    StringBuilder rebuiltMessage = new StringBuilder();
                    for (int i = 2; i < parsedInput.length; i++) {
                        rebuiltMessage.append(parsedInput[i]);
                        if (i != parsedInput.length - 1) {
                            rebuiltMessage.append(" ");
                        }
                    }
                    handleMessage(rebuiltMessage.toString(), parsedInput[1]);
                }
                if (command.equals("logout")) {
                    logout();
                }
                if (command.equals("newuser")) {
                    // If the parsed statement is less than three, we're missing some args.
                    if (parsedInput.length < 3) {
                        out.println("Missing password");
                    }
                    else {
                        handleNewUser(parsedInput[1], parsedInput[2]);
                    }
                }
                if (command.equals("who")) {
                    displayConnectedUsers();
                }

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        stop();
    }
    // Safely closes all resources.
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
        // Boilerplate checks.
        if (isLoggedin) {
            out.println("You are already logged in as " + username);
            return;
        }
        for (ClientHandler clientHandler : Server.getConnectedClients()) {
            if (clientHandler.isLoggedin && clientHandler.username.equals(user)) {
                out.println("A user is already logged in with that username");
                return;
            }
        }
        // Open a reader to see if the login info is actually there.
        final BufferedReader bufferedReader = new BufferedReader(new FileReader(filepath));
        String login = "(" + user + ", " + pass + ")";
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.equals(login)) {
                out.println("Login confirmed");
                username = user;
                isLoggedin = true;
                sendMessageToClients(user + " has logged in.");
                bufferedReader.close();
                return;
            }

        }
        bufferedReader.close();
        // We didn't find the login.
        out.println("Denied. Username or password is incorrect.");
    }
    private void handleNewUser(String user, String pass) throws IOException {

        // Look for a preexisting user account. Careful not to reveal password info, so we just check if the user is the same.
        final BufferedReader bufferedReader = new BufferedReader(new FileReader(filepath));
        String login = "(" + user + ", " + pass + ")";
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.contains(user)) {
                bufferedReader.close();
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
        bufferedWriter.close();
        bufferedReader.close();


    }
    private void handleMessage(String message, String arg) throws IOException {
        if (!isLoggedin) {
            out.println("Not logged in");
            return;
        }
        if (message.length() > 256) {
            out.println("Message received is too long");
            return;
        }
        if (arg.equalsIgnoreCase("all")) {
            sendMessageToClients(username + ": " + message);
        }
        else {
            sendMessageToSpecificClient(message, arg);
        }

    }
    private void displayConnectedUsers() {
        int i = 0;
        String comma;
        // We'll build our message versus sending it out for each client.
        StringBuilder message = new StringBuilder();
        for (ClientHandler clientHandler : Server.getConnectedClients()) {
            if (clientHandler.isLoggedin) {
                comma = i == Server.getConnectedClients().size() - 1 ? "" : ", ";
                message.append(clientHandler.username).append(comma);
            }
            i++;
        }
        out.println(message);
    }
    private void sendMessageToClients(String message) {
        for (ClientHandler clientHandler : Server.getConnectedClients()) {
            if (clientHandler.isLoggedin) {
                clientHandler.out.println(message);
            }
        }
        System.out.println(message);
    }
    private void sendMessageToSpecificClient(String message, String userID) {
        boolean sent = false;
        for (ClientHandler clientHandler : Server.getConnectedClients()) {
            if (clientHandler.isLoggedin && clientHandler.username.equals(userID)) {
                clientHandler.out.println(username + " sent you a DM: " + message);
                sent = true;
            }
        }
        if (!sent) {
            out.println("Could not find a user by that username connected");
        }
        else {
            System.out.println(username + " to " + userID + ": " + message);
        }
    }
    // Close resources when the client disconnects.

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
