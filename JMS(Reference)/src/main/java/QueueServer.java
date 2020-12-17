import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public class QueueServer {
    private final ObjectMapper mapper = new ObjectMapper();

    public String putData(final String request, final AtomicReference<HashMap<String, ConcurrentLinkedQueue<String>>> mapReference) throws IOException {
        var oldMap = new HashMap<String, ConcurrentLinkedQueue<String>>();
        var newMap = new HashMap<String, ConcurrentLinkedQueue<String>>();
        String cleanRequest = request.replace("queue ", "");
        StringReader stringReader = new StringReader(cleanRequest);
        JsonData jsonData = mapper.readValue(stringReader, JsonData.class);
        String text = jsonData.getText();
        String nameOfQueue = jsonData.getNameOfQueue();
        do {
            oldMap = mapReference.get();
            oldMap.putIfAbsent(nameOfQueue, new ConcurrentLinkedQueue<String>());
            newMap = oldMap;
        } while (!mapReference.compareAndSet(oldMap, newMap));
        mapReference.get().get(nameOfQueue).add(text);
        return "ignoreResponse";
    }

    public String getData(final String request, final AtomicReference<HashMap<String, ConcurrentLinkedQueue<String>>> mapReference) {
        var oldMap = new HashMap<String, ConcurrentLinkedQueue<String>>();
        var newMap = new HashMap<String, ConcurrentLinkedQueue<String>>();
        String cleanRequest = request.replace("queue/", "");
        do {
            oldMap = mapReference.get();
            if (!oldMap.containsKey(cleanRequest)) {
                return "No data in storage";
            }
            newMap = oldMap;
        } while (!mapReference.compareAndSet(oldMap, newMap));
        return "Hello from server! " + mapReference.get().get(cleanRequest).poll();
    }
}
