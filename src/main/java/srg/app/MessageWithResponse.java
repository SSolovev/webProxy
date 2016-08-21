package srg.app;

import java.io.OutputStream;

/**
 * Created by Sergey on 28.07.2016.
 */
public class MessageWithResponse {
    String message;
    OutputStream out;

    public MessageWithResponse(String message, OutputStream out) {
        this.message = message;
        this.out = out;
    }
}
