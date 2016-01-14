package cz.cvut.fit.novotvi4.chat.client;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

/**
 *
 * @author viky
 */
public class SettingsPanelFactory {

    public static JComponent getComponent(Client clientInstance, Thread clientThread, JTabbedPane tabbedPane) {
        Settings settings;
        if (clientInstance != null) {
            settings = clientInstance.getSettings();
        } else {
            settings = new Settings();
        }
        JPanel p = new JPanel();

        GroupLayout layout = new GroupLayout(p);
        p.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        JLabel nickLabel = new JLabel("nick:", JLabel.TRAILING);
        JTextField nickField = new JTextField(20);
        nickField.setToolTipText("alfanumeric symbols only");
        nickLabel.setLabelFor(nickField);
        nickField.setText(settings.getNick());

        JLabel serverLabel = new JLabel("server:", JLabel.TRAILING);
        JTextField serverField = new JTextField();
        serverField.setToolTipText("alfanumeric symbols only");
        serverLabel.setLabelFor(nickField);
        serverField.setText(settings.getAddress());

        JLabel portLabel = new JLabel("port:", JLabel.TRAILING);
        JTextField portField = new JTextField(5);
        portField.setToolTipText("valid port numbers only");
        portField.setInputVerifier(new PortNumberVerifier(portField));
        portLabel.setLabelFor(portField);
        portField.setText(Integer.toString(settings.getPort()));

        JButton submitBtn = new JButton("Save");
        submitBtn.addActionListener(new SaveSettingsListener(tabbedPane, clientInstance,clientThread));

        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

        // Variable indentation is used to reinforce the level of grouping.
        hGroup.addGroup(layout.createParallelGroup().
                addComponent(nickLabel).addComponent(serverLabel).addComponent(portLabel));
        hGroup.addGroup(layout.createParallelGroup().
                addComponent(nickField).addComponent(serverField).addComponent(portField).addComponent(submitBtn));
        layout.setHorizontalGroup(hGroup);

        // Create a sequential group for the vertical axis.
        GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).
                addComponent(nickLabel).addComponent(nickField));
        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).
                addComponent(serverLabel).addComponent(serverField));
        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).
                addComponent(portLabel).addComponent(portField));
        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).
                addComponent(submitBtn));
        layout.setVerticalGroup(vGroup);

        return p;
    }
}
