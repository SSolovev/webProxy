package srg.app;

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
    Socket serverSocketFromBrowser;

    public TestThread(Socket serverSocketFromBrowser) {
        this.serverSocketFromBrowser = serverSocketFromBrowser;
    }

    @Override
    public void run() {
        System.out.println("Begin!");
        try {
//            serverSocketFromBrowser = new ServerSocket(5555);


//            while (true) {
                try (
//                        Socket browserSocket = serverSocketFromBrowser.accept();
                        InputStreamReader fromBrowserStream =
                                new InputStreamReader(serverSocketFromBrowser.getInputStream());
//                        BufferedReader fromBrowserStream =
//                                new BufferedReader(
//                                        new InputStreamReader(browserSocket.getInputStream(),"UTF-8"));
                        OutputStream toBrowserStream = serverSocketFromBrowser.getOutputStream();
                ) {
                    System.out.println("Accepted!");
//            while (true) {
                    StringBuilder fromBrowserString = new StringBuilder();

//                sb.delete(0, sb.length());
//                while (br.ready()) {
//                    sb.append(br.readLine()).append("\n");
//                }
                    String[] hostPort = null;
                    char[] buffer = new char[10];
                    while (fromBrowserStream.ready()) {
                        fromBrowserStream.read(buffer);
                        fromBrowserString.append(buffer);

                    }
                    Request request = parseRequestFromString(fromBrowserString.toString());
//                    System.out.println(request.toString());
//                    while (fromBrowserStream.ready()) {
//                       String buff= fromBrowserStream.readLine();
//                        if(buff!=null && buff.indexOf("Host:")!=-1){
//                            hostPort=getHostPort(buff);
//                        }
//                        fromBrowserString.append(buff);
//                    }

                    if (request != null) {
                        System.out.println("Reading finished!");
                        System.out.println(request.toString());
                        try (Socket redirectServerSocket =
//                                     new Socket(hostPort[0], Integer.parseInt(hostPort[1]));
                                     new Socket(request.getHost(), request.getPort());
                             PrintWriter toRedirectServerStream =
                                     new PrintWriter(redirectServerSocket.getOutputStream());
//                         OutputStream os = redirectSoket.getOutputStream();
                             InputStream fromRedirectServerStream = redirectServerSocket.getInputStream();
                        ) {
                            toRedirectServerStream.write(fromBrowserString.toString());
                            toRedirectServerStream.flush();
                            byte[] b = new byte[4096];
                            int bytesRead;
//                        StringCharBuffer cb = new StringCharBuffer();
//                       ByteArrayInputStream bb =new ByteArrayInputStream();
                            while ((bytesRead = fromRedirectServerStream.read(b)) != -1) {
                                System.out.println("Write back to browser!");
                                System.out.println("=====================");
                                System.out.println(new String(b));
                                System.out.println("=====================");
                                toBrowserStream.write(b, 0, bytesRead);
                                toBrowserStream.flush();

                            }

//                            StringBuilder sb = new StringBuilder();
//                            while ((fromRedirectServerStream.read(b)) != -1) {
//                                sb.append(b);
//                                toBrowserStream.write(b);
//                                toBrowserStream.flush();
//                            }
//                            System.out.println("Write back to browser!");
//                            System.out.println(sb.toString());
//                            System.out.println("==========================");
//                            toBrowserStream.write(sb.toString());
//                            toBrowserStream.flush();
                            //todo:
//                            while(fromRedirectServerStream.available()>0)
//                            System.out.println("Done!");

//                            int bytesRead;
//                            byte[] reply = new byte[4096];
//                            while ((bytesRead = isRedirect.read(reply)) != -1) {
//                                p.write(reply, 0, bytesRead);
//                                p.flush();
//                            }
                        }
                    }


                }
//            }

        } catch (IOException e) {
            e.printStackTrace();
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
