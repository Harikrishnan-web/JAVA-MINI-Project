import java.io.*;
import java.net.*;
import java.util.*;

public class DictionaryClient {
    public static void main(String[] args) {
        try {
            // Step 1: Connect to server
            Socket socket = new Socket("localhost", 5000);
            System.out.println("Connected to Dictionary Server.\n");

            // Step 2: Create I/O streams
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Step 3: Get word from user
            System.out.print("Enter a word to find its meaning: ");
            String word = input.readLine();

            // Step 4: Send to server
            out.println(word);

            // Step 5: Receive and display result
            String meaning = in.readLine();
            System.out.println("Meaning: " + meaning);

            // Step 6: Close socket
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
