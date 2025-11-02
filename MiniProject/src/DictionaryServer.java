import java.io.*;
import java.net.*;
import java.util.*;

public class DictionaryServer {
    public static void main(String[] args) {
        try {
            // Step 1: Create server socket
            ServerSocket serverSocket = new ServerSocket(5000);
            System.out.println("Dictionary Server is running on port 5000...");

            // Step 2: Initialize dictionary
            HashMap<String, String> dictionary = new HashMap<>();
            dictionary.put("java", "A high-level, object-oriented programming language.");
            dictionary.put("python", "An interpreted, high-level programming language.");
            dictionary.put("tcp", "Transmission Control Protocol used for reliable communication.");
            dictionary.put("ai", "Artificial Intelligence, simulation of human intelligence in machines.");

            while (true) {
                // Step 3: Accept client connection
                Socket socket = serverSocket.accept();
                System.out.println("Client connected.");

                // Step 4: Create I/O streams
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                // Step 5: Read client message
                String word = in.readLine();
                System.out.println("Client requested meaning for: " + word);

                // Step 6: Search in dictionary
                String meaning = dictionary.getOrDefault(word.toLowerCase(), "Word not found in dictionary.");

                // Step 7: Send meaning back to client
                out.println(meaning);

                // Step 8: Close socket
                socket.close();
                System.out.println("Client disconnected.\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
