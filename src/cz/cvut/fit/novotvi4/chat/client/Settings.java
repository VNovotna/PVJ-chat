package cz.cvut.fit.novotvi4.chat.client;

/**
 *
 * @author viky
 */
public class Settings extends cz.cvut.fit.novotvi4.chat.Settings {

    public String getNick() {
       return  props.getProperty("nick");
    }

    public void setNick(String text) {
        props.setProperty("nick", text);
    }

}
