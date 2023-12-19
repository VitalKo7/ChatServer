package chat.server.task;

import mediation.BlkQueue;
import mediation.BlkQueueImpl;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ChatServerSender implements Runnable {

    private Socket socket;

    private BlkQueue messageBox;

    private List<Socket> clientsList = new ArrayList<Socket>();

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public ChatServerSender(Socket socket, BlkQueueImpl<String> messageBox) {
        this.socket = socket;
        this.messageBox = messageBox;
    }

    @Override
    public void run() {
        try {                                           // чистый трай - фоновый процесс
            PrintWriter socketWriter;
            String messageNew = messageBox.pop().toString();    //? not sure: Object -> String ?

            clientsList.add(socket);    //? separate method?

            for (Socket socket : clientsList) {
                socketWriter = new PrintWriter(socket.getOutputStream());
                socketWriter.println(LocalTime.now().format(formatter) + ": " + messageNew);
                socketWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}