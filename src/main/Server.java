package main;

import java.net.ServerSocket;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
    Server Class
        Responsible for listening to client connection requests and spawning a new thread with a client handler
    Matt Marlow
    Spring 2024
 */

public class Server {
    public static List<ClientHandler> getConnectedClients() {
        return connectedClients;
    }
    private static final List<ClientHandler> connectedClients = new ArrayList<>(); // useful for client handler to know which other clients are available


    public void start(int port, int poolSize)  {
        // https://www.baeldung.com/a-guide-to-java-sockets
        // Uses a threadpool to avoid Denial of Service, plus it's just better practice
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            try (ExecutorService pool = Executors.newFixedThreadPool(poolSize)) {
                while (true) {
                    Socket s = serverSocket.accept();

                    ClientHandler client = new ClientHandler(s);

                    connectedClients.add(client);
                    pool.execute(client);

                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }




    }
    public static void main(String[] args) {
        System.out.println("Chatroom server version 2");
        Server server = new Server();
        final int MAXCLIENTS = 3;
        server.start(1060, MAXCLIENTS);
    }

}
