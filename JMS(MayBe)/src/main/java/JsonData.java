import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect
public class JsonData {
    private String nameOfQueue;
    private String text;

    public JsonData() {
    }

    public JsonData(String nameOfQueue, String text) {
        this.nameOfQueue = nameOfQueue;
        this.text = text;
    }

    public String getNameOfQueue() {
        return nameOfQueue;
    }

    public void setNameOfQueue(String nameOfQueue) {
        this.nameOfQueue = nameOfQueue;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "QueueType{" +
                "queue='" + nameOfQueue + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
