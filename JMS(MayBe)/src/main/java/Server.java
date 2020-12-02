import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Server {
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> map = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<StringBuffer, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>> mapOfMap = new ConcurrentHashMap<StringBuffer, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>>();
    private volatile StringBuffer cookie = new StringBuffer();
    private static final StringBuffer noCookie = new StringBuffer("noCookie");

    public void handleRequest(BufferedReader reader, BufferedWriter writer) throws IOException {
        String request = reader.readLine();
        String response = null;
        ObjectMapper mapper = new ObjectMapper();
        if (request.startsWith("GET /")) {
            String cleanRequest = request.replace("GET /", "");
            if (cleanRequest.startsWith("queue/")) {
                String cleanRequest2 = cleanRequest.replace("queue/", "");
                if (map.containsKey(cleanRequest2)) {
                    response = "Hello from server! " + map.get(cleanRequest2).poll();
                    writer.write(response);
                    writer.newLine();
                    writer.flush();
                }
            } else if (cleanRequest.startsWith("topic/")) {
                String cleanRequest2 = cleanRequest.replace("topic/", "");
                cookie.append(new Random().nextLong());
                if (!mapOfMap.isEmpty()) {
                    if (!mapOfMap.containsKey(cookie)) {
                        mapOfMap.put(cookie, mapOfMap.get(noCookie));
                    }
                    response = "Hello from server! " + mapOfMap.get(cookie).get(cleanRequest2).poll();
                } else {
                    mapOfMap.put(cookie, new ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>());
                    response = "Hello from server! " + "No data in storage";
                }
            } else {
                response = "Hello from server! " + "Bad request";
            }
            writer.write(response);
            writer.newLine();
            writer.flush();
        } else if (request.startsWith("POST /")) {
            String cleanRequest = request.replace("POST /", "");
            if (cleanRequest.startsWith("queue ")) {
                String cleanRequest2 = cleanRequest.replace("queue ", "");
                StringReader stringReader = new StringReader(cleanRequest2);
                JsonData jsonData = mapper.readValue(stringReader, JsonData.class);
                String text = jsonData.getText();
                String nameOfQueue = jsonData.getNameOfQueue();
                if (!map.containsKey(nameOfQueue)) {
                    map.put(nameOfQueue, new ConcurrentLinkedQueue<String>());
                }
                map.get(nameOfQueue).add(text);
            } else if (cleanRequest.startsWith("topic ")) {
                String cleanRequest2 = cleanRequest.replace("topic ", "");
                StringReader stringReader = new StringReader(cleanRequest2);
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
            }
        }
    }
}
