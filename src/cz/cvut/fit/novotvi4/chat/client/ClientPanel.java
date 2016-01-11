package cz.cvut.fit.novotvi4.chat.client;

import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author viky
 */
public class ClientPanel extends JPanel {

    private final String title;
    private Client clientInstance;

    ClientPanel(String title, Client clientInstance) {
        super(new GridLayout(1, 1));
        this.title = title;
        this.clientInstance = clientInstance;
        setVisible(true);

        JTabbedPane tabbedPane = new JTabbedPane();

        JComponent chatPanel = ChatPanelFactory.getComponent(clientInstance);
        tabbedPane.addTab("Chat", null, chatPanel, "Chat window");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_F1);
        tabbedPane.setSelectedIndex(0);

        JComponent settingsPanel = makeSettingsPanel("Panel #2");
        tabbedPane.addTab("Settings", null, settingsPanel, "Settings");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_F2);

        add(tabbedPane);

        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    }

    private JComponent makeSettingsPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }

    public String getTitle() {
        return title;
    }

}
