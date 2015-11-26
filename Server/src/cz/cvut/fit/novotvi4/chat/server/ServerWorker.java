package cz.cvut.fit.novotvi4.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author viky
 */
public class ServerWorker implements Runnable {

    private Socket socket;
    private boolean clientAlive;
    private BufferedReader input;
    public PrintWriter output;

    public ServerWorker(Socket s) throws IOException {
        this.clientAlive = true;
        this.socket = s;
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(socket.getOutputStream(), true);
    }

    private void ping() {
        output.println("ping");
        try {
            String ans = input.readLine();
            if (ans == null) {
                Logger.getLogger(ServerWorker.class.getName()).log(Level.INFO, "cannot ping");
                clientAlive = false;
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerWorker.class.getName()).log(Level.INFO, "cannot ping", ex);
            clientAlive = false;
        }

    }
    
    private void randomMessage(){
        output.println(new Date(System.currentTimeMillis()).toLocaleString() +"Server is fine.");
    }

    @Override
    public void run() {
        while (clientAlive) {
            ping();
            try {
                String r = input.readLine();
                System.out.println("Server echoing: " + r);
                output.println(r);
            } catch (IOException ex) {
                System.out.println("exception socket state:" + socket.isInputShutdown() + socket.isOutputShutdown() + socket.isClosed());
                Logger.getLogger(ServerWorker.class.getName()).log(Level.SEVERE, "cannot respond", ex);
                break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                break;
            }
        }
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerWorker.class.getName()).log(Level.SEVERE, "Cannot close socket", ex);
        }
        Logger.getLogger(Server.class.getName()).log(Level.INFO, "Worker ends");
    }
}
