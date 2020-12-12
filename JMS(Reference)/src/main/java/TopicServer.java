import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringReader;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public class TopicServer {
    private final StringBuffer cookie = new StringBuffer();
    private static final StringBuffer noCookie = new StringBuffer("noCookie");
    private final ObjectMapper mapper = new ObjectMapper();

    public String getData(final String request, final AtomicReference<ConcurrentHashMap<StringBuffer, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>>> mapOfMapReference) {
        ConcurrentHashMap<StringBuffer, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>> oldMap = new ConcurrentHashMap<StringBuffer, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>>();
        ConcurrentHashMap<StringBuffer, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>> newMap = new ConcurrentHashMap<StringBuffer, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>>();
        String cleanRequest = request.replace("topic/", "");
        cookie.append(new Random().nextLong());
        do {
            oldMap = mapOfMapReference.get();
            if (!oldMap.isEmpty()) {
                if (!oldMap.containsKey(cookie)) {
                    oldMap.put(cookie, oldMap.get(noCookie));
                }
                newMap = oldMap;
            } else {
                oldMap.put(cookie, new ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>());
                return "No data in storage";
            }
        } while (!mapOfMapReference.compareAndSet(oldMap, newMap));
        return mapOfMapReference.get().get(cookie).get(cleanRequest).poll();
    }

    public String putData(final String request, final AtomicReference<ConcurrentHashMap<StringBuffer, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>>> mapOfMapReference) throws IOException {
        ConcurrentHashMap<StringBuffer, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>> oldMap = new ConcurrentHashMap<StringBuffer, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>>();
        ConcurrentHashMap<StringBuffer, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>> newMap = new ConcurrentHashMap<StringBuffer, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>>();
        String cleanRequest = request.replace("topic ", "");
        StringReader stringReader = new StringReader(cleanRequest);
        JsonData jsonData = mapper.readValue(stringReader, JsonData.class);
        String text = jsonData.getText();
        String nameOfQueue = jsonData.getNameOfQueue();
        do {
            oldMap = mapOfMapReference.get();
            if (!oldMap.containsKey(noCookie)) {
                oldMap.put(noCookie, new ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>());
                newMap = oldMap;
            } else {
                break;
            }
        } while (!mapOfMapReference.compareAndSet(oldMap, newMap));
        for (ConcurrentHashMap.Entry<StringBuffer, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>> bigMapElement : mapOfMapReference.get().entrySet()) {
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
