import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringReader;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TopicServer {
    private volatile StringBuffer cookie = new StringBuffer();
    private static final StringBuffer noCookie = new StringBuffer("noCookie");
    ObjectMapper mapper = new ObjectMapper();

    public String getData(final String request, ConcurrentHashMap<StringBuffer, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>> mapOfMap) {
        String cleanRequest = request.replace("topic/", "");
        cookie.append(new Random().nextLong());
        if (!mapOfMap.isEmpty()) {
            if (!mapOfMap.containsKey(cookie)) {
                mapOfMap.put(cookie, mapOfMap.get(noCookie));
            }
            return mapOfMap.get(cookie).get(cleanRequest).poll();
        } else {
            mapOfMap.put(cookie, new ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>());
            return "No data in storage";
        }
    }

    public String putData(final String request, ConcurrentHashMap<StringBuffer, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>> mapOfMap) throws IOException {
        String cleanRequest = request.replace("topic ", "");
        StringReader stringReader = new StringReader(cleanRequest);
        JsonData jsonData = mapper.readValue(stringReader, JsonData.class);
        String text = jsonData.getText();
        String nameOfQueue = jsonData.getNameOfQueue();
        if (!mapOfMap.containsKey(noCookie)) {
            mapOfMap.put(noCookie, new ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>());
        }
        for (ConcurrentHashMap.Entry<StringBuffer, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>> bigMapElement : mapOfMap.entrySet()) {
            if (!bigMapElement.getValue().containsKey(nameOfQueue)) {
                bigMapElement.getValue().put(nameOfQueue, new ConcurrentLinkedQueue<String>());
            }
            for (ConcurrentHashMap.Entry<String, ConcurrentLinkedQueue<String>> smallMapElement : bigMapElement.getValue().entrySet()) {
                if (smallMapElement.getKey().equals(nameOfQueue)) {
                    smallMapElement.getValue().add(text);
                }
            }
        }
        return "ignoreResponse";
    }
}
