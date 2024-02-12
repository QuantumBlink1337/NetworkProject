import java.net.ServerSocket;
import java.io.*;
import java.net.Socket;

public class Server {
    // https://www.baeldung.com/a-guide-to-java-sockets
    private ServerSocket serverSocket;


    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        while (true) {
            Thread thread = new Thread(new ClientHandler(serverSocket.accept()));
            thread.start();
        }
    }
    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.start(1060);
    }

}
