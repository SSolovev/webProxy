package srg.app;

import net.pms.network.ProxyServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException {

//        Executors.newFixedThreadPool(1);


        ExecutorService service = Executors.newFixedThreadPool(1);
//
        ServerSocket serverSocketFromBrowser = new ServerSocket(5555);
        while (true) {
            Socket s = serverSocketFromBrowser.accept();
            TestThread testThread = new TestThread(s);
            service.execute(testThread);

        }

        //ProxyServer p = new ProxyServer(5555);
//        p.start();
//        service.execute(listenerThread);
//        service.execute(consumerThread);
    }


}
