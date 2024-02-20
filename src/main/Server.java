package main;

import java.net.ServerSocket;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static List<ClientHandler> getConnectedClients() {
        return connectedClients;
    }
    private static final List<ClientHandler> connectedClients = new ArrayList<>();


    public void start(int port) throws IOException {
        // https://www.baeldung.com/a-guide-to-java-sockets
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket s = serverSocket.accept();
                ClientHandler client = new ClientHandler(s, connectedClients.size());
                connectedClients.add(client);
                Thread thread = new Thread(client);
                thread.start();
            }
        }
    }
    public static void main(String[] args) throws IOException {
        System.out.println("Server chatroom Version 1");
        Server server = new Server();
        server.start(1060);
    }

}
