/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.novotvi4.chat.client;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author viky
 */
public class ChatPanelFactory {

    public static JComponent getComponent(Client clientInstance) {

        JPanel panel = new JPanel();

        JComponent editorScrollPane = MessagesAreaFactory.getComponent(clientInstance);

        JTextArea ta = new JTextArea("text");
        ta.setRows(3);

        JScrollPane editorScrollPane2 = new JScrollPane(ta);
        editorScrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        editorScrollPane2.setMinimumSize(new Dimension(10, 10));

        JButton jl = new JButton("Send");

        jl.addActionListener((ActionEvent e) -> {
            System.out.println("Send buttton clicked: " + ta.getText());
            clientInstance.sendMessage(ta.getText());
            ta.setText("");
        });

        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = 10;

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 10;
        c.weightx = 0;
        c.weighty = 0.9;
        panel.add(editorScrollPane, c);

        c.gridwidth = 1;
        c.gridy = 1;
        c.weightx = 0.9;
        c.weighty = 0.1;
        panel.add(editorScrollPane2, c);

        c.gridx = 1;
        c.weightx = 0.1;
        c.gridwidth = 1;
        panel.add(jl, c);

        return panel;
    }
}
