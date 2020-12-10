import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SocketBox {
    private static volatile String request;
    private static ExecutorService pool = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()
    );

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        try (ServerSocket socket = new ServerSocket(8000);) {
            RequestDispatcher serverTask = new RequestDispatcher();
            while (true) {
                Socket connection = socket.accept();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                request = reader.readLine();
                serverTask.setRequest(request);
                Future<String> response = pool.submit(serverTask);
                String result = response.get();
                writer.write(result);
                writer.newLine();
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            SocketBox.close();
        }
    }

    private static void close() {
        pool.shutdown();
        while (!pool.isTerminated()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
