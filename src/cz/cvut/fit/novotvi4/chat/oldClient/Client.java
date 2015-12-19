package cz.cvut.fit.novotvi4.chat.oldClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author viky
 */
public class Client implements Runnable {

    private Socket socket;
    private javax.swing.JEditorPane chatWindow;
    private BufferedReader input;
    private PrintWriter output;

    public Client(String address, int port, javax.swing.JEditorPane chatWindow) throws IOException {
        this.socket = new Socket(address, port);
        this.chatWindow = chatWindow;

        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(socket.getOutputStream(), true);
    }

    public void sendMessage(String text) {
        output.println(text);
    }

    @Override
    public void run() {
        Logger.getLogger(Client.class.getName()).log(Level.INFO, "Starting client thread");
        try {
            while (!Thread.interrupted()) {
                String response = input.readLine();
                if (response != null) {
                    if(response.equals("ping")){
                        output.println("cink");
                        continue;
                    }
                    System.out.println("Recieved: " + response);
                    if (!response.equals("") && chatWindow != null) {
                        final String shit = chatWindow.getText()+ "\n" + response;
                        SwingUtilities.invokeLater(() -> {
                            chatWindow.setText(shit);
                        });
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    break;
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.WARNING, "Cannot read", ex);
        } finally {
            try {
                stop();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, "Cannot close streams", ex);
            }
        }
        Logger.getLogger(Client.class.getName()).log(Level.INFO, "Stoped client thread");
    }

    public void stop() throws IOException {
        socket.close();
    }
}
