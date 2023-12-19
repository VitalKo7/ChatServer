package chat.server;

import chat.server.task.ChatServerReceiver;
import chat.server.task.ChatServerSender;
import mediation.BlkQueueImpl;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ChatServerAppl {
    public static void main(String[] args) {
        int port = 9000;
        BlkQueueImpl<String> messageBox = new BlkQueueImpl(100_000);

//        ExecutorService executorService = Executors.newFixedThreadPool(10);
//        int poolSize = Runtime.getRuntime().availableProcessors();
//        ExecutorService executorService = Executors.newFixedThreadPool(poolSize);
        ExecutorService executorService = Executors.newWorkStealingPool();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            try {
                while (true) {
                    System.out.println("Server waits...");
                    Socket socket = serverSocket.accept();
                    System.out.println("Connection established");
                    System.out.println("Client host: " + socket.getInetAddress() + ":" + socket.getPort());
                    // Client host: /127.0.0.1:49755 || Client host: /127.0.0.1:49758 || Client host: /127.0.0.1:49763

                    Thread sender = new Thread(new ChatServerSender(socket, messageBox));
                    sender.setDaemon(true);
                    sender.start();

                    ChatServerReceiver receiver = new ChatServerReceiver(socket, messageBox);
                    executorService.execute(receiver);
                }
            } finally {
                executorService.shutdown();
                executorService.awaitTermination(1, TimeUnit.MINUTES);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}