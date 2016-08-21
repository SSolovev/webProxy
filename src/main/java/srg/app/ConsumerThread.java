package srg.app;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by Sergey on 27.07.2016.
 */
public class ConsumerThread implements Runnable {
    public static final String GET_PREDICATE = "GET";
    public static final String CONNECT_PREDICATE = "CONNECT";
    public BlockingQueue<MessageWithResponse> queue;

    public ConsumerThread(BlockingQueue<MessageWithResponse> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
//        try {
        while (true) {
            try {
                MessageWithResponse message = queue.poll(100l, TimeUnit.MILLISECONDS);
                if (message != null && !message.message.isEmpty()) {
                    System.out.println("Got it!");
                    String[] hostPort = null;
                    String delimeter = CONNECT_PREDICATE;
                    if (message.message.contains(GET_PREDICATE)) {
                        delimeter = GET_PREDICATE;
                    }
                    hostPort = getHostPort(message.message, delimeter);
                    transferData(hostPort, message);
                }
            } catch (Exception e) {
                System.out.println(e.toString());
            }


        }
    }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

    public static void transferData(String[] hostPort, MessageWithResponse message) {
        if (hostPort == null) return;
        System.out.println("Transfer message:\n" + message.message + " To " + hostPort[0] + " " + hostPort[1]);
        try (Socket s = new Socket(hostPort[0], Integer.parseInt(hostPort[1]));

             InputStreamReader ir = new InputStreamReader(s.getInputStream());
             BufferedReader br = new BufferedReader(ir);

             OutputStream out = s.getOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(out);
        ) {
//            for(String m:message){
            try {
                writer.write(message.message);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            }


            writer.flush();
            System.out.println("Reading response from server..");

//            br.lines().forEach(System.out::println);

            OutputStreamWriter writer2 = new OutputStreamWriter(message.out);
           StringBuilder sb = new StringBuilder();
            while (br.ready()) {
                sb.append(br.readLine()).append("\n");
//                String line = br.readLine();
//                System.out.println(line);
//                writer2.write(line);
            }
            System.out.println(sb.toString());
//            writer2.write(sb.toString());
            writer2.write("OKI");
            writer2.flush();
            writer2.close();
            System.out.println("Output completed");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static String[] getHostPort(String line, String delimeter) {

        String url = line.split(delimeter)[1].split(" ")[1];
        String[] path = url.split(":");
        String host = path[0].trim();
//        String host = url;
        String port = "80";
        if (path.length >= 2) {
            port = path[1];
        }
        return new String[]{host, port};
    }
}
