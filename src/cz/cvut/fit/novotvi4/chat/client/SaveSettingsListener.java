package cz.cvut.fit.novotvi4.chat.client;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *
 * @author viky
 */
public class SaveSettingsListener implements ActionListener {

    private JTabbedPane tabbedPane;
    private Client clientInstance;
    private Thread clientThread;

    SaveSettingsListener(JTabbedPane tabbedPane, Client clientInstance, Thread clientThread) {
        this.tabbedPane = tabbedPane;
        this.clientInstance = clientInstance;
        this.clientThread = clientThread;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Component[] components = ((JPanel) tabbedPane.getComponentAt(0)).getComponents();
        for (Component component : components) {
            System.out.println(component);
        }
        JTextField nickField = (JTextField) components[3];
        JTextField serverField = (JTextField) components[4];
        JTextField portField = (JTextField) components[5];
        //check form
        Settings settings;
        if (clientInstance != null) {
            settings = clientInstance.getSettings();
        } else {
            settings = new Settings();
        }
        //save
        settings.setPort(Integer.parseInt(portField.getText()));
        settings.setAddress(serverField.getText());
        settings.setNick(nickField.getText());
        try {
            if (clientInstance != null) {
                clientInstance.stop();
                tabbedPane.remove(1);
            }
            settings.writeProperties();
            //clientInstance.reload();
            clientInstance = new Client(null);
            clientThread = new Thread(clientInstance);
            clientThread.start();

            JComponent chatPanel = ChatPanelFactory.getComponent(clientInstance);
            tabbedPane.addTab("Chat", null, chatPanel, "Chat window");
            tabbedPane.setSelectedIndex(1);
            SwingUtilities.invokeLater(() -> {
                tabbedPane.repaint();
            });

        } catch (IOException ex) {
            Logger.getLogger(ClientPanel.class.getName()).log(Level.SEVERE, "Cannot reload the client after settings change,", ex);
        }
    }

}
