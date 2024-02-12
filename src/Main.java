import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        // Press Alt+Enter with your caret at the highlighted text to see how
        // IntelliJ IDEA suggests fixing it.
        System.out.printf("Hello and welcome!");

        // Press Shift+F10 or click the green arrow button in the gutter to run the code.
        for (int i = 1; i <= 5; i++) {

            // Press Shift+F9 to start debugging your code. We have set one breakpoint
            // for you, but you can always add more by pressing Ctrl+F8.
            System.out.println("i = " + i);
        }
    }
    private Client client1;
    private Client client2;
    @Before
    public void setup() throws IOException {
        client1 = new Client();
        client2 = new Client();
        client1.startConnection("127.0.0.1", 1060);
        client2.startConnection("127.0.0.1", 1060);
    }
    @Test
    public void givenGreetingClient_whenServerRespondsWhenStarted_thenCorrect() throws IOException {
        Client client = new Client();
        client.startConnection("127.0.0.1", 1060);
        String response = client.sendMessage("hello server");
        assertEquals("hello client", response);
    }
    @Test
    public void givenClient_TestMultipleResponses() throws IOException {
        String resp1 = client1.sendMessage("hello");
        String resp2 = client1.sendMessage("world");
        String resp3 = client1.sendMessage("!");
        String resp4 = client1.sendMessage(".");
        assertEquals("hello", resp1);
        assertEquals("world", resp2);
        assertEquals("!", resp3);
        assertEquals("goodbye", resp4);
    }
    @Test
    public void testSecondClientMultipleResponses() throws IOException {
        String resp1 = client2.sendMessage("hello");
        String resp2 = client2.sendMessage("world");
        String resp3 = client2.sendMessage("!");
        String resp4 = client2.sendMessage(".");
        assertEquals("hello", resp1);
        assertEquals("world", resp2);
        assertEquals("!", resp3);
        assertEquals("goodbye", resp4);
    }
     @After
    public void teardown() throws IOException {
        client1.stopConnection();
        client2.stopConnection();
    }
}