import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketBox {

    public static void main(String[] args) throws IOException {
        ExecutorService pool = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors()
        );
        Server server = new Server();
        try (ServerSocket socket = new ServerSocket(8000)) {
            while (true) {
                final Socket connection = socket.accept();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                pool.submit(() -> {
                    try {
                        server.handleRequest(reader, writer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
