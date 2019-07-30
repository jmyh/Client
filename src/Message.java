import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {
    public final static String END="/end";
    public final static String DELETE="/delete";
    public final static String MY_MESSAGES="/getHistoryOfMessages";
    public final static String EXIT="/exit";
    public final static String GET_FILE="/getFile";
    public final static String SEND_FILE="/sendFile";
    public final static String ALL_MESSAGES="/getAllMessages";
    public final static String ERROR="/error";

    private int id;
    private String login;
    private String message;
    private boolean file=false;
    private LocalDateTime date;
    private final static DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Message(String login, String message, int id) {
        this.login = login;
        this.message=message;
        this.id=id;
        this.date=LocalDateTime.now();
    }

    public Message(String login, String message) {
        this.login = login;
        this.message = message;
        this.date=LocalDateTime.now();
    }

    public Message(String json) {
        this.login=getLogin(json);
        this.message=getMessage(json);
        this.id=getId(json);
        this.file=getType(json);
        this.date= LocalDateTime.parse(getData(json),formatter);
    }



    public void markAsFile() { file=true; }

    public void markAsText() { file=false; }

    public boolean isFile() { return file; }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate()  {return date;}

    public static String getLogin(String jsonStr) {
        if(jsonStr!=null && !jsonStr.equals("")) {
            int startIndex = jsonStr.indexOf("login");
            int lastIndex = jsonStr.indexOf("\"", startIndex + 8);
            return jsonStr.substring(startIndex + 8, lastIndex);
        }
        return null;
    }

    public static String getMessage(String jsonStr) {
        if(jsonStr!=null && !jsonStr.equals("")) {
            int startIndex = jsonStr.indexOf("message");
            int lastIndex = jsonStr.indexOf("\"", startIndex + 10);
            return jsonStr.substring(startIndex + 10, lastIndex);
        }
        return null;
    }

    public static int getId(String jsonStr) {
        if(jsonStr!=null && !jsonStr.equals("")) {
            int startIndex = jsonStr.indexOf("id");
            int lastIndex = jsonStr.indexOf("\"", startIndex + 5);
            return Integer.parseInt(jsonStr.substring(startIndex + 5, lastIndex));
        }
        return 0;
    }

    public static boolean getType(String jsonStr) {
        if(jsonStr!=null && !jsonStr.equals("")) {
            int startIndex = jsonStr.indexOf("type");
            int lastIndex = jsonStr.indexOf("\"", startIndex + 7);
            return jsonStr.substring(startIndex + 7, lastIndex).equals("file");
        }
        return false;
    }

    private CharSequence getData(String jsonStr) {

        if(jsonStr!=null && !jsonStr.equals("")) {
            int startIndex = jsonStr.indexOf("date");
            int lastIndex = jsonStr.indexOf("\"", startIndex + 7);
            return jsonStr.substring(startIndex + 7, lastIndex);
        }
        return null;
    }

    @Override
    public String toString() {
        return "{\"id\":\""+id+"\","+"\"login\":\""+login+"\",\"message\":\""+message+"\",\"type\":\""+(file ?"file":"text")+"\""+",\"date\":\""+date.format(formatter)+"\"}";
    }
}
