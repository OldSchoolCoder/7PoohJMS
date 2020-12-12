import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class SocketBox {
    private static volatile String request;
    private final static ExecutorService pool = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()
    );
    private final static AtomicReference<ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>> mapReference = new AtomicReference<>(new ConcurrentHashMap<>());
    private final static AtomicReference<ConcurrentHashMap<StringBuffer, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>>> mapOfMapReference = new AtomicReference<>(new ConcurrentHashMap<>());


    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        try (ServerSocket socket = new ServerSocket(8000);) {
            while (true) {
                Socket connection = socket.accept();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                request = reader.readLine();
                RequestDispatcher serverTask = new RequestDispatcher(request, mapReference, mapOfMapReference);
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
