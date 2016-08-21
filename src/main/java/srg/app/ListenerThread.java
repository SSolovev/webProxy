package srg.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Sergey on 27.07.2016.
 */
public class ListenerThread implements Runnable {
    public BlockingQueue<MessageWithResponse> queue;

    public ListenerThread(BlockingQueue<MessageWithResponse> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        System.out.println("Waiting....");
//        ServerSocket ss=null;
        try (ServerSocket ss = new ServerSocket(5555);) {
            while (true) {
                try (
                        Socket s = ss.accept();
                        InputStreamReader ir = new InputStreamReader(s.getInputStream());
//             BufferedReader br = new BufferedReader(ir);
                ) {

                    System.out.println("Connected");
                    System.out.println("Reading request from browser..");


                    StringBuilder sb = new StringBuilder();
//                while (br.ready()){
//                    sb.append(br.readLine()).append("\n");
//                }
                    char[] buffer = new char[10];
                    while (ir.ready()) {
                        ir.read(buffer);
                        sb.append(buffer);
                    }

//                final List<String> lines = new ArrayList<>();
//                br.lines().forEach(item -> {
//                    System.out.println(item);
//                    lines.add(item);
//                });
                    if (sb.length() > 0) {
                        System.out.println("READ: -=" + sb.toString() + "=-");
                        System.out.println("Put data into queue..");
                        queue.add(new MessageWithResponse(sb.toString(),s.getOutputStream()));
                        System.out.println("End");
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
        }


    }
}
