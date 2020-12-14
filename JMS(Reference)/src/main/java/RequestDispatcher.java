import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public class RequestDispatcher implements Callable<String> {
    private final String request;
    private final AtomicReference<HashMap<String, ConcurrentLinkedQueue<String>>> mapReference;
    private final AtomicReference<HashMap<String, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>>> mapOfMapReference;
    private final QueueServer queueServer = new QueueServer();

    public RequestDispatcher(final String request, final AtomicReference<HashMap<String, ConcurrentLinkedQueue<String>>> mapReference, final AtomicReference<HashMap<String, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>>> mapOfMapReference) {
        this.request = request;
        this.mapReference = mapReference;
        this.mapOfMapReference = mapOfMapReference;
    }

    @Override
    public String call() throws Exception {
        TopicServer topicServer = new TopicServer();
        if (request.startsWith("GET /")) {
            String cleanRequest = request.replace("GET /", "");
            if (cleanRequest.startsWith("queue/")) {
                return queueServer.getData(cleanRequest, mapReference);
            } else if (cleanRequest.startsWith("topic/")) {
                return topicServer.getData(cleanRequest, mapOfMapReference);
            }
            return "Bad request";
        } else if (request.startsWith("POST /")) {
            String cleanRequest = request.replace("POST /", "");
            if (cleanRequest.startsWith("queue ")) {
                return queueServer.putData(cleanRequest, mapReference);
            } else if (cleanRequest.startsWith("topic ")) {
                return topicServer.putData(cleanRequest, mapOfMapReference);
            }
            return "Bad request";
        } else {
            return "Bad request";
        }
    }
}
