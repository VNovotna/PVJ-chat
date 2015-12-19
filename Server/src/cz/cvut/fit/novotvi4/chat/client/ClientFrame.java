package cz.cvut.fit.novotvi4.chat.client;

import javax.swing.JFrame;

/**
 *
 * @author viky
 */
public class ClientFrame extends JFrame {

    public ClientFrame(ClientPanel panel) {
        setTitle(panel.getTitle());
        setLocationByPlatform(true);
        add(panel);
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
