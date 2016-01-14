/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.novotvi4.chat.client;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 *
 * @author viky
 */
public class PortNumberVerifier extends InputVerifier {

    private JTextField field;

    public PortNumberVerifier(JTextField field) {
        this.field = field;
    }

    @Override
    public boolean verify(JComponent input) {
        try {
            int port = Integer.parseInt(((JTextField) input).getText());
            return port > 0 && port < 65536;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    @Override
    public boolean shouldYieldFocus(JComponent input) {
        if (!verify(input)) {
            field.selectAll();
            return false;
        }
        return true;
    }
}
