import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Problem creating out and in streams");
        }
        String inputLine;
        while (true) {
            try {
                inputLine = in.readLine();
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
