import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect
public class JsonData {
    private String nameOfQueue;
    private String text;

    public JsonData() {
    }

    public JsonData(final String nameOfQueue, final String text) {
        this.nameOfQueue = nameOfQueue;
        this.text = text;
    }

    public String getNameOfQueue() {
        return nameOfQueue;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "QueueType{" +
                "queue='" + nameOfQueue + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
