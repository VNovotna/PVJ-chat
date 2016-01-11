package cz.cvut.fit.novotvi4.chat.client;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author viky
 */
public class ClientUI {

    private static Client clientInstance;
    private static Thread clientThread;

    private static void createFrame() {
        JFrame f = new ClientFrame(new ClientPanel("Chat", clientInstance));
        f.pack();
        SwingUtilities.invokeLater(() -> {
            f.setVisible(true);
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    clientInstance.stop();
                } catch (IOException ex) {
                    Logger.getLogger(ClientUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(ClientUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            clientInstance = new Client(null);
            clientThread = new Thread(clientInstance);
            createFrame();
            clientThread.start();
        } catch (IOException ex) {
            Logger.getLogger(ClientUI.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
    }
}
