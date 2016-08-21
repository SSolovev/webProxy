package srg.app;

/**
 * Created by Sergey on 01.08.2016.
 */
public enum RequestElement {
    GET("GET"), POST("POST"), CONNECT("CONNECT"), HEAD("HEAD");
    private String val;

    RequestElement(String val) {
        this.val = val;
    }

    public String getValue() {
        return val;
    }

    public static RequestElement fromString(String val){
       return RequestElement.valueOf(val.trim().split(" ")[0]);
    }
}
