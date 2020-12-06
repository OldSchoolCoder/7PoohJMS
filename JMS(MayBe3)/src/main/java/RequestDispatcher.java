import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RequestDispatcher implements Callable<String> {
    private volatile String request;
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> map = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<StringBuffer, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>> mapOfMap = new ConcurrentHashMap<StringBuffer, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>>();
    QueueServer queueServer = new QueueServer();

    @Override
    public String call() throws Exception {
        TopicServer topicServer = new TopicServer();
        if (request.startsWith("GET /")) {
            String cleanRequest = request.replace("GET /", "");
            if (cleanRequest.startsWith("queue/")) {
                return queueServer.getData(cleanRequest, map);
            } else if (cleanRequest.startsWith("topic/")) {
                return topicServer.getData(cleanRequest, mapOfMap);
            }
            return "Bad request";
        } else if (request.startsWith("POST /")) {
            String cleanRequest = request.replace("POST /", "");
            if (cleanRequest.startsWith("queue ")) {
                return queueServer.putData(cleanRequest, map);
            } else if (cleanRequest.startsWith("topic ")) {
                return topicServer.putData(cleanRequest, mapOfMap);
            }
            return "Bad request";
        } else {
            return "Bad request";
        }
    }

    public void setRequest(String request) {
        this.request = request;
    }

}
