
package cz.cvut.fit.novotvi4.chat.client;

import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author viky
 */
public class MessagesAreaFactory {
    public static JComponent getComponent(Client clientInstance){
        JEditorPane pane = new JEditorPane();
        pane.setEditable(false);
        pane.setContentType("text/html");
        pane.setText("<b>Vítejte</b><br>");
        clientInstance.setChatWindow(pane);

        JScrollPane editorScrollPane = new JScrollPane(pane);
        editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        editorScrollPane.setPreferredSize(new Dimension(250, 145));
        editorScrollPane.setMinimumSize(new Dimension(10, 10));

        JScrollBar scrollBar = editorScrollPane.getVerticalScrollBar();
        DefaultCaret caret = (DefaultCaret) pane.getCaret();
        scrollBar.addAdjustmentListener((AdjustmentEvent e) -> {
            caret.setDot(pane.getDocument().getLength());
            caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        });
        return editorScrollPane;
    }
}
