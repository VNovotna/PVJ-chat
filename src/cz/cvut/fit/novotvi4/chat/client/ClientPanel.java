package cz.cvut.fit.novotvi4.chat.client;

import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author viky
 */
public class ClientPanel extends JPanel {

    private final String title;

    ClientPanel(String title, Client clientInstance, Thread clientThread) {
        super(new GridLayout(1, 1));
        this.title = title;
        setVisible(true);

        JTabbedPane tabbedPane = new JTabbedPane();

        int tabsCnt = 0;

        JComponent settingsPanel = SettingsPanelFactory.getComponent(clientInstance, clientThread, tabbedPane);
        tabbedPane.addTab("Settings", null, settingsPanel, "Settings");
        tabbedPane.setMnemonicAt(tabsCnt, KeyEvent.VK_F2);
        tabbedPane.setSelectedIndex(tabsCnt++);

        if (clientInstance != null) {
            JComponent chatPanel = ChatPanelFactory.getComponent(clientInstance);
            tabbedPane.addTab("Chat", null, chatPanel, "Chat window");
            tabbedPane.setSelectedIndex(tabsCnt);
            tabbedPane.setMnemonicAt(tabsCnt++, KeyEvent.VK_F1);
        }
        add(tabbedPane);

        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    }

    public String getTitle() {
        return title;
    }

}
