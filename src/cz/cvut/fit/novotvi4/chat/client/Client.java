package cz.cvut.fit.novotvi4.chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

/**
 * Listening thread and message sender 
 * @author viky
 */
public class Client implements Runnable {

    private Socket socket;
    private JEditorPane chatWindow;
    private BufferedReader input;
    private PrintWriter output;
    private Settings settings;

    public Client(JEditorPane chatWindow) throws IOException {
        this.chatWindow = chatWindow;
        init();
    }

    /**
     * @param text 
     */
    public void sendMessage(String text) {
        output.println(text);
    }

    /**
     * send login of user to the server, so the user won't by identified by port
     * @param login 
     */
    public void sendLogin(String login) {
        output.println("/login=" + login);
    }

    public void setChatWindow(JEditorPane jp) {
        chatWindow = jp;
    }

    /**
     * write some HTML to text area
     * @param incomingStr 
     */
    private void processIncomingMessage(String incomingStr) {
        HTMLEditorKit kit = new HTMLEditorKit();
        HTMLDocument doc = (HTMLDocument) chatWindow.getDocument();
        chatWindow.setEditorKit(kit);
        chatWindow.setDocument(doc);
        try {
            kit.insertHTML(doc, doc.getLength(), incomingStr, 0, 0, null);
        } catch (BadLocationException | IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, "Can't set chatWindow text", ex);
        }

    }

    @Override
    public void run() {
        Logger.getLogger(Client.class.getName()).log(Level.INFO, "Starting client thread");
        try {
            while (!Thread.interrupted()) {
                String response = input.readLine();
                if (response != null) {
                    if (response.equals("ping")) {
                        output.println("cink");
                        continue;
                    }
                    System.out.println("Recieved: " + response);

                    if (!response.equals("") && chatWindow != null) {
                        SwingUtilities.invokeLater(() -> {
                            processIncomingMessage(response);
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

    /**
     * closes settings and socket - thus the tread die 
     * @throws IOException 
     */
    public void stop() throws IOException {
        settings.writeProperties();
        socket.close();
    }

    private void init() throws IOException {
        settings = new Settings();
        try {
            socket = new Socket(settings.getAddress(), settings.getPort());

            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
        } catch (ConnectException ex) {
            throw ex;
        }
    }

    public void reload() throws IOException {
        init();
    }

    public Settings getSettings() {
        return settings;
    }
}
