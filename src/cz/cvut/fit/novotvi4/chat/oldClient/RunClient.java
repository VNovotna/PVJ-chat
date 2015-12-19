package cz.cvut.fit.novotvi4.chat.oldClient;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author viky
 */
public class RunClient {

    public static void main(String[] args) {

        try {
            Client client = new Client("localhost", 1128, null);
            Thread ct = new Thread(client);
            ct.start();
            client.sendMessage("Ahoj");
            client.sendMessage("Ahoj1");
            client.sendMessage("Ahoj2");
            client.sendMessage("Ahoj3");
            client.sendMessage("Ahoj4");
        } catch (IOException ex) {
            Logger.getLogger(RunClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
