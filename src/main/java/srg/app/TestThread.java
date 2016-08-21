package srg.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.CharBuffer;
import java.util.ArrayList;

import static srg.app.Request.parseRequestFromString;

/**
 * Created by Sergey on 27.07.2016.
 */
public class TestThread implements Runnable {

    private final static Logger log = LoggerFactory.getLogger(TestThread.class);

    private Socket serverSocketFromBrowser;

    public TestThread(Socket serverSocketFromBrowser) {
        this.serverSocketFromBrowser = serverSocketFromBrowser;
    }

    @Override
    public void run() {
        log.info("Begin!");
        try {
            try (InputStreamReader fromBrowserStream = new InputStreamReader(serverSocketFromBrowser.getInputStream());
                 OutputStream toBrowserStream = serverSocketFromBrowser.getOutputStream()) {

                log.info("Accepted!");
                StringBuilder fromBrowserString = new StringBuilder();

                char[] buffer = new char[10];

                while (fromBrowserStream.ready()) {
                    fromBrowserStream.read(buffer);
                    fromBrowserString.append(buffer);
                }

                Request request = parseRequestFromString(fromBrowserString.toString());

                if (request != null) {
                    log.info("+++++Request: \n{}", request);
//                    System.out.println(request.toString());
                    try (Socket redirectServerSocket = new Socket(request.getHost(), request.getPort());
                         PrintWriter toRedirectServerStream = new PrintWriter(redirectServerSocket.getOutputStream());
                         InputStream fromRedirectServerStream = redirectServerSocket.getInputStream();
                    ) {
                        toRedirectServerStream.write(fromBrowserString.toString());
                        toRedirectServerStream.flush();
                        byte[] b = new byte[4096];
                        int bytesRead;

                        while ((bytesRead = fromRedirectServerStream.read(b)) != -1) {
                            log.info("****Response part begin***\n{}\n***Response part end***", new String(b));
                            if (bytesRead > 0) {
                                toBrowserStream.write(b, 0, bytesRead);

                            }
                        }
                        toBrowserStream.flush();

                    } catch (Exception e) {
                        log.error("***Error: {}", e);
                    }
                }

            }

        } catch (Exception e) {
            log.error("###Error: {}", e);
        }
    }

    public static String[] getHostPort(String line) {
        String[] path = line.split("Host:")[1].split(":");
        String host = path[0].trim();
        String port = "80";
        if (path.length >= 2) {
            port = path[1];
        }
        return new String[]{host, port};
    }
}
