package srg.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

import static srg.app.Request.parseRequestFromString;

/**
 * Created by Sergey on 27.07.2016.
 */
public class TestThread implements Runnable {

    private final static Logger log = LoggerFactory.getLogger(TestThread.class);
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private static final byte[] END_OF_RESPONSE_ARRAY = "\r\n0\r\n\r\n".getBytes();
    private Socket serverSocketFromBrowser;
    private String name = "TestThread-";

    public TestThread(Socket serverSocketFromBrowser) {
        this.serverSocketFromBrowser = serverSocketFromBrowser;
        name += poolNumber.getAndIncrement();
    }

    @Override
    public void run() {
        log.info("{} :: Begin!", name);
        try {
            try (InputStreamReader fromBrowserStream = new InputStreamReader(serverSocketFromBrowser.getInputStream());
                 OutputStream toBrowserStream = serverSocketFromBrowser.getOutputStream()) {

                log.info("{} :: Accepted!", name);
                StringBuilder fromBrowserString = new StringBuilder();

                char[] buffer = new char[100];
                int charIndex;

                while (fromBrowserStream.ready()) {
                    charIndex = fromBrowserStream.read(buffer);
                    fromBrowserString.append(buffer, 0, charIndex);
                }

                Request request = parseRequestFromString(fromBrowserString.toString());

                if (request != null) {
                    log.info("{} :: -- Request: \n{}", name, request);
//                    System.out.println(request.toString());
                    try (Socket redirectServerSocket = new Socket(request.getHost(), request.getPort());
                         PrintWriter toRedirectServerStream = new PrintWriter(redirectServerSocket.getOutputStream());
                         InputStream fromRedirectServerStream = redirectServerSocket.getInputStream();) {
                        toRedirectServerStream.write(fromBrowserString.toString());
                        toRedirectServerStream.flush();
                        byte[] b = new byte[8192];
                        int bytesRead;
                        int totalBytesRead = 0;
                        int endOfResponse = Integer.MAX_VALUE;
                        log.info("{} :: ** Waiting for Response host:{}; port:{};", name, request.getHost(), request.getPort());

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                        String chunk = null;
                        while (totalBytesRead < endOfResponse && (bytesRead = fromRedirectServerStream.read(b)) != -1) {
//                            String chunk = new String(b);
//                            chunk.append(b);
//                            log.info("{} :: ** Response part begin***\n{}\n***Response part end***", name, chunk);
//                            chunk.indexOf("HTTP/") == 0 &&
                            if (chunk == null) {
                                chunk = new String(b, 0, bytesRead);
                                log.info("{} :: ** Response part begin***\n{}\n***Response part end***", name, chunk);
                                if (chunk.indexOf("Content-Length: ") > 0) {
                                    int indexOfHeaderEnd = chunk.indexOf("\r\n\r\n") + 4;
                                    int contentInd = chunk.indexOf("Content-Length: ");
                                    int contentLength = Integer.parseInt(chunk.substring(contentInd + 16, chunk.indexOf("\n", contentInd)).trim());

                                    endOfResponse = contentLength + indexOfHeaderEnd;
                                    log.info("{} :: ** Content length: {}; Full length: {}", name, contentLength, endOfResponse);
                                }
                            }

                            if (endOfResponse == Integer.MAX_VALUE) {
                                if (bytesRead >= 7) {
                                    boolean isEnd = true;
                                    for (int i = 0; i < 7; i++) {
                                        if (END_OF_RESPONSE_ARRAY[i] != b[bytesRead - 7 + i]) {
                                            isEnd = false;
                                        }
                                    }
                                    if (isEnd) {
                                        endOfResponse = -1;
                                    }
                                }
                            }


                            if (bytesRead > 0) {
//                                toBrowserStream.write(b, 0, bytesRead);
                                byteArrayOutputStream.write(b, 0, bytesRead);
                                totalBytesRead += bytesRead;
                            }

                        }
                        toBrowserStream.write(byteArrayOutputStream.toByteArray());
                        toBrowserStream.flush();

                    } catch (Exception e) {
                        log.error("{} :: ** host:{}; port:{}; Error:", name, request.getHost(), request.getPort(), e);
                    }
                }

            }

        } catch (Exception e) {
            log.error("{} :: ## Error: {}", name, e);
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
