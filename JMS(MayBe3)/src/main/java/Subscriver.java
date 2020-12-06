import java.io.*;
import java.net.Socket;

public class Subscriver {

    public static void main(String[] args) {
        try (Socket socket = new Socket("127.0.0.1", 8000);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            String request = "GET /topic/weather";
            writer.write(request);
            writer.newLine();
            writer.flush();
            String response = reader.readLine();
            System.out.println("Response: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

