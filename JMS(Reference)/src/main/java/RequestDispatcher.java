import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public class RequestDispatcher implements Callable<String> {
    private volatile String request;
    private final AtomicReference<ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>> mapReference = new AtomicReference<>(new ConcurrentHashMap<>());
    private final AtomicReference<ConcurrentHashMap<StringBuffer, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>>> mapOfMapReference = new AtomicReference<>(new ConcurrentHashMap<>());
    private final QueueServer queueServer = new QueueServer();

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

    public void setRequest(final String request) {
        this.request = request;
    }

}
