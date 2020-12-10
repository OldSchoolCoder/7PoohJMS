import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;

public class Publisher {

    public static void main(String[] args) {
        try (Socket socket = new Socket("127.0.0.1", 8000);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            JsonData jsonData = new JsonData("weather", "temperature +18 C");
            ObjectMapper mapper = new ObjectMapper();
            String request = mapper.writeValueAsString(jsonData);
            String finalRequest = "POST /topic " + request;
            System.out.println("Request: " + finalRequest);
            writer.write(finalRequest);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
