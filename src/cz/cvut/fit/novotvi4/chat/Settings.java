package cz.cvut.fit.novotvi4.chat;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author viky
 */
public abstract class Settings {

    private Properties props;

    public Settings() {
        loadSettings();
    }

    private void loadSettings() {
        Properties defaultProps = new Properties();
        try (FileInputStream in = new FileInputStream("defaultProperties")) {
            defaultProps.load(in);
        } catch (IOException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.WARNING, "No default properties file, loading harcoded one.", ex);
            defaultProps = loadHarcodedProperties();
            try {
                writeCustomProperties(defaultProps, "defaultProperties");
            } catch (IOException ex1) {
                Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, "Can not save default properties - no permission to write on disk", ex1);
            }
        }
        props = new Properties(defaultProps);

        try (FileInputStream in = new FileInputStream("properties")) {
            props.load(in);
            in.close();
        } catch (IOException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.INFO, "Custom settings not found - that's usually ok.");
        }
    }

    public int getPort() {
        return Integer.parseInt(props.getProperty("port"));
    }

    public void setPort(int portNumber) {
        props.setProperty("port", Integer.toString(portNumber));
    }

    public String getAddress() {
        return props.getProperty("address");
    }

    /**
     * store settings on disk
     *
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public void writeProperties() throws FileNotFoundException, IOException {
        writeCustomProperties(props,"properties");
    }

    /**
     * store settings on disk
     *
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    private void writeCustomProperties(Properties prop, String filename) throws FileNotFoundException, IOException {
        try (FileOutputStream out = new FileOutputStream(filename)) {
            prop.store(out, "--- Chat settings ---");

        }
    }

    private Properties loadHarcodedProperties() {
        Properties ps = new Properties();
        ps.setProperty("address", "localhost");
        ps.setProperty("port", "1128");
        return ps;
    }
}
