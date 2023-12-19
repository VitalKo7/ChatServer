package chat.server.task;

import mediation.BlkQueue;
import mediation.BlkQueueImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ChatServerReceiver implements Runnable {
    private Socket socket;
    private BlkQueue messageBox;

    public ChatServerReceiver(Socket socket, BlkQueueImpl<String> messageBox) {
        this.socket = socket;
        this.messageBox = messageBox;
    }

    @Override
    public void run() {
        try (Socket socket = this.socket) {
            BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (true) {
                String message = socketReader.readLine();   // gets msgs from client
                if (message == null) {
                    break;
                }
                System.out.println("Server received: " + message);

                messageBox.push(message);   // puts into BlkQueue: push()
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}