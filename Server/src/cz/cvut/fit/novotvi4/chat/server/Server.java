package cz.cvut.fit.novotvi4.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author viky
 */
public class Server implements Runnable {

    private static final int TIMEOUT = 500;
    private ServerSocket serverSocket;

    public Server() {
        try {
            serverSocket = new ServerSocket(1128);
            serverSocket.setSoTimeout(TIMEOUT);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "Cannot create server socket", ex);
        }
    }

    @Override
    public void run() {
        try {
            Socket clientSocket;
            while (!Thread.interrupted()) {
                try {
                    clientSocket = serverSocket.accept();
                } catch (SocketTimeoutException ex) {
                    continue;
                }
                try {
                    (new Thread(new ServerWorker(clientSocket))).start();
                    Logger.getLogger(Server.class.getName()).log(Level.INFO, "Spawning worker");
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "Cannot spawn worker", ex);
                }
            }
            Logger.getLogger(Server.class.getName()).log(Level.INFO, "Server stoped (Thread interupted)");
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "Cannot create worker socket", ex);
        }
    }

    public static void main(String[] args) {
        Thread server = new Thread(new Server());
        server.start();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Chat cmd > ");
            String input = scanner.nextLine();
            if (input.equals("stop")) {
                server.interrupt();
                Logger.getLogger(Server.class.getName()).log(Level.INFO, "Stoping server");
                break;
            } else {
                System.out.println("write \"stop\" to to stop server");
            }
        }
    }
}
