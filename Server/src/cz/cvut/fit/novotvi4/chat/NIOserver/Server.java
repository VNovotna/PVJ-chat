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
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.MalformedInputException;
import java.util.HashMap;
import java.util.Map;
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
    private ByteBuffer writeBuffer;
    private ByteBuffer readBuffer;
    private Map<SocketChannel, String> clients;

    /**
     * @throws IOException
     */
    public void init() throws IOException {
        ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        ssc.socket().bind(new InetSocketAddress("localhost", PORT));
        decoder = Charset.forName("UTF-8").newDecoder();
        writeBuffer = ByteBuffer.allocateDirect(256);
        readBuffer = ByteBuffer.allocateDirect(256);
        clients = new HashMap<>();
    }

    /**
     * fills the buffer from the given string and prepares it for a channel
     * write
     *
     * @param msg
     */
    private void prepWriteBuffer(String msg) {
        writeBuffer.clear();
        writeBuffer.put(msg.getBytes());
        writeBuffer.putChar('\n');
        writeBuffer.flip();
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
        channelWrite(channel, writeBuffer);
    }

    private void broadcastMessage(String msg, SocketChannel from) throws IOException {
        prepWriteBuffer(msg);
        for (SelectionKey key : selector.keys()) {
            if (key.isValid() && key.channel() instanceof SocketChannel) {
                SocketChannel sch = (SocketChannel) key.channel();
                if (from == null || sch != from) {
                    channelWrite(sch, writeBuffer);
                }
            }
        }
    }

    private void acceptNewConnections() {
        try {
            SocketChannel clientChannel;
            while ((clientChannel = ssc.accept()) != null) {//non-blocking
                registerNewClient(clientChannel);
                sendMessage(clientChannel, "\n\nWelcome , there are " + clients.size() + " users online.\n");
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.WARNING, "Cannot accept", ex);
        }
    }

    /**
     * @param client
     * @throws IOException
     */
    private void registerNewClient(SocketChannel client) throws IOException {
        Logger.getLogger(ServerWorker.class.getName()).log(Level.INFO, "New client on {0}", client.getRemoteAddress());
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ, new StringBuffer());
        clients.put(client, client.getRemoteAddress().toString());
        broadcastMessage("New user " + clients.get(client) + " connected\n", client);
    }

    private void readIncomingMessages() {
        try {
            selector.selectNow();
            Set<SelectionKey> keys = selector.selectedKeys();
            for (SelectionKey key : keys) {
                SocketChannel chan = (SocketChannel) key.channel();
                long read = -1;
                try {
                    readBuffer.clear();
                    read = chan.read(readBuffer);
                } catch (IOException ex) {
                    /*klient může poslat data na server a umřít dřív než server stihne request zpracovat*/
                }
                if (read == -1) { //end of connection
                    Logger.getLogger(ServerWorker.class.getName()).log(Level.INFO, "Lost contact with client {0}", chan.getRemoteAddress());
                    broadcastMessage("User " + chan + " leaved\n", null);
                    clients.remove(chan);
                    key.cancel();
                    chan.close();
                    return;
                }
                StringBuffer sb = (StringBuffer) key.attachment();
                readBuffer.flip();
                String str = decoder.decode(readBuffer).toString();
                readBuffer.clear();
                sb.append(str);
                // check for a full line
                String line = sb.toString();
                if ((line.contains("\n")) || (line.contains("\r"))) {
                    line = line.trim();
                    System.out.println("broadcasting: " + line);
                    broadcastMessage(clients.get(chan) + ": " + line, chan);
                    sb.delete(0, sb.length());

                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }

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
        while (ssc.isOpen() || !Thread.interrupted()) {
            acceptNewConnections();
            readIncomingMessages();
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.WARNING, "Server loop interupted", ex);
                
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
