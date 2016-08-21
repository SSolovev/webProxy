package srg.app;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException {
        BlockingQueue<MessageWithResponse> queue = new LinkedBlockingDeque<>();
        ConsumerThread consumerThread = new ConsumerThread(queue);
        ListenerThread listenerThread = new ListenerThread(queue);

        ExecutorService service = Executors.newFixedThreadPool(2);

        ServerSocket serverSocketFromBrowser = new ServerSocket(5555);
        while (true) {
            Socket s = serverSocketFromBrowser.accept();
            TestThread testThread = new TestThread(s);
            service.execute(testThread);
        }


//        service.execute(listenerThread);
//        service.execute(consumerThread);
    }


}
