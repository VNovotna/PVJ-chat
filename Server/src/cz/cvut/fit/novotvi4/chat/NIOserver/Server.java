/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.novotvi4.chat.NIOserver;

import cz.cvut.fit.novotvi4.chat.server.ServerWorker;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author viky
 */
public class Server extends Thread {

    public static final int PORT = 1128;

    /**
     * Univerzální odpoveď
     */
    public static final String RESPONSE = "HTTP/1.0 200 OK\r\n"
            + "Connection: closed\r\n"
            + "Content-Type: text/plain\r\n"
            + "Content-length: 20\r\n"
            + "\r\n"
            + "Simple HTTP server.\n";

    private ServerSocketChannel ssc;
    private Selector selector;
    private CharsetDecoder decoder;
    private ByteBuffer buffer;

    /**
     * @throws IOException
     */
    public void init() throws IOException {
        ssc = ServerSocketChannel.open(); //otevřít serverový kanál
        ssc.configureBlocking(false); //vypnout blokování
        selector = Selector.open(); //otevřít selektor
        ssc.register(selector, SelectionKey.OP_ACCEPT); //přidat kanál do selektoru
        ssc.socket().bind(new InetSocketAddress("localhost", PORT)); //připojit serverový soket
        decoder = Charset.forName("UTF-8").newDecoder(); //vytvořit dekóder pro ASCII
        buffer = ByteBuffer.allocateDirect(512); //alokovat bajtový buffer
    }

    /**
     * fills the buffer from the given string and prepares it for a channel
     * write
     *
     * @param msg
     */
    private void prepWriteBuffer(String msg) {
        buffer.clear();
        buffer.put(msg.getBytes());
        buffer.putChar('\n');
        buffer.flip();
    }

    private void channelWrite(SocketChannel channel, ByteBuffer writeBuffer) throws IOException {
        long nbytes = 0;
        long toWrite = writeBuffer.remaining();
        while (nbytes != toWrite) {
            nbytes += channel.write(writeBuffer);
        }
        writeBuffer.rewind();
    }

    private void sendMessage(SocketChannel channel, String mesg) throws IOException {
        prepWriteBuffer(mesg);
        channelWrite(channel, buffer);
    }

    private void broadcastMessage(String msg, SocketChannel from) throws IOException {
        ByteBuffer msgBuf = ByteBuffer.wrap(msg.getBytes());
        for (SelectionKey key : selector.keys()) {
            if (key.isValid() && key.channel() instanceof SocketChannel) {
                SocketChannel sch = (SocketChannel) key.channel();
                if (from == null || sch != from) {
                    channelWrite(sch, msgBuf);
                }
            }
        }
    }

    /**
     * @param client
     * @throws IOException
     */
    private void registerNewClient(SocketChannel client) throws IOException {
        if (client != null) { //pokud se někdo opravdu připojil
            Logger.getLogger(ServerWorker.class.getName()).log(Level.INFO, "New client on {0}", client.getRemoteAddress());
            client.configureBlocking(false); //vypnout blokování
            client.register(selector, SelectionKey.OP_READ, new StringBuffer()); //a přidat do selektoru
            broadcastMessage("New client " + client.getRemoteAddress() + " connected\n",null);
        }
    }

    private String readInput(SelectionKey key) throws CharacterCodingException {
        StringBuffer sb = (StringBuffer) key.attachment();
        buffer.flip();
        sb.append(decoder.decode(buffer).toString());
        buffer.clear();
        if (sb.indexOf("\r\n\r\n") != -1) {
            return sb.toString().trim();
        }
        return "bad input";
    }

    private void processInput(Channel chan, SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) chan;
        int read = -1;
        try {
            read = client.read(buffer);
        } catch (IOException ex) {
            /*klient může poslat data na server a umřít dřív než server stihne request zpracovat*/
        }
        if (read == -1) { //end of connection
            Logger.getLogger(ServerWorker.class.getName()).log(Level.INFO, "Lost contact with client {0}", client.getRemoteAddress());
            key.cancel();
            client.close();
            return;
        }
        String message = readInput(key);
        System.out.println(message.trim());

        sendMessage(client, RESPONSE);

    }

    @Override
    public void run() {
        try {
            init();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "Cannot init server", ex);
            return;
        }
        (new InputThread(this)).start();
        while (ssc.isOpen()) {
            try {
                selector.select(); //block until event
                Set<SelectionKey> keys = selector.selectedKeys();
                for (SelectionKey key : keys) {
                    Channel chan = key.channel(); //získat kanál
                    if (chan == ssc) { //new client
                        registerNewClient(ssc.accept());
                    } else { //known client
                        processInput(chan, key);
                    }
                }
            } catch (IOException | ClosedSelectorException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.WARNING, "Server encounterd an exception or shutdown.");
            }
        }
        try {
            if (ssc != null) {
                ssc.socket().close();
                ssc.close();
            }
            if (selector != null) {
                selector.close();
            }
        } catch (IOException e) {
        }

    }

    public static void main(String[] args) {
        new Server().start();
    }

    private void shutdown() {
        try {
            if (ssc != null) {
                ssc.socket().close();
                ssc.close();
            }
            if (selector != null) {
                selector.close();
            }
        } catch (IOException e) {
        }
    }

    class InputThread extends Thread {

        private Server cc;
        private boolean running;

        public InputThread(Server cc) {
            this.cc = cc;
        }

        @Override
        public void run() {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            running = true;
            while (running) {
                try {
                    String s;
                    System.out.print("> ");
                    System.out.flush();
                    s = br.readLine();
                    if (s.equals("quit")) {
                        running = false;
                    }

                } catch (IOException ioe) {
                    running = false;
                }
            }
            cc.shutdown();
        }

        public void shutdown() {
            running = false;
            interrupt();
        }
    }
}
