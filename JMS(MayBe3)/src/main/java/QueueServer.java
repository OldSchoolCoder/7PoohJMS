import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueServer {
    ObjectMapper mapper = new ObjectMapper();

    public String putData(final String request, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> map) throws IOException {
        String cleanRequest = request.replace("queue ", "");
        StringReader stringReader = new StringReader(cleanRequest);
        JsonData jsonData = mapper.readValue(stringReader, JsonData.class);
        String text = jsonData.getText();
        String nameOfQueue = jsonData.getNameOfQueue();
        if (!map.containsKey(nameOfQueue)) {
            map.put(nameOfQueue, new ConcurrentLinkedQueue<String>());
        }
        map.get(nameOfQueue).add(text);
        return "ignoreResponse";
    }

    public String getData(final String request, final ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> map) {
        String cleanRequest = request.replace("queue/", "");
        if (map.containsKey(cleanRequest)) {
            return "Hello from server! " + map.get(cleanRequest).poll();
        } else {
            return "No data in storage";
        }
    }
}
