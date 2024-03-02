package main;

import java.net.ServerSocket;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static List<ClientHandler> getConnectedClients() {
        return connectedClients;
    }
    private static final List<ClientHandler> connectedClients = new ArrayList<>();


    public void start(int port, int poolSize)  {
        // https://www.baeldung.com/a-guide-to-java-sockets
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            try (ExecutorService pool = Executors.newFixedThreadPool(poolSize)) {
                while (true) {
                    Socket s = serverSocket.accept();

                    ClientHandler client = new ClientHandler(s, connectedClients.size());

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
        System.out.println("Horrible chatroom server version 2");
        Server server = new Server();
        server.start(1060, 10);
    }

}
